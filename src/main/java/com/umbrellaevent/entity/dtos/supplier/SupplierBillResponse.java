package com.umbrellaevent.entity.dtos.supplier;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierBillResponse {
    private UUID id;
    private String invoiceNo;
    private Double toPay;
    private Double debitAmount;
    private Double creditAmount;
    private Double paid;
    private Double totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
