package com.umbrellaevent.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umbrellaevent.config.AmountUtils;
import com.umbrellaevent.entity.SupplierCredit;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditResponse;
import com.umbrellaevent.repository.SupplierCreditRepository;
import com.umbrellaevent.service.SupplierCreditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SupplierCreditServiceImpl implements SupplierCreditService {

    private final SupplierCreditRepository supplierCreditRepository;

    @Override
    @Transactional
    public SupplierCreditResponse createSupplierCredit(SupplierCreditRequest request, String username) {
        SupplierCredit supplierCredit = new SupplierCredit();
        supplierCredit.setToPay(AmountUtils.roundToTwoDecimalPlaces(request.getToPay()));
        supplierCredit.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(request.getDebitAmount()));
        supplierCredit.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(request.getCreditAmount()));
        supplierCredit.setPaid(AmountUtils.roundToTwoDecimalPlaces(request.getPaid()));
        supplierCredit.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(request.getTotalAmount()));

        SupplierCredit saved = supplierCreditRepository.save(supplierCredit);
        return mapToResponse(saved);
    }

    @Override
    public SupplierCreditResponse getSupplierCreditById(UUID id) {
        SupplierCredit supplierCredit = supplierCreditRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SupplierCredit not found"));
        return mapToResponse(supplierCredit);
    }

    @Override
    public List<SupplierCreditResponse> getAllSupplierCredits() {
        return supplierCreditRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SupplierCreditResponse> getAllSupplierCredits(Pageable pageable) {
        return supplierCreditRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public SupplierCreditResponse updateSupplierCredit(UUID id, SupplierCreditRequest request, String username) {
        SupplierCredit supplierCredit = supplierCreditRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SupplierCredit not found"));

        supplierCredit.setToPay(AmountUtils.roundToTwoDecimalPlaces(request.getToPay()));
        supplierCredit.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(request.getDebitAmount()));
        supplierCredit.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(request.getCreditAmount()));
        supplierCredit.setPaid(AmountUtils.roundToTwoDecimalPlaces(request.getPaid()));
        supplierCredit.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(request.getTotalAmount()));

        SupplierCredit updated = supplierCreditRepository.save(supplierCredit);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSupplierCredit(UUID id) {
        if (!supplierCreditRepository.existsById(id)) {
            throw new RuntimeException("SupplierCredit not found");
        }
        supplierCreditRepository.deleteById(id);
    }

    private SupplierCreditResponse mapToResponse(SupplierCredit supplierCredit) {
        SupplierCreditResponse response = new SupplierCreditResponse();
        response.setId(supplierCredit.getId());
        response.setToPay(supplierCredit.getToPay());
        response.setDebitAmount(supplierCredit.getDebitAmount());
        response.setCreditAmount(supplierCredit.getCreditAmount());
        response.setPaid(supplierCredit.getPaid());
        response.setTotalAmount(supplierCredit.getTotalAmount());
        response.setCreatedAt(supplierCredit.getCreatedAt());
        response.setUpdatedAt(supplierCredit.getUpdatedAt());
        response.setCreatedBy(supplierCredit.getCreatedBy());
        response.setUpdatedBy(supplierCredit.getUpdatedBy());
        return response;
    }
}
