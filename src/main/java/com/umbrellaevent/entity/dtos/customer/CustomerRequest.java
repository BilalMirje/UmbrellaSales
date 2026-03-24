package com.umbrellaevent.entity.dtos.customer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequest {
    private String name;
    private String email;
    private String contact;
    private String gstNo;
    private String address;
    private String supplyType;
}
