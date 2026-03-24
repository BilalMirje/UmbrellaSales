package com.umbrellaevent.entity.dtos.purchase;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStockResponse {
    private UUID id;
    private String size;
    private String color;
    private String barcode;
    private String unit;
    private String hsnNo;
    private Integer receivedQuantity;
    private Integer missingQuantity;
    private Integer totalQuantity;
    private Integer stockQuantity;
    private Double pricePerUnit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String imageUrl;
}
