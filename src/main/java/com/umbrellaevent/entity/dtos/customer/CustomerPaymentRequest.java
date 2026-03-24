package com.umbrellaevent.entity.dtos.customer;

import java.util.UUID;

import lombok.Data;

@Data
public class CustomerPaymentRequest {
    private UUID customerId;
    private Double paidAmount;
}