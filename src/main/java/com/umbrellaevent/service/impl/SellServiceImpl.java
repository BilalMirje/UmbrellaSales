package com.umbrellaevent.service.impl;

import com.umbrellaevent.config.AmountUtils;
import com.umbrellaevent.entity.*;

import com.umbrellaevent.entity.dtos.material.MaterialResponse;
import com.umbrellaevent.entity.dtos.sell.*;import com.umbrellaevent.repository.*;
import com.umbrellaevent.service.SellService;

import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SellServiceImpl implements SellService {

    private final SellRepository sellRepository;
    private final SellStockRepository sellStockRepository;
    private final CustomerRepository customerRepository;
    private final CustomerCreditRepository customerCreditRepository;
    private final ProductStockRepository productStockRepository;

    private final SellBillRepository sellBillRepository;
    private final SequenceGeneratorService sequenceGeneratorService;

    private final PurchaseRepository purchaseRepository;

    @SuppressWarnings("null")
    @Override
    @Transactional
    public SellResponse createSell(SellRequest request, String username) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        
        SellStockRequest firstItem = request.getSellStocks().get(0);

        ProductStock psFirst = productStockRepository.findByBarcode(firstItem.getBarcode())
                .orElseThrow(() -> new RuntimeException("Product not found for barcode: " + firstItem.getBarcode()));

        // Fetch purchase linked to this product stock
        Purchase purchase = purchaseRepository.findByProductStocks_Id(psFirst.getId())
                .orElseThrow(
                        () -> new RuntimeException("Purchase not found for stock item: " + firstItem.getBarcode()));

        String productName = purchase.getProductName();
        Material material = purchase.getMaterial();

        // Validate stocks and subtract quantities
       List<SellStock> sellStocks = new ArrayList<>();

for (SellStockRequest item : request.getSellStocks()) {

    ProductStock ps = productStockRepository.findByBarcode(item.getBarcode())
            .orElseThrow(() -> new RuntimeException("Product not found for barcode: " + item.getBarcode()));

    // ⭐ GET Purchase per barcode
    Purchase purchaseItem = purchaseRepository.findByProductStocks_Id(ps.getId())
            .orElseThrow(() -> new RuntimeException("Purchase not found for stock item: " + item.getBarcode()));

    if (ps.getStockQuantity() < item.getQuantity()) {
        throw new RuntimeException("Insufficient stock for barcode: " + item.getBarcode());
    }

    ps.setStockQuantity(ps.getStockQuantity() - item.getQuantity());
    productStockRepository.save(ps);

    SellStock ss = new SellStock();
    ss.setSize(item.getSize());
    ss.setColor(item.getColor());
    ss.setBarcode(item.getBarcode());
    ss.setUnit(item.getUnit());
    ss.setHsnNo(item.getHsnNo());
    ss.setQuantity(item.getQuantity());
    ss.setRate(item.getRate());
    ss.setAmount(item.getAmount());

    // ⭐ FIX — productName per barcode
    ss.setProductName(purchaseItem.getProductName());

    sellStocks.add(ss);
}


        List<SellStock> savedSellStocks = sellStockRepository.saveAll(sellStocks);

        // Create sell
        Sell sell = new Sell();
        sell.setInvoiceNo(request.getInvoiceNo());
        // sell.setProductName(request.getProductName());
        sell.setSellDate(request.getSellDate());
        sell.setSellAmount(request.getSellAmount());
        sell.setTotalSellAmount(request.getTotalSellAmount());
        sell.setCgst(request.getCgst());
        sell.setSgst(request.getSgst());
        sell.setIgst(request.getIgst());
        // sell.setPaymentMode(request.getPaymentMode());
        sell.setSellStocks(savedSellStocks);
        sell.setCustomer(customer);
        sell.setMaterial(material);
        sell.setChequeNo(request.getChequeNo());
        sell.setGrandTotal(request.getGrandTotal());
        sell.setTaxTotal(request.getTaxTotal());
        sell.setProductName(productName);
        sell.setMaterial(material);

        Sell savedSell = sellRepository.save(sell);

        // Ledger: customer credit update (mirror supplier logic)
        CustomerCreditRequest cr = request.getCustomerCredit();
        CustomerCredit existingCredit = customer.getCustomerCredit();

        existingCredit.setTotalAmount(
                AmountUtils.roundToTwoDecimalPlaces(existingCredit.getTotalAmount() + safeDouble(cr.getTotalAmount())));
        existingCredit
                .setPaid(AmountUtils.roundToTwoDecimalPlaces(existingCredit.getPaid() + safeDouble(cr.getPaid())));
        existingCredit.setCreditAmount(AmountUtils
                .roundToTwoDecimalPlaces(existingCredit.getCreditAmount() + safeDouble(cr.getCreditAmount())));
        existingCredit.setDebitAmount(
                AmountUtils.roundToTwoDecimalPlaces(existingCredit.getDebitAmount() + safeDouble(cr.getDebitAmount())));
        existingCredit.setToPay(0.0); // after applying payment

        customerCreditRepository.save(existingCredit);

        // Create SellBill (history record)
        SellBill bill = new SellBill();
        bill.setInvoiceNo(savedSell.getInvoiceNo());
        bill.setSell(savedSell);
        bill.setCustomer(customer);
        bill.setToPay(safeDouble(cr.getToPay()));
        bill.setDebitAmount(safeDouble(cr.getDebitAmount()));
        bill.setCreditAmount(safeDouble(cr.getCreditAmount()));
        bill.setPaid(safeDouble(cr.getPaid()));
        bill.setTotalAmount(safeDouble(cr.getTotalAmount()));
        bill.setTaxTotal(cr.getTaxTotal());

        SellBill savedBill = sellBillRepository.save(bill);

        // increment invoice sequence
        sequenceGeneratorService.getNextNumber("sell_invoice");

        return mapToResponse(savedSell, savedBill);
    }

    @SuppressWarnings("null")
    @Override
    public SellResponse getSellById(UUID id) {
        Sell sell = sellRepository.findById(id).orElseThrow(() -> new RuntimeException("Sell not found"));
        SellBill bill = sellBillRepository.findBySell(sell);
        return mapToResponse(sell, bill);
    }

    @Override
    public SellResponse getSellByInvoiceNo(String invoiceNo) {
        Sell sell = sellRepository.findByInvoiceNo(invoiceNo).orElseThrow(() -> new RuntimeException("Sell not found"));
        SellBill bill = sellBillRepository.findBySell(sell);
        return mapToResponse(sell, bill);
    }

    @Override
    public List<SellResponse> getAllSells() {
        return sellRepository.findAll().stream().map(s -> mapToResponse(s, sellBillRepository.findBySell(s)))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Override
    public Page<SellResponse> getAllSells(Pageable pageable) {
        Page<Sell> page = sellRepository.findAll(pageable);
        return page.map(s -> mapToResponse(s, sellBillRepository.findBySell(s)));
    }

    @SuppressWarnings("null")
    @Override
    @Transactional
    public void deleteSell(UUID id) {
        Sell sell = sellRepository.findById(id).orElseThrow(() -> new RuntimeException("Sell not found"));
        SellBill bill = sellBillRepository.findBySell(sell);

        // revert customer credit
        CustomerCredit cc = sell.getCustomer().getCustomerCredit();
        cc.setTotalAmount(cc.getTotalAmount() - bill.getTotalAmount());
        cc.setPaid(cc.getPaid() - bill.getPaid());
        cc.setCreditAmount(cc.getCreditAmount() - bill.getCreditAmount());
        cc.setDebitAmount(cc.getDebitAmount() - bill.getDebitAmount());
        cc.setToPay(0.0);
        customerCreditRepository.save(cc);

        // revert stocks
        if (sell.getSellStocks() != null) {
            for (SellStock ss : sell.getSellStocks()) {
                ProductStock ps = productStockRepository.findByBarcode(ss.getBarcode()).orElse(null);
                if (ps != null) {
                    ps.setStockQuantity(ps.getStockQuantity() + ss.getQuantity());
                    productStockRepository.save(ps);
                }
            }
        }

        sellStockRepository.deleteAll(sell.getSellStocks());
        sellBillRepository.delete(bill);
        sellRepository.delete(sell);
    }

    @Override
    public String getNextSellInvoiceNumber() {
        return sequenceGeneratorService.getNextNumberWithoutSave("sell_invoice");
    }


    
    private SellResponse mapToResponse(Sell sell, SellBill bill) {
    SellResponse res = new SellResponse();

    // OLD FIELDS (unchanged)
    res.setId(sell.getId());
    res.setInvoiceNo(sell.getInvoiceNo());
    res.setMaterialName(sell.getMaterial() != null ? sell.getMaterial().getName() : null);
    res.setSellDate(sell.getSellDate());
    res.setSellAmount(sell.getSellAmount());
    res.setTotalSellAmount(sell.getTotalSellAmount());
    res.setCgst(sell.getCgst());  // amount or percentage? (You store percentage)
    res.setSgst(sell.getSgst());
    res.setIgst(sell.getIgst());
    res.setChequeNo(sell.getChequeNo());
    res.setGrandTotal(sell.getGrandTotal());
    res.setTaxTotal(sell.getTaxTotal());

    //  NEW: Percentages (same values because DB stores percentage)
    res.setCgstPercentage(sell.getCgst());
    res.setSgstPercentage(sell.getSgst());
    res.setIgstPercentage(sell.getIgst());

    // OLD: SELL STOCKS
    if (sell.getSellStocks() != null) {
        res.setSellStocks(
            sell.getSellStocks()
                .stream()
                .map(s -> new SellStockResponse(
                        s.getSize(),
                        s.getColor(),
                        s.getBarcode(),
                        s.getUnit(),
                        s.getHsnNo(),
                        s.getQuantity(),
                        s.getRate(),
                        s.getAmount()
                )).collect(Collectors.toList())
        );
    }

    // NEW: PRODUCT DETAILS (from ProductStock table)
    List<ProductDetailResponse> productDetailsList = new ArrayList<>();

    for (SellStock ss : sell.getSellStocks()) {

        ProductStock ps = productStockRepository.findByBarcode(ss.getBarcode())
                .orElse(null);

        if (ps != null) {
            productDetailsList.add(
    new ProductDetailResponse(
        ss.getProductName(),  
        ps.getPricePerUnit(),
        ps.getHsnNo()
    )
);

        }
    }

    res.setProductDetails(productDetailsList);

    // OLD: CUSTOMER
    if (sell.getCustomer() != null) {
        Customer c = sell.getCustomer();
        res.setCustomer(new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getContact(),
                c.getGstNo(), c.getAddress(), c.getSupplyType(),
                c.getCreatedAt(), c.getUpdatedAt(),
                c.getCreatedBy(), c.getUpdatedBy()
        ));
    }

    // OLD: MATERIAL
    if (sell.getMaterial() != null) {
        Material m = sell.getMaterial();
        res.setMaterial(new MaterialResponse(
                m.getId(), m.getName(),
                m.getCreatedAt(), m.getUpdatedAt(),
                m.getCreatedBy(), m.getUpdatedBy()
        ));
    }

    // OLD: SELL BILL
    SellBillResponse br = new SellBillResponse();

    if (bill != null) {
        br.setId(bill.getId());
        br.setTotalAmount(bill.getTotalAmount());
        br.setGst(sell.getCgst() + sell.getSgst() + sell.getIgst());  // percentage sum
        br.setSubTotal(sell.getTotalSellAmount());
        br.setTaxTotal(bill.getTaxTotal());
        br.setGrandTotal(bill.getTotalAmount());
        br.setPaidAmount(bill.getPaid());
        br.setRemainingAmount(bill.getToPay());
        br.setUnit(
            sell.getSellStocks() != null && !sell.getSellStocks().isEmpty()
                ? sell.getSellStocks().get(0).getUnit()
                : null
        );
        br.setCreatedAt(bill.getCreatedAt());
        br.setUpdatedAt(bill.getUpdatedAt());
        br.setCreatedBy(bill.getCreatedBy());
        br.setUpdatedBy(bill.getUpdatedBy());
    }

    res.setSellBill(br);

    // OLD AUDIT FIELDS
    res.setCreatedAt(sell.getCreatedAt());
    res.setUpdatedAt(sell.getUpdatedAt());
    res.setCreatedBy(sell.getCreatedBy());
    res.setUpdatedBy(sell.getUpdatedBy());

    return res;
}

    private double safeDouble(Double d) {
        return d == null ? 0.0 : d;
    }

    @Override
    public CustomerCreditResponse getCustomerCredit(UUID customerId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCustomerCredit'");
    }

   @Override
public Optional<SellResponse> findByInvoiceNo(String invoiceNo) {
    return sellRepository.findByInvoiceNo(invoiceNo)
            .map(sell -> mapToResponse(sell, sellBillRepository.findBySell(sell)));
}

}
