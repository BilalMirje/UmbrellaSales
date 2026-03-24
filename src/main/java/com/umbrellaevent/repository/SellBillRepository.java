package com.umbrellaevent.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.Sell;
import com.umbrellaevent.entity.SellBill;

@Repository
public interface SellBillRepository extends JpaRepository<SellBill, UUID> {
    SellBill findBySell(Sell sell);
    List<SellBill> findByCustomer_Id(UUID customerId);

}
