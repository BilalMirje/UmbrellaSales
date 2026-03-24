package com.umbrellaevent.entity.dtos.sell;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellBillResponse {
    private UUID id;
    private Double totalAmount;
    // private Double discount;
    private Double gst;
    // private Double extraDiscount;
    private Double subTotal;
    // private Double savedAmount;
    private Double grandTotal;
    private Double paidAmount;
    private Double remainingAmount;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Double taxTotal;

}
