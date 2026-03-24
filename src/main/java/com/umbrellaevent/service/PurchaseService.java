package com.umbrellaevent.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.umbrellaevent.entity.dtos.purchase.ProductStockResponse;
import com.umbrellaevent.entity.dtos.purchase.ProductStockWithPurchaseResponse;
import com.umbrellaevent.entity.dtos.purchase.PurchaseRequest;
import com.umbrellaevent.entity.dtos.purchase.PurchaseResponse;

public interface PurchaseService {
    PurchaseResponse createPurchase(PurchaseRequest request, String username);
    PurchaseResponse getPurchaseById(UUID id);
    // PurchaseResponse getPurchaseByArticleNo(String articleNo);
    PurchaseResponse getPurchaseByInvoiceNo(String invoiceNo);
    ProductStockResponse getStockByBarcode(String barcode);
    ProductStockWithPurchaseResponse getStockByBarcodeWithPurchase(String barcode);
    List<PurchaseResponse> getAllPurchases();
    Page<PurchaseResponse> getAllPurchases(Pageable pageable);
    PurchaseResponse updatePurchase(UUID id, PurchaseRequest request, String username);
    void deletePurchase(UUID id);
    String getNextPurchaseInvoiceNo();

}
