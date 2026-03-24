package com.umbrellaevent.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.PrinterConfig;

@Repository
public interface PrinterConfigRepository extends JpaRepository<PrinterConfig, UUID> {
    List<PrinterConfig> findByIsActiveTrue();
    @Query("SELECT p FROM PrinterConfig p WHERE p.isActive = true")
    Optional<PrinterConfig> findActivePrinter();
}
