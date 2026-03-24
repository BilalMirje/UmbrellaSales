package com.umbrellaevent.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.Sell;

@Repository
public interface SellRepository extends JpaRepository<Sell, UUID> {
    Optional<Sell> findByInvoiceNo(String invoiceNo);
}

