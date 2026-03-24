package com.umbrellaevent.entity.dtos.sell;





import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLedgerResponse {
    private UUID customerId;
    private String customerName;
    private String contactNumber;
    private List<CustomerSellHistoryResponse> entries;
    private String supplyType;
}
