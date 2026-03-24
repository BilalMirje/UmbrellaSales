package com.umbrellaevent.service;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.umbrellaevent.entity.dtos.customer.CustomerPaymentHistoryResponse;
import com.umbrellaevent.entity.dtos.customer.CustomerRequest;
import com.umbrellaevent.entity.dtos.customer.CustomerResponse;
import com.umbrellaevent.entity.dtos.customer.CustomerWithCreditResponse;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request, String username);

    CustomerWithCreditResponse getCustomerWithCreditById(UUID id);

    List<CustomerWithCreditResponse> getAllCustomersWithCredit();

    Page<CustomerWithCreditResponse> getAllCustomersWithCredit(Pageable pageable);

    CustomerResponse updateCustomer(UUID id, CustomerRequest request, String username);

    void deleteCustomer(UUID id);

   CustomerResponse getCustomerByContact(String contact);

   void payCustomer(UUID customerId, double paidAmount, String username);
CustomerPaymentHistoryResponse getCustomerPaymentHistory(UUID customerId);

}

