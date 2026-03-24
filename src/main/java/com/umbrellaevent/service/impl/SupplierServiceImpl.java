package com.umbrellaevent.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umbrellaevent.config.AmountUtils;
import com.umbrellaevent.entity.Supplier;
import com.umbrellaevent.entity.SupplierCredit;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierWithCreditResponse;
import com.umbrellaevent.repository.SupplierRepository;
import com.umbrellaevent.service.SupplierService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
        @Transactional
        public SupplierResponse createSupplier(SupplierRequest request, String username) {
            if (supplierRepository.findByGstNo(request.getGstNo()).isPresent()) {
                throw new RuntimeException("GST No already exists");
            }

        // Create default SupplierCredit with zero values
        SupplierCredit supplierCredit = new SupplierCredit();
        supplierCredit.setToPay(AmountUtils.roundToTwoDecimalPlaces(0.0));
        supplierCredit.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(0.0));
        supplierCredit.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(0.0));
        supplierCredit.setPaid(AmountUtils.roundToTwoDecimalPlaces(0.0));
        supplierCredit.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(0.0));

        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setContact(request.getContact());
        supplier.setGstNo(request.getGstNo());
        supplier.setAddress(request.getAddress());
        supplier.setSupplyType(request.getSupplyType());
        supplier.setSupplierCredit(supplierCredit);

        Supplier saved = supplierRepository.save(supplier);
        return mapToResponse(saved);
    }

    @Override
    public SupplierResponse getSupplierById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return mapToResponse(supplier);
    }

    @Override
    public List<SupplierResponse> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SupplierResponse> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    public SupplierWithCreditResponse getSupplierWithCreditById(UUID id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return mapToResponseWithCredit(supplier);
    }

    @Override
    public List<SupplierWithCreditResponse> getAllSuppliersWithCredit() {
        return supplierRepository.findAll().stream()
                .map(this::mapToResponseWithCredit)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SupplierWithCreditResponse> getAllSuppliersWithCredit(Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(this::mapToResponseWithCredit);
    }

    @Override
    @Transactional
    public SupplierResponse updateSupplier(UUID id, SupplierRequest request, String username) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        if (!supplier.getGstNo().equals(request.getGstNo()) &&
            supplierRepository.findByGstNo(request.getGstNo()).isPresent()) {
            throw new RuntimeException("GST No already exists");
        }

        supplier.setName(request.getName());
        supplier.setEmail(request.getEmail());
        supplier.setContact(request.getContact());
        supplier.setGstNo(request.getGstNo());
        supplier.setAddress(request.getAddress());
        supplier.setSupplyType(request.getSupplyType());


        Supplier updated = supplierRepository.save(supplier);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSupplier(UUID id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found");
        }
        supplierRepository.deleteById(id);
    }

    private SupplierResponse mapToResponse(Supplier supplier) {
        SupplierResponse response = new SupplierResponse();
        response.setId(supplier.getId());
        response.setName(supplier.getName());
        response.setEmail(supplier.getEmail());
        response.setContact(supplier.getContact());
        response.setGstNo(supplier.getGstNo());
        response.setAddress(supplier.getAddress());
        response.setSupplyType(supplier.getSupplyType());
        response.setCreatedAt(supplier.getCreatedAt());
        response.setUpdatedAt(supplier.getUpdatedAt());
        response.setCreatedBy(supplier.getCreatedBy());
        response.setUpdatedBy(supplier.getUpdatedBy());
        return response;
    }

    private SupplierWithCreditResponse mapToResponseWithCredit(Supplier supplier) {
        SupplierWithCreditResponse response = new SupplierWithCreditResponse();
        response.setId(supplier.getId());
        response.setName(supplier.getName());
        response.setEmail(supplier.getEmail());
        response.setContact(supplier.getContact());
        response.setGstNo(supplier.getGstNo());
        response.setAddress(supplier.getAddress());
        response.setSupplyType(supplier.getSupplyType());
        response.setCreatedAt(supplier.getCreatedAt());
        response.setUpdatedAt(supplier.getUpdatedAt());
        response.setCreatedBy(supplier.getCreatedBy());
        response.setUpdatedBy(supplier.getUpdatedBy());

        // Map supplier credit
        SupplierCredit supplierCredit = supplier.getSupplierCredit();
        if (supplierCredit != null) {
            SupplierCreditResponse creditResponse = new SupplierCreditResponse();
            creditResponse.setId(supplierCredit.getId());
            creditResponse.setToPay(supplierCredit.getToPay());
            creditResponse.setDebitAmount(supplierCredit.getDebitAmount());
            creditResponse.setCreditAmount(supplierCredit.getCreditAmount());
            creditResponse.setPaid(supplierCredit.getPaid());
            creditResponse.setTotalAmount(supplierCredit.getTotalAmount());
            creditResponse.setCreatedAt(supplierCredit.getCreatedAt());
            creditResponse.setUpdatedAt(supplierCredit.getUpdatedAt());
            creditResponse.setCreatedBy(supplierCredit.getCreatedBy());
            creditResponse.setUpdatedBy(supplierCredit.getUpdatedBy());
            response.setSupplierCredit(creditResponse);
        }

        return response;
    }
}
