package com.umbrellaevent.entity.dtos.customer;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPaymentEntryResponse {

    private UUID paymentId;
    private Double paidAmount;
    private LocalDate paymentDate;
}