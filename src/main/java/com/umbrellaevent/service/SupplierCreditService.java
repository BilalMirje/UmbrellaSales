package com.umbrellaevent.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.umbrellaevent.entity.dtos.supplier.SupplierCreditRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditResponse;

public interface SupplierCreditService {
    SupplierCreditResponse createSupplierCredit(SupplierCreditRequest request, String username);
    SupplierCreditResponse getSupplierCreditById(UUID id);
    List<SupplierCreditResponse> getAllSupplierCredits();
    Page<SupplierCreditResponse> getAllSupplierCredits(Pageable pageable);
    SupplierCreditResponse updateSupplierCredit(UUID id, SupplierCreditRequest request, String username);
    void deleteSupplierCredit(UUID id);
}
