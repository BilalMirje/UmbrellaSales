package com.umbrellaevent.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.SellStock;

@Repository
public interface SellStockRepository extends JpaRepository<SellStock, UUID> {
    Optional<SellStock> findByBarcode(String barcode);
}
