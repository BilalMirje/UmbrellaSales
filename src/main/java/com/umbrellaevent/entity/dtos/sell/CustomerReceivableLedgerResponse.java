package com.umbrellaevent.entity.dtos.sell;

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
public class CustomerReceivableLedgerResponse {

    private UUID customerId;
    private String customerName;
    private String contactNumber;
    private Double totalReceivable;
    private LocalDateTime date;
}
