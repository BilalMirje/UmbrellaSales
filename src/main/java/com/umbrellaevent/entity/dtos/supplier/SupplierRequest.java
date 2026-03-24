package com.umbrellaevent.entity.dtos.supplier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierRequest {
    private String name;
    private String email;
    private String contact;
    private String gstNo;
    private String address;
    private String supplyType;
}
