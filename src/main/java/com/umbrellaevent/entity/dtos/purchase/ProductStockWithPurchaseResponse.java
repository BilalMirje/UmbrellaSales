package com.umbrellaevent.entity.dtos.purchase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.umbrellaevent.entity.dtos.material.MaterialResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockWithPurchaseResponse {
    private UUID productStockId;
    private String size;
    private String color;
    private String barcode;
    private Integer quantity;
    private String unit;
    private Integer receivedQuantity;
    private Integer missingQuantity;
    private Integer totalQuantity;
    private Integer stockQuantity;
    private Double pricePerUnit;
    private LocalDateTime productStockCreatedAt;
    private LocalDateTime productStockUpdatedAt;
    private String productStockCreatedBy;
    private String productStockUpdatedBy;

    // Purchase details
    private UUID purchaseId;
    private String HsnNo;
    private String productName;
    private LocalDate purchaseDate;
    private Double purchaseAmount;
    private Double totalPurchaseAmount;
    private Double cgst;
    private Double sgst;
    private Double igst;
    private String paymentMode;
    private Double discount;
    private Double totalSaleRate;
    private SupplierResponse supplier;
    private MaterialResponse material;
    private LocalDateTime purchaseCreatedAt;
    private LocalDateTime purchaseUpdatedAt;
    private String purchaseCreatedBy;
    private String purchaseUpdatedBy;
}
