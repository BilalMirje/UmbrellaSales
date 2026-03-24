package com.umbrellaevent.entity.dtos.purchase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


import com.umbrellaevent.entity.dtos.material .MaterialResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierBillResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseResponse {
    private UUID id;
    private String invoiceNo;
    private String productName;
    private List<ProductStockResponse> productStocks;
    private LocalDate purchaseDate;
    private Double purchaseAmount;
    private Double totalPurchaseAmount;
    private Double cgst;
    private Double sgst;
    private Double igst;
    private String paymentMode;
    private SupplierResponse supplier;
    private MaterialResponse material;
    private SupplierBillResponse supplierBill;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
