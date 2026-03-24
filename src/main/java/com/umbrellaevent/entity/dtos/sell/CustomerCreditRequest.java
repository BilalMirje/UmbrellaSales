package com.umbrellaevent.entity.dtos.sell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Mirror of SupplierCreditRequest + toPay
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreditRequest {
    private Double toPay;
    private Double debitAmount;
    private Double creditAmount;
    private Double paid;
    private Double totalAmount;
    private Double taxTotal;
}
