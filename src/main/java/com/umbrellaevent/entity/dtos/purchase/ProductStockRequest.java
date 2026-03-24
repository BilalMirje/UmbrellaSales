package com.umbrellaevent.entity.dtos.purchase;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockRequest {
    private String size;
    private String color;
    private String barcode;
    private String unit;
    private String hsnNo;
    private Integer receivedQuantity;
    private Integer missingQuantity;
    private Integer totalQuantity;
    private Double pricePerUnit;

    @JsonIgnore
    private MultipartFile image;
}
