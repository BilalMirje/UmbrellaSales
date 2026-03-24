package com.umbrellaevent.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.umbrellaevent.entity.dtos.supplier.SupplierRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierWithCreditResponse;

public interface SupplierService {
    SupplierResponse createSupplier(SupplierRequest request, String username);
    SupplierWithCreditResponse getSupplierWithCreditById(UUID id);
    List<SupplierWithCreditResponse> getAllSuppliersWithCredit();
    Page<SupplierWithCreditResponse> getAllSuppliersWithCredit(Pageable pageable);
    SupplierResponse updateSupplier(UUID id, SupplierRequest request, String username);
    void deleteSupplier(UUID id);
	SupplierResponse getSupplierById(UUID id);
    List<SupplierResponse> getAllSuppliers();
    Page<SupplierResponse> getAllSuppliers(Pageable pageable);
}
