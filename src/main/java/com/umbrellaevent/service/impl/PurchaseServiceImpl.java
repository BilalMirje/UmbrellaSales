package com.umbrellaevent.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.umbrellaevent.config.AmountUtils;
import com.umbrellaevent.entity.Material;
import com.umbrellaevent.entity.ProductStock;
import com.umbrellaevent.entity.Purchase;
import com.umbrellaevent.entity.Supplier;
import com.umbrellaevent.entity.SupplierBill;
import com.umbrellaevent.entity.SupplierCredit;
import com.umbrellaevent.entity.dtos.material.MaterialResponse;
import com.umbrellaevent.entity.dtos.purchase.ProductStockRequest;
import com.umbrellaevent.entity.dtos.purchase.ProductStockResponse;
import com.umbrellaevent.entity.dtos.purchase.ProductStockWithPurchaseResponse;
import com.umbrellaevent.entity.dtos.purchase.PurchaseRequest;
import com.umbrellaevent.entity.dtos.purchase.PurchaseResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierBillResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierResponse;
import com.umbrellaevent.repository.ProductStockRepository;
import com.umbrellaevent.repository.PurchaseRepository;
import com.umbrellaevent.repository.SupplierCreditRepository;
import com.umbrellaevent.repository.SupplierRepository;
import com.umbrellaevent.service.PurchaseService;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;
    private final SupplierCreditRepository supplierCreditRepository;
    private final ProductStockRepository productStockRepository;
    private final com.umbrellaevent.repository.MaterialRepository materialRepository;
    private final com.umbrellaevent.repository.SupplierBillRepository supplierBillRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    @Transactional
    public PurchaseResponse createPurchase(PurchaseRequest request, String username) {
        System.out.println("SUPPLIER====>"+request.toString());
        if (request.getSupplierId() == null) {
            throw new IllegalArgumentException("Supplier ID is required");
        }

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // Material handling logic
        Material material;
        if (request.getMaterialId() != null) {
            material = materialRepository.findById(request.getMaterialId())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
        } else if (request.getMaterialName() != null && !request.getMaterialName().trim().isEmpty()) {
            material = materialRepository.findByName(request.getMaterialName())
                    .orElseGet(() -> {
                        Material newMaterial = new Material();
                        newMaterial.setName(request.getMaterialName());
                        return materialRepository.save(newMaterial);
                    });
        } else {
            throw new RuntimeException("Either materialId or materialName must be provided");
        }

        // Convert request → entity
        List<ProductStock> productStocks = request.getProductStocks()
                .stream()
                .map(this::mapToProductStock)
                .collect(Collectors.toList());

        List<ProductStock> savedProductStocks = productStockRepository.saveAll(productStocks);

        String invoiceNo = request.getInvoiceNo();

        // Update supplier credit
        // creditAmount = amount we have to give to the supplier (how much we still owe)
        // debitAmount = amount we will receive from the supplier (how much they still have to pay us)
        // toPay = amount we are actually going to pay to the supplier (from total creditAmount)
        SupplierCreditRequest creditRequest = request.getSupplierCredit();
        SupplierCredit existingCredit = supplier.getSupplierCredit();

        existingCredit.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(
                existingCredit.getTotalAmount() + creditRequest.getTotalAmount()
        ));
        existingCredit.setPaid(AmountUtils.roundToTwoDecimalPlaces(
                existingCredit.getPaid() + creditRequest.getPaid()
        ));
        existingCredit.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(
                existingCredit.getCreditAmount() + creditRequest.getCreditAmount()
        ));
        existingCredit.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(
                existingCredit.getDebitAmount() + creditRequest.getDebitAmount()
        ));
        existingCredit.setToPay(0.0); // Reset toPay to 0 after payment

        SupplierCredit savedSupplierCredit = supplierCreditRepository.save(existingCredit);

        // Create Purchase record
        Purchase purchase = new Purchase();
        purchase.setInvoiceNo(invoiceNo);
        purchase.setProductName(request.getProductName());
        purchase.setProductStocks(savedProductStocks);
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setPurchaseAmount(request.getPurchaseAmount());
        purchase.setTotalPurchaseAmount(request.getTotalPurchaseAmount());
        purchase.setCgst(request.getCgst());
        purchase.setSgst(request.getSgst());
        purchase.setIgst(request.getIgst());
        purchase.setPaymentMode(request.getPaymentMode());
        purchase.setSupplier(supplier);
        purchase.setMaterial(material);

        Purchase savedPurchase = purchaseRepository.save(purchase);

        // SupplierBill history
        SupplierBill history = new SupplierBill();
        history.setInvoiceNo(invoiceNo);
        history.setPurchase(savedPurchase);
        history.setSupplier(supplier);
        history.setToPay(creditRequest.getToPay()); // toPay = amount we are actually going to pay (historical record)
        history.setDebitAmount(creditRequest.getDebitAmount()); // debitAmount (amount we will receive)
        history.setCreditAmount(creditRequest.getCreditAmount());
        history.setPaid(creditRequest.getPaid());
        history.setTotalAmount(creditRequest.getTotalAmount());

        supplierBillRepository.save(history);

        // Increment invoice sequence
        sequenceGeneratorService.getNextNumber("purchase_invoice");

        // After all database operations are successful, upload images to S3
        List<String> uploadedImageUrls = new ArrayList<>();
        try {
            for (int i = 0; i < savedProductStocks.size(); i++) {
                ProductStock savedStock = savedProductStocks.get(i);
                MultipartFile image = request.getProductStocks().get(i).getImage();

                if (image != null && !image.isEmpty()) {
                    String imageUrl = uploadImageToS3(image);  // full URL returned
                    if (imageUrl != null) {
                        savedStock.setImageUrl(imageUrl);
                        uploadedImageUrls.add(imageUrl);
                    } else {
                        savedStock.setImageUrl(null);
                    }
                } else {
                    savedStock.setImageUrl(null);
                }
            }

            // Update ProductStock entities with image URLs
            productStockRepository.saveAll(savedProductStocks);
        } catch (Exception e) {
            // If database update fails, delete uploaded images to maintain consistency
            for (String imageUrl : uploadedImageUrls) {
                deleteImageFromS3(imageUrl);
            }
            throw e; // Re-throw the exception
        }

        return mapToResponse(savedPurchase, savedSupplierCredit);
    }


    @Override
    public PurchaseResponse getPurchaseById(UUID id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
        // Get supplier credit from supplier
        SupplierCredit supplierCredit = purchase.getSupplier().getSupplierCredit();
        return mapToResponse(purchase, supplierCredit);
    }


    @Override
    public PurchaseResponse getPurchaseByInvoiceNo(String invoiceNo) {
        Purchase purchase = purchaseRepository.findByInvoiceNo(invoiceNo)
                .orElseThrow(() -> new RuntimeException("Purchase not found with invoice number: " + invoiceNo));
        // Get supplier credit from supplier
        SupplierCredit supplierCredit = purchase.getSupplier().getSupplierCredit();
        return mapToResponse(purchase, supplierCredit);
    }

    @Override
    public ProductStockResponse getStockByBarcode(String barcode) {
        ProductStock productStock = productStockRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Product stock not found with barcode: " + barcode));
        return mapToProductStockResponse(productStock);
    }

    @Override
    public ProductStockWithPurchaseResponse getStockByBarcodeWithPurchase(String barcode) {
        ProductStock productStock = productStockRepository.findByBarcode(barcode)
                .orElseThrow(() -> new RuntimeException("Product stock not found with barcode: " + barcode));

        // Find the purchase that contains this product stock
        Purchase purchase = purchaseRepository.findAll().stream()
                .filter(p -> p.getProductStocks().contains(productStock))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Purchase not found for product stock"));

        return mapToProductStockWithPurchaseResponse(productStock, purchase);
    }

    @Override
    public List<PurchaseResponse> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(purchase -> {
                    SupplierCredit supplierCredit = purchase.getSupplier().getSupplierCredit();
                    return mapToResponse(purchase, supplierCredit);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<PurchaseResponse> getAllPurchases(Pageable pageable) {
        Page<Purchase> purchases = purchaseRepository.findAll(pageable);

        return purchases.map(purchase -> {
            SupplierCredit credit = purchase.getSupplier().getSupplierCredit();
            return mapToResponse(purchase, credit);
        });
    }



    @Override
    @Transactional
    public PurchaseResponse updatePurchase(UUID id, PurchaseRequest request, String username) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        Supplier oldSupplier = purchase.getSupplier();
        Supplier newSupplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        boolean supplierChanged = !oldSupplier.getId().equals(newSupplier.getId());

        // Handle Material - use existing or create new
        Material material;
        if (request.getMaterialId() != null) {
            material = materialRepository.findById(request.getMaterialId())
                    .orElseThrow(() -> new RuntimeException("Material not found"));
        } else if (request.getMaterialName() != null && !request.getMaterialName().trim().isEmpty()) {
            material = materialRepository.findByName(request.getMaterialName())
                    .orElseGet(() -> {
                        Material newMaterial = new Material();
                        newMaterial.setName(request.getMaterialName());
                        return materialRepository.save(newMaterial);
                    });
        } else {
            throw new RuntimeException("Either materialId or materialName must be provided");
        }

        // Update existing ProductStocks (do not change barcodes)
        List<ProductStock> existingStocks = purchase.getProductStocks();
        List<ProductStockRequest> requestStocks = request.getProductStocks();

        if (existingStocks.size() != requestStocks.size()) {
            throw new RuntimeException("Number of product stocks in request must match existing stocks");
        }

        for (int i = 0; i < existingStocks.size(); i++) {
            ProductStock existingStock = existingStocks.get(i);
            ProductStockRequest stockReq = requestStocks.get(i);

            // Update only non-barcode fields
            existingStock.setColor(stockReq.getColor());
            existingStock.setSize(stockReq.getSize());
            existingStock.setUnit(stockReq.getUnit());
            existingStock.setHsnNo(stockReq.getHsnNo());
            existingStock.setUpdatedBy(username);
            existingStock.setUpdatedAt(LocalDateTime.now());
        }

        List<ProductStock> savedProductStocks = productStockRepository.saveAll(existingStocks);

        // Handle Supplier Credit changes
        SupplierBill oldHistory = supplierBillRepository.findByPurchase(purchase)
                .orElseThrow(() -> new RuntimeException("History record not found for purchase"));

        // Subtract old amounts from old supplier's credit
        // creditAmount = amount we have to give to the supplier (how much we still owe)
        // debitAmount = amount we will receive from the supplier (how much they still have to pay us)
        SupplierCredit oldSupplierCredit = oldSupplier.getSupplierCredit();
        oldSupplierCredit.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(oldSupplierCredit.getTotalAmount() - oldHistory.getTotalAmount()));
        oldSupplierCredit.setPaid(AmountUtils.roundToTwoDecimalPlaces(oldSupplierCredit.getPaid() - oldHistory.getPaid())); // Use paid instead of toPay
        oldSupplierCredit.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(oldSupplierCredit.getCreditAmount() - oldHistory.getCreditAmount()));
        oldSupplierCredit.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(oldSupplierCredit.getDebitAmount() - oldHistory.getDebitAmount()));
        supplierCreditRepository.save(oldSupplierCredit);

        // If supplier changed, transfer to new supplier
        SupplierCredit currentSupplierCredit;
        if (supplierChanged) {
            currentSupplierCredit = newSupplier.getSupplierCredit();
        } else {
            currentSupplierCredit = oldSupplierCredit; // Same supplier
        }

        // Add new amounts to current supplier's credit
        SupplierCreditRequest creditRequest = request.getSupplierCredit();
        currentSupplierCredit.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(currentSupplierCredit.getTotalAmount() + creditRequest.getTotalAmount()));
        currentSupplierCredit.setPaid(AmountUtils.roundToTwoDecimalPlaces(currentSupplierCredit.getPaid() + creditRequest.getPaid()));
        currentSupplierCredit.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(currentSupplierCredit.getCreditAmount() + creditRequest.getCreditAmount()));
        currentSupplierCredit.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(currentSupplierCredit.getDebitAmount() + creditRequest.getDebitAmount()));
        SupplierCredit savedSupplierCredit = supplierCreditRepository.save(currentSupplierCredit);

        // Update history record
        oldHistory.setSupplier(newSupplier); // Update supplier if changed
        oldHistory.setToPay(creditRequest.getToPay()); // toPay = amount we are actually going to pay (historical record)
        oldHistory.setDebitAmount(creditRequest.getDebitAmount()); // debitAmount (amount we will receive)
        oldHistory.setCreditAmount(creditRequest.getCreditAmount());
        oldHistory.setPaid(creditRequest.getPaid());
        oldHistory.setTotalAmount(creditRequest.getTotalAmount());
        supplierBillRepository.save(oldHistory);

        // Update Purchase details
        purchase.setProductName(request.getProductName());
        purchase.setProductStocks(savedProductStocks);
        purchase.setPurchaseDate(request.getPurchaseDate());
        purchase.setPurchaseAmount(request.getPurchaseAmount());
        purchase.setTotalPurchaseAmount(request.getTotalPurchaseAmount());
        purchase.setCgst(request.getCgst());
        purchase.setSgst(request.getSgst());
        purchase.setIgst(request.getIgst());
        purchase.setPaymentMode(request.getPaymentMode());
        // purchase.setDiscount(request.getDiscount());
        purchase.setSupplier(newSupplier);
        purchase.setMaterial(material);

        Purchase updatedPurchase = purchaseRepository.save(purchase);

        return mapToResponse(updatedPurchase, savedSupplierCredit);
    }


    @Override
    @Transactional
    public void deletePurchase(UUID id) {
        Purchase purchase = purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        // Get the supplier credit history for this purchase
        SupplierBill history = supplierBillRepository.findByPurchase(purchase)
                .orElseThrow(() -> new RuntimeException("History record not found for purchase"));

        // Reduce supplier credit amounts
        // creditAmount = amount we have to give to the supplier (how much we still owe)
        // debitAmount = amount we will receive from the supplier (how much they still have to pay us)
        // toPay = amount we are actually going to pay to the supplier (from total creditAmount)
        Supplier supplier = purchase.getSupplier();
        SupplierCredit supplierCredit = supplier.getSupplierCredit();
        supplierCredit.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(supplierCredit.getTotalAmount() - history.getTotalAmount()));
        supplierCredit.setPaid(AmountUtils.roundToTwoDecimalPlaces(supplierCredit.getPaid() - history.getPaid()));
        supplierCredit.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(supplierCredit.getCreditAmount() - history.getCreditAmount())); // Add back the toPay amount
        supplierCredit.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(supplierCredit.getDebitAmount() - history.getDebitAmount()));
        supplierCredit.setToPay(0.0); // Reset toPay to 0
        supplierCreditRepository.save(supplierCredit);

        // Delete all associated product stocks
        List<ProductStock> productStocks = purchase.getProductStocks();
        if (productStocks != null && !productStocks.isEmpty()) {

            for (ProductStock stock : productStocks) {
                if (stock.getImageUrl() != null && !stock.getImageUrl().isEmpty()) {
                    deleteImageFromS3(stock.getImageUrl());   // <---- DELETE IMAGE
                }
            }

            // Delete product stock DB records
            productStockRepository.deleteAll(productStocks);
        }

        // Delete the supplier credit history record
        supplierBillRepository.delete(history);

        // Delete the purchase
        purchaseRepository.deleteById(id);
    }

    @Override
    public String getNextPurchaseInvoiceNo() {
        return sequenceGeneratorService.getNextNumberWithoutSave("purchase_invoice");
    }

    private ProductStock mapToProductStock(ProductStockRequest request) {
        ProductStock productStock = new ProductStock();
        productStock.setSize(request.getSize());
        productStock.setColor(request.getColor());
        productStock.setBarcode(request.getBarcode());
        productStock.setUnit(request.getUnit());
        productStock.setHsnNo(request.getHsnNo());
        productStock.setReceivedQuantity(request.getReceivedQuantity());
        productStock.setMissingQuantity(request.getMissingQuantity());
        productStock.setTotalQuantity(request.getTotalQuantity());
        productStock.setStockQuantity(request.getReceivedQuantity()); // stockQuantity same as receivedQuantity
        productStock.setPricePerUnit(request.getPricePerUnit());
        return productStock;
    }

    private ProductStockResponse mapToProductStockResponse(ProductStock productStock) {
        ProductStockResponse response = new ProductStockResponse();
        response.setId(productStock.getId());
        response.setSize(productStock.getSize());
        response.setColor(productStock.getColor());
        response.setBarcode(productStock.getBarcode());
        response.setUnit(productStock.getUnit());
        response.setHsnNo(productStock.getHsnNo());
        response.setReceivedQuantity(productStock.getReceivedQuantity());
        response.setMissingQuantity(productStock.getMissingQuantity());
        response.setTotalQuantity(productStock.getTotalQuantity());
        response.setStockQuantity(productStock.getStockQuantity());
        response.setPricePerUnit(productStock.getPricePerUnit());
        response.setImageUrl(productStock.getImageUrl());
        response.setCreatedAt(productStock.getCreatedAt());
        response.setUpdatedAt(productStock.getUpdatedAt());
        response.setCreatedBy(productStock.getCreatedBy());
        response.setUpdatedBy(productStock.getUpdatedBy());
        return response;
    }

    private ProductStockWithPurchaseResponse mapToProductStockWithPurchaseResponse(ProductStock productStock, Purchase purchase) {
        ProductStockWithPurchaseResponse response = new ProductStockWithPurchaseResponse();
        // ProductStock details
        response.setProductStockId(productStock.getId());
        response.setSize(productStock.getSize());
        response.setColor(productStock.getColor());
        response.setBarcode(productStock.getBarcode());
        response.setUnit(productStock.getUnit());
        response.setHsnNo(productStock.getHsnNo());
        response.setReceivedQuantity(productStock.getReceivedQuantity());
        response.setMissingQuantity(productStock.getMissingQuantity());
        response.setTotalQuantity(productStock.getTotalQuantity());
        response.setStockQuantity(productStock.getStockQuantity());
        response.setPricePerUnit(productStock.getPricePerUnit());
        response.setProductStockCreatedAt(productStock.getCreatedAt());
        response.setProductStockUpdatedAt(productStock.getUpdatedAt());
        response.setProductStockCreatedBy(productStock.getCreatedBy());
        response.setProductStockUpdatedBy(productStock.getUpdatedBy());

        // Purchase details
        response.setPurchaseId(purchase.getId());
        response.setProductName(purchase.getProductName());
        response.setPurchaseDate(purchase.getPurchaseDate());
        response.setPurchaseAmount(purchase.getPurchaseAmount());
        response.setTotalPurchaseAmount(purchase.getTotalPurchaseAmount());
        response.setCgst(purchase.getCgst());
        response.setSgst(purchase.getSgst());
        response.setIgst(purchase.getIgst());
        response.setPaymentMode(purchase.getPaymentMode());
        // response.setDiscount(purchase.getDiscount());
        response.setPurchaseCreatedAt(purchase.getCreatedAt());
        response.setPurchaseUpdatedAt(purchase.getUpdatedAt());
        response.setPurchaseCreatedBy(purchase.getCreatedBy());
        response.setPurchaseUpdatedBy(purchase.getUpdatedBy());

        // Supplier details
        SupplierResponse supplierResponse = new SupplierResponse();
        supplierResponse.setId(purchase.getSupplier().getId());
        supplierResponse.setName(purchase.getSupplier().getName());
        supplierResponse.setEmail(purchase.getSupplier().getEmail());
        supplierResponse.setContact(purchase.getSupplier().getContact());
        supplierResponse.setGstNo(purchase.getSupplier().getGstNo());
        supplierResponse.setAddress(purchase.getSupplier().getAddress());
        supplierResponse.setSupplyType(purchase.getSupplier().getSupplyType());
        supplierResponse.setCreatedAt(purchase.getSupplier().getCreatedAt());
        supplierResponse.setUpdatedAt(purchase.getSupplier().getUpdatedAt());
        supplierResponse.setCreatedBy(purchase.getSupplier().getCreatedBy());
        supplierResponse.setUpdatedBy(purchase.getSupplier().getUpdatedBy());
        response.setSupplier(supplierResponse);

        // Material details
        MaterialResponse materialResponse = new MaterialResponse();
        materialResponse.setId(purchase.getMaterial().getId());
        materialResponse.setName(purchase.getMaterial().getName());
        materialResponse.setCreatedAt(purchase.getMaterial().getCreatedAt());
        materialResponse.setUpdatedAt(purchase.getMaterial().getUpdatedAt());
        materialResponse.setCreatedBy(purchase.getMaterial().getCreatedBy());
        materialResponse.setUpdatedBy(purchase.getMaterial().getUpdatedBy());
        response.setMaterial(materialResponse);

        return response;
    }

    private PurchaseResponse mapToResponse(Purchase purchase, SupplierCredit supplierCredit) {
        PurchaseResponse response = new PurchaseResponse();
        response.setId(purchase.getId());
        response.setInvoiceNo(purchase.getInvoiceNo());
        response.setProductName(purchase.getProductName());
        response.setProductStocks(purchase.getProductStocks().stream()
                .map(this::mapToProductStockResponse)
                .collect(Collectors.toList()));
        response.setPurchaseDate(purchase.getPurchaseDate());
        response.setPurchaseAmount(purchase.getPurchaseAmount());
        response.setCgst(purchase.getCgst());
        response.setSgst(purchase.getSgst());
        response.setIgst(purchase.getIgst());
        response.setPaymentMode(purchase.getPaymentMode());
        // response.setDiscount(purchase.getDiscount());
        SupplierResponse supplierResponse = new SupplierResponse();
        supplierResponse.setId(purchase.getSupplier().getId());
        supplierResponse.setName(purchase.getSupplier().getName());
        supplierResponse.setEmail(purchase.getSupplier().getEmail());
        supplierResponse.setContact(purchase.getSupplier().getContact());
        supplierResponse.setGstNo(purchase.getSupplier().getGstNo());
        supplierResponse.setAddress(purchase.getSupplier().getAddress());
        supplierResponse.setSupplyType(purchase.getSupplier().getSupplyType());
        supplierResponse.setCreatedAt(purchase.getSupplier().getCreatedAt());
        supplierResponse.setUpdatedAt(purchase.getSupplier().getUpdatedAt());
        supplierResponse.setCreatedBy(purchase.getSupplier().getCreatedBy());
        supplierResponse.setUpdatedBy(purchase.getSupplier().getUpdatedBy());
        response.setSupplier(supplierResponse);

        MaterialResponse materialResponse = new MaterialResponse();
        materialResponse.setId(purchase.getMaterial().getId());
        materialResponse.setName(purchase.getMaterial().getName());
        materialResponse.setCreatedAt(purchase.getMaterial().getCreatedAt());
        materialResponse.setUpdatedAt(purchase.getMaterial().getUpdatedAt());
        materialResponse.setCreatedBy(purchase.getMaterial().getCreatedBy());
        materialResponse.setUpdatedBy(purchase.getMaterial().getUpdatedBy());
        response.setMaterial(materialResponse);

        // Get the supplier credit history for this purchase
        SupplierBill history = supplierBillRepository.findByPurchase(purchase).orElse(null);
        SupplierBillResponse historyResponse = null;
        if (history != null) {
            historyResponse = new SupplierBillResponse();
            historyResponse.setId(history.getId());
            historyResponse.setInvoiceNo(history.getInvoiceNo());
            historyResponse.setToPay(history.getToPay());
            historyResponse.setDebitAmount(history.getDebitAmount());
            historyResponse.setCreditAmount(history.getCreditAmount());
            historyResponse.setPaid(history.getPaid());
            historyResponse.setTotalAmount(history.getTotalAmount());
            historyResponse.setCreatedAt(history.getCreatedAt());
            historyResponse.setUpdatedAt(history.getUpdatedAt());
            historyResponse.setCreatedBy(history.getCreatedBy());
            historyResponse.setUpdatedBy(history.getUpdatedBy());
        }
        response.setSupplierBill(historyResponse);

        response.setCreatedAt(purchase.getCreatedAt());
        response.setUpdatedAt(purchase.getUpdatedAt());
        response.setCreatedBy(purchase.getCreatedBy());
        response.setUpdatedBy(purchase.getUpdatedBy());
        return response;
    }

    public String uploadImageToS3(MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            // Extract extension
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Generate unique key (NO folders)
            String key = UUID.randomUUID().toString() + extension;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            // Return FULL URL (same as reference code)
            return "https://" + bucketName + ".s3.amazonaws.com/" + key;

        } catch (Exception e) {
            System.out.println("Image Upload Failed: " + e.getMessage());
            return null;
        }
    }


    public void deleteImageFromS3(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;
    
        String bucketUrl = "https://" + bucketName + ".s3.amazonaws.com/";
    
        if (!imageUrl.startsWith(bucketUrl)) {
            System.out.println("Invalid S3 URL format: " + imageUrl);
            return;
        }
    
        // Extract key from URL
        String key = imageUrl.substring(bucketUrl.length());
    
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build());
            System.out.println("Deleted S3 Image: " + key);
        } catch (Exception e) {
            System.out.println("Failed to delete S3 Image: " + e.getMessage());
        }
    }
    
}
