package com.umbrellaevent.entity.dtos.supplier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.umbrellaevent.entity.dtos.material.MaterialResponse;
import com.umbrellaevent.entity.dtos.purchase.ProductStockResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPurchaseHistoryResponse {
    private UUID id;
    private String productName;
    private LocalDate purchaseDate;
    private Double purchaseAmount;
    private Double totalPurchaseAmount;
    private Double cgst;
    private Double sgst;
    private Double igst;
    private String paymentMode;
    private SupplierBillResponse supplierBill;
    private List<ProductStockResponse> productStocks;
    private MaterialResponse material;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
