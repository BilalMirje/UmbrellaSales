package com.umbrellaevent.service;

import java.util.UUID;

import com.umbrellaevent.entity.dtos.supplier.SupplierPaymentHistoryResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPaymentRequest;

public interface SupplierPaymentService {

    void payToSupplier(SupplierPaymentRequest request, String username);

    void payFromSupplier(SupplierPaymentRequest request, String username);

    SupplierPaymentHistoryResponse getSupplierPaymentHistory(UUID supplierId);
}
