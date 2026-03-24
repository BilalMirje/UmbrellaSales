package com.umbrellaevent.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.umbrellaevent.config.AmountUtils;
import com.umbrellaevent.entity.Customer;
import com.umbrellaevent.entity.CustomerCredit;
import com.umbrellaevent.entity.ProductStock;
import com.umbrellaevent.entity.Purchase;
import com.umbrellaevent.entity.SellBill;
import com.umbrellaevent.entity.SupplierBill;
import com.umbrellaevent.entity.dtos.material.MaterialResponse;
import com.umbrellaevent.entity.dtos.purchase.AvailableProductStockResponse;
import com.umbrellaevent.entity.dtos.purchase.ProductStockGroupResponse;
import com.umbrellaevent.entity.dtos.purchase.ProductStockResponse;
import com.umbrellaevent.entity.dtos.sell.CustomerLedgerResponse;
import com.umbrellaevent.entity.dtos.sell.CustomerLedgerSummaryResponse;
import com.umbrellaevent.entity.dtos.sell.CustomerSellHistoryResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierBillResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPayableLedgerResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPurchaseHistoryListResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPurchaseHistoryResponse;
import com.umbrellaevent.repository.CustomerRepository;
import com.umbrellaevent.repository.ProductStockRepository;
import com.umbrellaevent.repository.PurchaseRepository;
import com.umbrellaevent.repository.SellBillRepository;
import com.umbrellaevent.repository.SupplierBillRepository;
import com.umbrellaevent.repository.SupplierCreditRepository;
import com.umbrellaevent.service.ReportsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportsServiceImpl implements ReportsService {

    private final PurchaseRepository purchaseRepository;
    private final ProductStockRepository productStockRepository;
    private final SupplierBillRepository supplierBillRepository;
    private final SupplierCreditRepository supplierCreditRepository;
   
    private final CustomerRepository customerRepository;
    private final SellBillRepository sellBillRepository;

    @Override
    public SupplierPurchaseHistoryListResponse getSupplierPurchaseHistory(UUID supplierId) {
        // Find all purchases for the supplier
        List<Purchase> purchases = purchaseRepository.findAll().stream()
                .filter(purchase -> purchase.getSupplier().getId().equals(supplierId))
                .collect(Collectors.toList());

        if (purchases.isEmpty()) {
            return null;
        }

        List<SupplierPurchaseHistoryResponse> purchaseResponses = purchases.stream()
                .map(this::mapToSupplierPurchaseHistoryResponse)
                .collect(Collectors.toList());

        SupplierPurchaseHistoryListResponse response = new SupplierPurchaseHistoryListResponse();
        response.setPurchases(purchaseResponses);
        return response;
    }

    private SupplierPurchaseHistoryResponse mapToSupplierPurchaseHistoryResponse(Purchase purchase) {
        SupplierPurchaseHistoryResponse response = new SupplierPurchaseHistoryResponse();
        response.setId(purchase.getId());
        response.setProductName(purchase.getProductName());
        response.setPurchaseDate(purchase.getPurchaseDate());
        response.setPurchaseAmount(AmountUtils.roundToTwoDecimalPlaces(purchase.getPurchaseAmount()));
        response.setTotalPurchaseAmount(AmountUtils.roundToTwoDecimalPlaces(purchase.getTotalPurchaseAmount()));
        response.setCgst(AmountUtils.roundToTwoDecimalPlaces(purchase.getCgst()));
        response.setSgst(AmountUtils.roundToTwoDecimalPlaces(purchase.getSgst()));
        response.setIgst(AmountUtils.roundToTwoDecimalPlaces(purchase.getIgst()));
        response.setPaymentMode(purchase.getPaymentMode());
        // response.setDiscount(AmountUtils.roundToTwoDecimalPlaces(purchase.getDiscount()));

        // Map SupplierBill for this purchase
        SupplierBill supplierBill = supplierBillRepository.findAll().stream()
                .filter(bill -> bill.getPurchase().getId().equals(purchase.getId()))
                .findFirst()
                .orElse(null);
        if (supplierBill != null) {
            SupplierBillResponse billResponse = new SupplierBillResponse();
            billResponse.setId(supplierBill.getId());
            billResponse.setInvoiceNo(supplierBill.getInvoiceNo());
            billResponse.setToPay(supplierBill.getToPay());
            
            billResponse.setDebitAmount(supplierBill.getDebitAmount());
            billResponse.setCreditAmount(supplierBill.getCreditAmount());
            billResponse.setPaid(supplierBill.getPaid());
            billResponse.setTotalAmount(supplierBill.getTotalAmount());
            billResponse.setCreatedAt(supplierBill.getCreatedAt());
            billResponse.setUpdatedAt(supplierBill.getUpdatedAt());
            billResponse.setCreatedBy(supplierBill.getCreatedBy());
            billResponse.setUpdatedBy(supplierBill.getUpdatedBy());
            response.setSupplierBill(billResponse);
        }

        // Map ProductStocks
        response.setProductStocks(purchase.getProductStocks().stream()
                .map(this::mapToProductStockResponse)
                .collect(Collectors.toList()));

        // Map Material
        MaterialResponse materialResponse = new MaterialResponse();
        materialResponse.setId(purchase.getMaterial().getId());
        materialResponse.setName(purchase.getMaterial().getName());
        materialResponse.setCreatedAt(purchase.getMaterial().getCreatedAt());
        materialResponse.setUpdatedAt(purchase.getMaterial().getUpdatedAt());
        materialResponse.setCreatedBy(purchase.getMaterial().getCreatedBy());
        materialResponse.setUpdatedBy(purchase.getMaterial().getUpdatedBy());
        response.setMaterial(materialResponse);

        response.setCreatedAt(purchase.getCreatedAt());
        response.setUpdatedAt(purchase.getUpdatedAt());
        response.setCreatedBy(purchase.getCreatedBy());
        response.setUpdatedBy(purchase.getUpdatedBy());

        return response;
    }

    @Override
    public List<ProductStockGroupResponse> getProductStocksWithPurchase() {
        List<ProductStock> productStocks = productStockRepository.findAll();

        // Filter out productStocks with stockQuantity == 0
        List<ProductStock> filteredStocks = productStocks.stream()
                .filter(stock -> stock.getStockQuantity() != null && stock.getStockQuantity() > 0)
                .collect(Collectors.toList());

        // Group by productName + purchaseDate
        Map<String, List<ProductStock>> groupedStocks = filteredStocks.stream()
                .collect(Collectors.groupingBy(stock -> {
                    Purchase purchase = purchaseRepository.findAll().stream()
                            .filter(p -> p.getProductStocks().contains(stock))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Purchase not found for product stock"));
                    return purchase.getProductName() + "|" + purchase.getPurchaseDate().toString();
                }));

        return groupedStocks.entrySet().stream()
                .map(entry -> {
                    String key = entry.getKey();
                    String[] parts = key.split("\\|");
                    String productName = parts[0];
                    LocalDate purchaseDate = LocalDate.parse(parts[1]);

                    List<AvailableProductStockResponse> availableStocks = entry.getValue().stream()
                            .map(this::mapToAvailableProductStockResponse)
                            .collect(Collectors.toList());

                    ProductStockGroupResponse response = new ProductStockGroupResponse();
                    response.setProductName(productName);
                    response.setPurchaseDate(purchaseDate);
                    response.setProductStocks(availableStocks);
                    return response;
                })
                .collect(Collectors.toList());
    }

    private AvailableProductStockResponse mapToAvailableProductStockResponse(ProductStock productStock) {
        AvailableProductStockResponse response = new AvailableProductStockResponse();
        response.setSize(productStock.getSize());
        response.setColor(productStock.getColor());
        response.setBarcode(productStock.getBarcode());
        response.setUnit(productStock.getUnit());
        response.setHsnNo(productStock.getHsnNo());
        response.setStockQuantity(productStock.getStockQuantity());
        response.setPricePerUnit(productStock.getPricePerUnit());
        response.setImageUrl(productStock.getImageUrl());
        return response;
    }

    private ProductStockResponse mapToProductStockResponse(com.umbrellaevent.entity.ProductStock productStock) {
        ProductStockResponse response = new ProductStockResponse();
        response.setId(productStock.getId());
        response.setSize(productStock.getSize());
        response.setColor(productStock.getColor());
        response.setBarcode(productStock.getBarcode());
        response.setUnit(productStock.getUnit());
        response.setHsnNo(productStock.getHsnNo());
        response.setMissingQuantity(productStock.getMissingQuantity());
        response.setReceivedQuantity(productStock.getReceivedQuantity());
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

    @Override
    public List<SupplierPayableLedgerResponse> getSupplierPayableLedger() {
        List<com.umbrellaevent.entity.SupplierBill> bills = supplierBillRepository.findAll();
        return bills.stream()
                .collect(Collectors.groupingBy(bill -> bill.getSupplier().getId()))
                .entrySet()
                .stream()
                .map(entry -> {
                    UUID supplierId = entry.getKey();
                    List<com.umbrellaevent.entity.SupplierBill> supplierBills = entry.getValue();
                    com.umbrellaevent.entity.Supplier supplier = supplierBills.get(0).getSupplier();
                    Double totalPayable = AmountUtils.roundToTwoDecimalPlaces(supplierBills.stream().mapToDouble(com.umbrellaevent.entity.SupplierBill::getDebitAmount).sum());
                    java.time.LocalDateTime date = supplierBills.stream()
                            .map(bill -> bill.getCreatedAt())
                            .max(java.time.LocalDateTime::compareTo)
                            .orElse(java.time.LocalDateTime.now());
                    SupplierPayableLedgerResponse response = new SupplierPayableLedgerResponse();
                    response.setSupplierId(supplierId);
                    response.setSupplierName(supplier.getName());
                    response.setSupplyType(supplier.getSupplyType());
                    response.setTotalPayable(totalPayable);
                    response.setContactNumber(supplier.getContact());
                    response.setDate(date);
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<SupplierCreditResponse> getSupplierCreditData() {
        List<com.umbrellaevent.entity.SupplierCredit> credits = supplierCreditRepository.findAll();
        return credits.stream()
                .map(credit -> {
                    SupplierCreditResponse response = new SupplierCreditResponse();
                    response.setId(credit.getId());
                    response.setToPay(credit.getToPay());
                    response.setDebitAmount(credit.getDebitAmount());
                    response.setCreditAmount(credit.getCreditAmount());
                    response.setPaid(credit.getPaid());
                    response.setTotalAmount(credit.getTotalAmount());
                    response.setCreatedAt(credit.getCreatedAt());
                    response.setUpdatedAt(credit.getUpdatedAt());
                    response.setCreatedBy(credit.getCreatedBy());
                    response.setUpdatedBy(credit.getUpdatedBy());
                    return response;
                })
                .collect(Collectors.toList());
    }

   


  @SuppressWarnings("unused")
@Override
  public CustomerLedgerResponse getCustomerSellLedger(UUID customerId) {
      @SuppressWarnings("null")
      Customer customer = customerRepository.findById(customerId)
              .orElseThrow(() -> new RuntimeException("Customer not found"));

      List<SellBill> bills = sellBillRepository.findAll().stream()
              .filter(b -> b.getCustomer() != null && b.getCustomer().getId().equals(customerId))
              .collect(Collectors.toList());

      List<CustomerSellHistoryResponse> entries = bills.stream().map(b -> {
          CustomerSellHistoryResponse e = new CustomerSellHistoryResponse();
          e.setSellBillId(b.getId());
          e.setSellId(b.getSell() != null ? b.getSell().getId() : null);
          e.setInvoiceNo(b.getInvoiceNo());
          e.setSellDate(b.getSell() != null ? b.getSell().getSellDate() : null);
          e.setTotalAmount(b.getTotalAmount());
          e.setPaid(b.getPaid());
          e.setToPay(b.getToPay());
          e.setCreatedAt(b.getCreatedAt());
          return e;
      }).collect(Collectors.toList());

      double totalCredit = bills.stream().mapToDouble(b -> b.getCreditAmount() == null ? 0.0 : b.getCreditAmount())
              .sum();
      double totalPaid = bills.stream().mapToDouble(b -> b.getPaid() == null ? 0.0 : b.getPaid()).sum();
      double outstanding = totalCredit - totalPaid;

      CustomerLedgerResponse resp = new CustomerLedgerResponse();
      resp.setCustomerId(customer.getId());
      resp.setCustomerName(customer.getName());
      resp.setContactNumber(customer.getContact());
      resp.setSupplyType(customer.getSupplyType());
      // resp.setTotalOutstanding(outstanding);
      resp.setEntries(entries);

      return resp;
  }
  @Override
  public List<CustomerLedgerResponse> getAllCustomerSellLedgers() {

      List<Customer> customers = customerRepository.findAll();

      List<CustomerLedgerResponse> responseList = new ArrayList<>();

      for (Customer customer : customers) {
          CustomerLedgerResponse ledger = getCustomerSellLedger(customer.getId());
          responseList.add(ledger);
      }

      return responseList;
  }


@Override
public List<CustomerLedgerSummaryResponse> getCustomerLedgerSummary() {

    return customerRepository.findAll().stream()
        .map(customer -> {

            CustomerCredit credit = customer.getCustomerCredit();

            return new CustomerLedgerSummaryResponse(
                customer.getId(),
                customer.getName(),
                customer.getContact(),
                AmountUtils.roundToTwoDecimalPlaces(credit.getPaid()),
                AmountUtils.roundToTwoDecimalPlaces(
                    credit.getTotalAmount() - credit.getPaid()
                )
            );
        })
        .collect(Collectors.toList());
}

@SuppressWarnings("unused")
private double safe(Double d) {
    return d == null ? 0.0 : d;
}



}
