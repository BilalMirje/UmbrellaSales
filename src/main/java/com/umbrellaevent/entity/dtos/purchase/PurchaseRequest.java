package com.umbrellaevent.entity.dtos.purchase;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.umbrellaevent.entity.dtos.supplier.SupplierCreditRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
public class PurchaseRequest {

    private String invoiceNo;
    private String productName;
        private List<ProductStockRequest> productStocks;
        private LocalDate purchaseDate;
        private Double purchaseAmount;
        private Double totalPurchaseAmount;
        private Double cgst;
        private Double sgst;
        private Double igst;
        private String paymentMode;
        private UUID supplierId;
        private UUID materialId;
        private String materialName;
        private SupplierCreditRequest supplierCredit;
    }
