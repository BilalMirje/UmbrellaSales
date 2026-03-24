package com.umbrellaevent.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.CustomerPaymentHistory;

@Repository
public interface CustomerPaymentHistoryRepository
        extends JpaRepository<CustomerPaymentHistory, UUID> {

    List<CustomerPaymentHistory> findByCustomer_Id(UUID customerId);
}