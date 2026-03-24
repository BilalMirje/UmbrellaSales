package com.umbrellaevent.entity.dtos.sell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SellStockRequest {
    private String size;
    private String color;
    private String barcode;
    private String unit;
    private String hsnNo;
    private Integer quantity;
    private Double rate;
    private Double amount;
}
