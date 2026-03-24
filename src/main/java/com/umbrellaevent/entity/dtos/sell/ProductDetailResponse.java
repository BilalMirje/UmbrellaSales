package com.umbrellaevent.entity.dtos.sell;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailResponse {
    private String productName;
    private Double pricePerUnit;
    private String hsnNo;
}
