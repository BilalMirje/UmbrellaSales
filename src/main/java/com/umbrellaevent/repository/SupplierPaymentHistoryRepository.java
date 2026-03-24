package com.umbrellaevent.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.SupplierPaymentHistory;

@Repository
public interface SupplierPaymentHistoryRepository extends JpaRepository<SupplierPaymentHistory, UUID> {

    List<SupplierPaymentHistory> findBySupplierId(UUID supplierId);
}
