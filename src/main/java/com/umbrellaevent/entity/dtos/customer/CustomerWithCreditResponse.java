package com.umbrellaevent.entity.dtos.customer;

import java.time.LocalDateTime;
import java.util.UUID;

import com.umbrellaevent.entity.dtos.sell.CustomerCreditResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerWithCreditResponse {
    private UUID id;
    private String name;
    private String email;
    private String contact;
    private String gstNo;
    private String address;
    private String supplyType;

    private CustomerCreditResponse customerCredit;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
