package com.umbrellaevent.entity.dtos.supplier;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierBillRequest {
    private UUID purchaseId;
    private UUID supplierId;
    private Double toPay;
    private Double debitAmount;
    private Double creditAmount;
    private Double paid;
    private Double totalAmount;
    private Integer receivedQuantity;
    private Integer missingQuantity;
    private Integer totalQuantity;
    private Double pricePerUnit;
}
