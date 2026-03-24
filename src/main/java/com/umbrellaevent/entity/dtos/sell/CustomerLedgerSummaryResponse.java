package com.umbrellaevent.entity.dtos.sell;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerLedgerSummaryResponse {
    private UUID customerId;
    private String customerName;
    private String contactNumber;
    private Double totalPaid;
    private Double totalRemaining;
}
