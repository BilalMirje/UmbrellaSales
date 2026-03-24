package com.umbrellaevent.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.Purchase;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, UUID> {
    Optional<Purchase> findByInvoiceNo(String invoiceNo);
    Optional<Purchase> findByProductStocks_Id(UUID productStockId);

}
