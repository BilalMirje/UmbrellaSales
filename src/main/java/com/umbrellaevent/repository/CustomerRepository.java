package com.umbrellaevent.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umbrellaevent.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
     Optional<Customer> findByContact(String contact);
}

