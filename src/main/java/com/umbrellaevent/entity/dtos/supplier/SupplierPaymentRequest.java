package com.umbrellaevent.entity.dtos.supplier;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPaymentRequest {

    private UUID supplierId;
    private Double paidAmount;
    
}
