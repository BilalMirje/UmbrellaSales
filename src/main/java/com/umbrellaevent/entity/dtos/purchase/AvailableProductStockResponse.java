package com.umbrellaevent.entity.dtos.purchase;

import lombok.Data;

@Data
public class AvailableProductStockResponse {
    private String size;
    private String color;
    private String barcode;
    private String unit;
    private String hsnNo;
    private Integer stockQuantity;
    private Double pricePerUnit;
    private String imageUrl;
}
