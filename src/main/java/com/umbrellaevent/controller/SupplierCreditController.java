package com.umbrellaevent.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditResponse;
import com.umbrellaevent.service.SupplierCreditService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/supplier-credit")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Supplier Credit Management", description = "APIs for managing supplier credits")
public class SupplierCreditController {

    private final SupplierCreditService supplierCreditService;
    private final AuditorAwareImpl auditorAware;

    @Operation(summary = "Create a new supplier credit", description = "Creates a new supplier credit with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Supplier credit created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create-supplier-credit")
    public ResponseEntity<?> createSupplierCredit(@RequestBody SupplierCreditRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        SupplierCreditResponse response = supplierCreditService.createSupplierCredit(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get supplier credit by ID", description = "Retrieves a supplier credit by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier credit found"),
        @ApiResponse(responseCode = "404", description = "Supplier credit not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-supplier-credit")
    public ResponseEntity<?> getSupplierCredit(@RequestParam UUID id) {
        SupplierCreditResponse response = supplierCreditService.getSupplierCreditById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all supplier credits", description = "Retrieves all supplier credits with optional pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier credits retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-all-supplier-credits")
    public ResponseEntity<?> getAllSupplierCredits(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {

        if (page != null && size != null) {
            // Pagination with optional sorting
            Pageable pageable;
            if (sort != null && !sort.isEmpty()) {
                // Parse sort string, e.g., "createdAt,desc" or "createdAt,asc"
                String[] sortParams = sort.split(",");
                Sort sortObj = Sort.by(sortParams.length > 1 && "desc".equalsIgnoreCase(sortParams[1])
                    ? Sort.Direction.DESC : Sort.Direction.ASC, sortParams[0]);
                pageable = PageRequest.of(page, size, sortObj);
            } else {
                pageable = PageRequest.of(page, size);
            }
            Page<SupplierCreditResponse> responses = supplierCreditService.getAllSupplierCredits(pageable);
            return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
        } else {
            // Return all supplier credits without pagination
            List<SupplierCreditResponse> responses = supplierCreditService.getAllSupplierCredits();
            return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
        }
    }

    @Operation(summary = "Update supplier credit", description = "Updates an existing supplier credit by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier credit updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Supplier credit or supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update-supplier-credit")
    public ResponseEntity<?> updateSupplierCredit(@RequestParam UUID id, @RequestBody SupplierCreditRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        SupplierCreditResponse response = supplierCreditService.updateSupplierCredit(id, request, username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete supplier credit", description = "Deletes a supplier credit by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Supplier credit deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Supplier credit not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete-supplier-credit")
    public ResponseEntity<?> deleteSupplierCredit(@RequestParam UUID id) {
        supplierCreditService.deleteSupplierCredit(id);
        return ResponseEntity.noContent().build();
    }
}
