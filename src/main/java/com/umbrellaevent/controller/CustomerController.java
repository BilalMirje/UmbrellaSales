package com.umbrellaevent.controller;
import java.util.UUID;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umbrellaevent.config.AuditorAwareImpl;
import com.umbrellaevent.entity.dtos.customer.CustomerPaymentRequest;
import com.umbrellaevent.entity.dtos.customer.CustomerRequest;
import com.umbrellaevent.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;




@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Customer Management", description = "APIs for managing customers")
public class CustomerController {

    private final CustomerService customerService;
    private final AuditorAwareImpl auditorAware;

    @PostMapping("/create-customer")
    public ResponseEntity<?> createCustomer(@RequestBody CustomerRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.createCustomer(request, username));
    }

    @GetMapping("/get-customer")
    public ResponseEntity<?> getCustomer(@RequestParam UUID id) {
        return ResponseEntity.ok(customerService.getCustomerWithCreditById(id));
    }

    @GetMapping("/get-all-customers")
    public ResponseEntity<?> getAllCustomers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Sort sort) {

        if (page != null && size != null) {
            Pageable pageable = sort != null ?
                    PageRequest.of(page, size, sort) :
                    PageRequest.of(page, size);

            Page<?> resp = customerService.getAllCustomersWithCredit(pageable);
            return resp.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resp);
        }

        List<?> resp = customerService.getAllCustomersWithCredit();
        return resp.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(resp);
    }

    @PutMapping("/update-customer")
    public ResponseEntity<?> updateCustomer(
            @RequestParam UUID id,
            @RequestBody CustomerRequest request) {

        String username = auditorAware.getCurrentAuditor().get();
        return ResponseEntity.ok(customerService.updateCustomer(id, request, username));
    }

    @DeleteMapping("/delete-customer")
    public ResponseEntity<?> deleteCustomer(@RequestParam UUID id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

  @GetMapping("/search-by-contact")
public ResponseEntity<?> searchByContact(@RequestParam String contact) {

    try {
        return ResponseEntity.ok(customerService.getCustomerByContact(contact));
    } 
    catch (NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); 
    }
}

@PostMapping("/pay")
public ResponseEntity<Void> payCustomer(
        @RequestBody CustomerPaymentRequest request) {

    String username = auditorAware.getCurrentAuditor().get();

    customerService.payCustomer(
            request.getCustomerId(),
            request.getPaidAmount(),
            username);

    return ResponseEntity.noContent().build(); //  204
}
@GetMapping("/payment-history")
public ResponseEntity<?> getPaymentHistory(@RequestParam UUID customerId) {
    return ResponseEntity.ok(
        customerService.getCustomerPaymentHistory(customerId)
    );
}

}
