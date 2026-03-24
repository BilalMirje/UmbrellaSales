package com.umbrellaevent.service.impl;

import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umbrellaevent.config.AmountUtils;
import com.umbrellaevent.entity.Purchase;

import com.umbrellaevent.entity.Supplier;
import com.umbrellaevent.entity.SupplierBill;
import com.umbrellaevent.entity.dtos.supplier.SupplierBillRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierBillResponse;
import com.umbrellaevent.repository.PurchaseRepository;
import com.umbrellaevent.repository.SupplierBillRepository;
import com.umbrellaevent.repository.SupplierRepository;
import com.umbrellaevent.service.SupplierBillService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SupplierBillServiceImpl implements SupplierBillService {

    private final SupplierBillRepository supplierBillRepository;
    private final PurchaseRepository purchaseRepository;
    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public SupplierBillResponse createSupplierBill(SupplierBillRequest request, String username) {
        Purchase purchase = purchaseRepository.findById(request.getPurchaseId())
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        SupplierBill supplierBill = new SupplierBill();
        supplierBill.setPurchase(purchase);
        supplierBill.setSupplier(supplier);
        supplierBill.setToPay(AmountUtils.roundToTwoDecimalPlaces(request.getToPay()));
        supplierBill.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(request.getDebitAmount()));
        supplierBill.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(request.getCreditAmount()));
        supplierBill.setPaid(AmountUtils.roundToTwoDecimalPlaces(request.getPaid()));
        supplierBill.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(request.getTotalAmount()));

        SupplierBill saved = supplierBillRepository.save(supplierBill);
        return mapToResponse(saved);
    }

    @Override
    public SupplierBillResponse getSupplierBillById(UUID id) {
        SupplierBill supplierBill = supplierBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SupplierBill not found"));
        return mapToResponse(supplierBill);
    }

    @Override
    public List<SupplierBillResponse> getAllSupplierBills() {
        return supplierBillRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Page<SupplierBillResponse> getAllSupplierBills(Pageable pageable) {
        return supplierBillRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public SupplierBillResponse updateSupplierBill(UUID id, SupplierBillRequest request, String username) {
        SupplierBill supplierBill = supplierBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("SupplierBill not found"));

        Purchase purchase = purchaseRepository.findById(request.getPurchaseId())
                .orElseThrow(() -> new RuntimeException("Purchase not found"));

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        supplierBill.setPurchase(purchase);
        supplierBill.setSupplier(supplier);
        supplierBill.setToPay(AmountUtils.roundToTwoDecimalPlaces(request.getToPay()));
        supplierBill.setDebitAmount(AmountUtils.roundToTwoDecimalPlaces(request.getDebitAmount()));
        supplierBill.setCreditAmount(AmountUtils.roundToTwoDecimalPlaces(request.getCreditAmount()));
        supplierBill.setPaid(AmountUtils.roundToTwoDecimalPlaces(request.getPaid()));
        supplierBill.setTotalAmount(AmountUtils.roundToTwoDecimalPlaces(request.getTotalAmount()));

        SupplierBill updated = supplierBillRepository.save(supplierBill);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteSupplierBill(UUID id) {
        if (!supplierBillRepository.existsById(id)) {
            throw new RuntimeException("SupplierBill not found");
        }
        supplierBillRepository.deleteById(id);
    }

    private SupplierBillResponse mapToResponse(SupplierBill supplierBill) {
        SupplierBillResponse response = new SupplierBillResponse();
        response.setId(supplierBill.getId());
        response.setInvoiceNo(supplierBill.getInvoiceNo());
        response.setToPay(supplierBill.getToPay());
        response.setDebitAmount(supplierBill.getDebitAmount());
        response.setCreditAmount(supplierBill.getCreditAmount());
        response.setPaid(supplierBill.getPaid());
        response.setTotalAmount(supplierBill.getTotalAmount());
        response.setCreatedAt(supplierBill.getCreatedAt());
        response.setUpdatedAt(supplierBill.getUpdatedAt());
        response.setCreatedBy(supplierBill.getCreatedBy());
        response.setUpdatedBy(supplierBill.getUpdatedBy());
        return response;
    }

 
}
