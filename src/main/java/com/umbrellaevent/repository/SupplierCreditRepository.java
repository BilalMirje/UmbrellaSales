package com.umbrellaevent.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.SupplierCredit;

@Repository
public interface SupplierCreditRepository extends JpaRepository<SupplierCredit, UUID> {

}
