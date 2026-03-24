package com.umbrellaevent.entity.dtos.purchase;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ProductStockGroupResponse {
    private String productName;
    private LocalDate purchaseDate;
    private List<AvailableProductStockResponse> productStocks;
}
