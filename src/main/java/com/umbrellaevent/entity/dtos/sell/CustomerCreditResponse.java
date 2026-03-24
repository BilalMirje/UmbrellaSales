package com.umbrellaevent.entity.dtos.sell;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreditResponse {
    private UUID id;
    private String customerName;
    private String contactNo;
    private Double paid;
    private Double remaining;
    private Double total;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
