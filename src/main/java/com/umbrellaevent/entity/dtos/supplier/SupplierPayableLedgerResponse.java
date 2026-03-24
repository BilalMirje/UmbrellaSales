package com.umbrellaevent.entity.dtos.supplier;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPayableLedgerResponse {

    private UUID supplierId;
    private String supplierName;
    private String contactNumber;
    private String supplyType;
    private Double totalPayable;
    private LocalDateTime date;
}
