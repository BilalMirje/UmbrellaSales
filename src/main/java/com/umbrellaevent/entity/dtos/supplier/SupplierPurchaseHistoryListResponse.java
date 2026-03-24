package com.umbrellaevent.entity.dtos.supplier;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierPurchaseHistoryListResponse {
    private List<SupplierPurchaseHistoryResponse> purchases;
}
