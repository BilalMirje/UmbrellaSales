package com.umbrellaevent.service;

import java.util.List;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.umbrellaevent.entity.dtos.supplier.SupplierBillRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierBillResponse;

public interface SupplierBillService {
    SupplierBillResponse createSupplierBill(SupplierBillRequest request, String username);
    SupplierBillResponse getSupplierBillById(UUID id);
    List<SupplierBillResponse> getAllSupplierBills();
    Page<SupplierBillResponse> getAllSupplierBills(Pageable pageable);
    SupplierBillResponse updateSupplierBill(UUID id, SupplierBillRequest request, String username);

    void deleteSupplierBill(UUID id);



}
