package com.umbrellaevent.entity.dtos.supplier;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPaymentHistoryResponse {

    private UUID supplierId;
    private String supplierName;
    private String supplierContact;
    private List<SupplierPaymentEntryResponse> payments;
}
