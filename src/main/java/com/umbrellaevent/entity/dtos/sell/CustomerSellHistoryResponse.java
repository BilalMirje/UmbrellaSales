package com.umbrellaevent.entity.dtos.sell;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSellHistoryResponse {
    private UUID sellBillId;
    private UUID sellId;
    private String invoiceNo;
    private LocalDate sellDate;
    private Double totalAmount;
    private Double paid;
    private Double toPay;
    private LocalDateTime createdAt;
}
