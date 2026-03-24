package com.umbrellaevent.repository;



import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.CustomerCredit;

@Repository
public interface CustomerCreditRepository extends JpaRepository<CustomerCredit, UUID> {
  
 
}

