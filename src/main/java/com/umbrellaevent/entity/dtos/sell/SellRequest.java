package com.umbrellaevent.entity.dtos.sell;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellRequest {
    private String invoiceNo;
    private UUID customerId;
    // private UUID materialId;
    // private String materialName;
    private List<SellStockRequest> sellStocks;
    private LocalDate sellDate;
    private Double sellAmount;
    private Double totalSellAmount;
    private Double cgst;
    private Double sgst;
    private Double igst;
    // private String paymentMode;
    private CustomerCreditRequest customerCredit;
    // private String productName;
    private String chequeNo;
    private Double grandTotal;
    private Double taxTotal;

}
