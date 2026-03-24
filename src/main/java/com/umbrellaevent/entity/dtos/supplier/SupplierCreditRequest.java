package com.umbrellaevent.entity.dtos.supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierCreditRequest {
    private Double toPay;
    private Double debitAmount;
    private Double creditAmount;
    private Double paid;
    private Double totalAmount;
}
