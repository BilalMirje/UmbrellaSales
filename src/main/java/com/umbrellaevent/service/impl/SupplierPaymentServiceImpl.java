package com.umbrellaevent.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.umbrellaevent.config.AmountUtils;
import com.umbrellaevent.entity.PaymentType;
import com.umbrellaevent.entity.Supplier;
import com.umbrellaevent.entity.SupplierCredit;
import com.umbrellaevent.entity.SupplierPaymentHistory;
import com.umbrellaevent.entity.dtos.supplier.SupplierPaymentEntryResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPaymentHistoryResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPaymentRequest;
import com.umbrellaevent.repository.SupplierPaymentHistoryRepository;
import com.umbrellaevent.repository.SupplierRepository;
import com.umbrellaevent.service.SupplierPaymentService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupplierPaymentServiceImpl implements SupplierPaymentService {

    private final SupplierRepository supplierRepository;
    private final SupplierPaymentHistoryRepository paymentHistoryRepository;

    @Transactional
    @Override
    public void payToSupplier(SupplierPaymentRequest request, String username) {
        processPayment(request, PaymentType.CREDIT, username);
    }

    @Transactional
    @Override
    public void payFromSupplier(SupplierPaymentRequest request, String username) {
        processPayment(request, PaymentType.DEBIT, username);
    }

    @SuppressWarnings("null")
    private void processPayment(SupplierPaymentRequest request, PaymentType type, String username) {

        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        SupplierCredit credit = supplier.getSupplierCredit();

        double paidAmount = request.getPaidAmount();
        if (paidAmount <= 0) {
            throw new RuntimeException("Invalid payment amount");
        }

        if (type == PaymentType.CREDIT) {
            if (paidAmount > credit.getCreditAmount()) {
                throw new RuntimeException("Payment exceeds credit amount");
            }
            credit.setCreditAmount(
                    AmountUtils.roundToTwoDecimalPlaces(credit.getCreditAmount() - paidAmount)
            );
            credit.setPaid(
                    AmountUtils.roundToTwoDecimalPlaces(credit.getPaid() + paidAmount)
            );
        } else {
            if (paidAmount > credit.getDebitAmount()) {
                throw new RuntimeException("Payment exceeds debit amount");
            }
            credit.setDebitAmount(
                    AmountUtils.roundToTwoDecimalPlaces(credit.getDebitAmount() - paidAmount)
            );
        }

        credit.setUpdatedBy(username);
        supplierRepository.save(supplier);

        // Save payment history
        SupplierPaymentHistory history = new SupplierPaymentHistory();
        history.setSupplierId(request.getSupplierId());
        history.setPaidAmount(paidAmount);
        history.setType(type);
        history.setCreatedBy(username);
        history.setUpdatedBy(username);

        paymentHistoryRepository.save(history);
    }

    @SuppressWarnings("null")
    @Override
    public SupplierPaymentHistoryResponse getSupplierPaymentHistory(UUID supplierId) {

        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        List<SupplierPaymentEntryResponse> payments = paymentHistoryRepository.findBySupplierId(supplierId)
                .stream()
                .map(p -> new SupplierPaymentEntryResponse(
                        p.getId(),
                        p.getPaidAmount(),
                        p.getType().toString(),
                        p.getCreatedAt()
                ))
                .collect(Collectors.toList());

        if (payments.isEmpty()) {
            return null;
        }

        return new SupplierPaymentHistoryResponse(
                supplier.getId(),
                supplier.getName(),
                supplier.getContact(),
                payments
        );
    }
}
