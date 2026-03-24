package com.umbrellaevent.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.umbrellaevent.config.AmountUtils;
import com.umbrellaevent.entity.Customer;
import com.umbrellaevent.entity.CustomerCredit;
import com.umbrellaevent.entity.CustomerPaymentHistory;
import com.umbrellaevent.entity.dtos.customer.CustomerPaymentEntryResponse;
import com.umbrellaevent.entity.dtos.customer.CustomerPaymentHistoryResponse;
import com.umbrellaevent.entity.dtos.customer.CustomerRequest;
import com.umbrellaevent.entity.dtos.customer.CustomerResponse;
import com.umbrellaevent.entity.dtos.customer.CustomerWithCreditResponse;
import com.umbrellaevent.entity.dtos.sell.CustomerCreditResponse;
import com.umbrellaevent.repository.CustomerPaymentHistoryRepository;
import com.umbrellaevent.repository.CustomerRepository;
import com.umbrellaevent.service.CustomerService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerPaymentHistoryRepository paymentHistoryRepository;



    @Override
    public CustomerResponse createCustomer(CustomerRequest request, String username) {

        if (customerRepository.findAll()
                .stream()
                .anyMatch(c -> c.getGstNo().equals(request.getGstNo()))) {
            throw new RuntimeException("GST No already exists");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setContact(request.getContact());
        customer.setGstNo(request.getGstNo());
        customer.setAddress(request.getAddress());
        customer.setSupplyType(request.getSupplyType());
        customer.setCreatedBy(username);
        customer.setUpdatedBy(username);

        CustomerCredit credit = new CustomerCredit();
        credit.setToPay(0.0);
        credit.setDebitAmount(0.0);
        credit.setCreditAmount(0.0);
        credit.setPaid(0.0);
        credit.setTotalAmount(0.0);

        customer.setCustomerCredit(credit);

        Customer saved = customerRepository.save(customer);

        return mapToResponse(saved);
    }

    @Override
    public CustomerWithCreditResponse getCustomerWithCreditById(UUID id) {
        @SuppressWarnings("null")
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return mapToWithCreditResponse(c);
    }

    @Override
    public List<CustomerWithCreditResponse> getAllCustomersWithCredit() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToWithCreditResponse)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Override
    public Page<CustomerWithCreditResponse> getAllCustomersWithCredit(Pageable pageable) {
        Page<Customer> page = customerRepository.findAll(pageable);
        return page.map(this::mapToWithCreditResponse);
    }

    @Override
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request, String username) {
        @SuppressWarnings("null")
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        c.setName(request.getName());
        c.setEmail(request.getEmail());
        c.setContact(request.getContact());
        c.setGstNo(request.getGstNo());
        c.setAddress(request.getAddress());
        c.setSupplyType(request.getSupplyType());
        c.setUpdatedBy(username);

        Customer updated = customerRepository.save(c);
        return mapToResponse(updated);
    }

    @SuppressWarnings("null")
    @Override
    public void deleteCustomer(UUID id) {
        @SuppressWarnings("null")
        Customer c = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customerRepository.delete(c);
    }

    // ---------- MAPPERS ----------
    private CustomerResponse mapToResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getContact(),
                c.getGstNo(), c.getAddress(), c.getSupplyType(),
                c.getCreatedAt(), c.getUpdatedAt(), c.getCreatedBy(), c.getUpdatedBy()
        );
    }

    private CustomerWithCreditResponse mapToWithCreditResponse(Customer c) {
        CustomerCredit credit = c.getCustomerCredit();
        CustomerCreditResponse creditResp = new CustomerCreditResponse(
                credit.getId(), c.getName(), c.getContact(),
                credit.getPaid(), (credit.getCreditAmount() - credit.getPaid()),
                credit.getTotalAmount(),
                credit.getCreatedAt(), credit.getUpdatedAt(),
                credit.getCreatedBy(), credit.getUpdatedBy());

        return new CustomerWithCreditResponse(
                c.getId(), c.getName(), c.getEmail(), c.getContact(),
                c.getGstNo(), c.getAddress(), c.getSupplyType(),
                creditResp,
                c.getCreatedAt(), c.getUpdatedAt(),
                c.getCreatedBy(), c.getUpdatedBy());
    }
    
 @Override
 public CustomerResponse getCustomerByContact(String contact) {
     Customer c = customerRepository.findByContact(contact)
             .orElseThrow(() -> new NoSuchElementException("Customer not found"));
     return mapToResponse(c);
 }


@SuppressWarnings("null")
@Transactional
@Override
public void payCustomer(UUID customerId, double paidAmount, String username) {

    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

    CustomerCredit credit = customer.getCustomerCredit();

    double remaining = credit.getTotalAmount() - credit.getPaid();

    if (paidAmount <= 0)
        throw new RuntimeException("Invalid payment amount");

    if (paidAmount > remaining)
        throw new RuntimeException("Payment exceeds remaining");

    //  update credit
    credit.setPaid(
        AmountUtils.roundToTwoDecimalPlaces(credit.getPaid() + paidAmount)
    );

    credit.setToPay(
        AmountUtils.roundToTwoDecimalPlaces(credit.getTotalAmount() - credit.getPaid())
    );

    credit.setUpdatedBy(username);

    //  save payment history
    CustomerPaymentHistory history = new CustomerPaymentHistory();
    history.setCustomer(customer);
    history.setPaidAmount(paidAmount);
    history.setPaymentDate(LocalDate.now());
    history.setCreatedBy(username);

    paymentHistoryRepository.save(history);
}

@SuppressWarnings("null")
@Override
public CustomerPaymentHistoryResponse getCustomerPaymentHistory(UUID customerId) {

    Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new RuntimeException("Customer not found"));

    List<CustomerPaymentEntryResponse> payments =
        paymentHistoryRepository.findByCustomer_Id(customerId)
            .stream()
            .map(p -> new CustomerPaymentEntryResponse(
                    p.getId(),
                    p.getPaidAmount(),
                    p.getPaymentDate()
            ))
            .collect(Collectors.toList());

    return new CustomerPaymentHistoryResponse(
            customer.getId(),
            customer.getName(),
            customer.getContact(),
            payments
    );
}



}

