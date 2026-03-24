package com.umbrellaevent.entity.dtos.supplier;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPaymentEntryResponse {

    private UUID id;
    private Double paidAmount;
    private String type;
    private LocalDateTime paymentDate;
}
