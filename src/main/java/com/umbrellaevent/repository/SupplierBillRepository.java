package com.umbrellaevent.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.Purchase;
import com.umbrellaevent.entity.SupplierBill;

@Repository
public interface SupplierBillRepository extends JpaRepository<SupplierBill, UUID> {

    Optional<SupplierBill> findByPurchase(Purchase purchase);

}
