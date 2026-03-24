package com.umbrellaevent.entity.dtos.customer;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerPaymentHistoryResponse {

    private UUID customerId;
    private String customerName;
    private String contactNumber;
    private List<CustomerPaymentEntryResponse> payments;
}

