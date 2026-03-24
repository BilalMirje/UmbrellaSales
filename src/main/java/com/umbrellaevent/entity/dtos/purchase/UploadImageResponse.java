package com.umbrellaevent.entity.dtos.purchase;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadImageResponse {
    private UUID productId;
    private String imageUrl;
}
