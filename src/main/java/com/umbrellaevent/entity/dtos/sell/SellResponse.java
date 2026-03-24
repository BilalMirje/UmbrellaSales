package com.umbrellaevent.entity.dtos.sell;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.umbrellaevent.entity.dtos.material.MaterialResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellResponse {
    private UUID id;
    private String invoiceNo;
    private String materialName;
    private List<SellStockResponse> sellStocks;
    private LocalDate sellDate;
    private Double sellAmount;
    private Double totalSellAmount;
    private Double cgst;
    private Double sgst;
    private Double igst;
    private Double taxTotal;
    // private String paymentMode;
    private String chequeNo;
     private Double grandTotal;

    private CustomerResponse customer;
    private MaterialResponse material;
    private SellBillResponse sellBill;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private List<ProductDetailResponse> productDetails;

// Add tax percentage fields
private Double cgstPercentage;
private Double sgstPercentage;
private Double igstPercentage;

}
