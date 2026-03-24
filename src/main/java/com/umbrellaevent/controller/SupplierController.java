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
import com.umbrellaevent.entity.dtos.supplier.SupplierRequest;
import com.umbrellaevent.entity.dtos.supplier.SupplierResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierWithCreditResponse;
import com.umbrellaevent.service.SupplierService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Supplier Management", description = "APIs for managing suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final AuditorAwareImpl auditorAware;

    @Operation(summary = "Create a new supplier", description = "Creates a new supplier with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Supplier created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "GST No already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create-supplier")
    public ResponseEntity<?> createSupplier(@RequestBody SupplierRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        SupplierResponse response = supplierService.createSupplier(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get supplier by ID", description = "Retrieves a supplier by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier found"),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-supplier")
    public ResponseEntity<?> getSupplier(@RequestParam UUID id) {
        SupplierWithCreditResponse response = supplierService.getSupplierWithCreditById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all suppliers", description = "Retrieves all active suppliers with optional pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suppliers retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-all-suppliers")
    public ResponseEntity<?> getAllSuppliers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Sort sort) {

        if (page != null && size != null) {
            // Pagination with optional sorting
            Pageable pageable;
            if (sort != null) {
                pageable = PageRequest.of(page, size, sort);
            } else {
                pageable = PageRequest.of(page, size);
            }
            Page<SupplierWithCreditResponse> responses = supplierService.getAllSuppliersWithCredit(pageable);
            return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
        } else {
            // Return all suppliers without pagination
            List<SupplierWithCreditResponse> responses = supplierService.getAllSuppliersWithCredit();
            return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
        }
    }

    @Operation(summary = "Update supplier", description = "Updates an existing supplier by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "409", description = "GST No already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update-supplier")
    public ResponseEntity<?> updateSupplier(@RequestParam UUID id, @RequestBody SupplierRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        SupplierResponse response = supplierService.updateSupplier(id, request, username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete supplier", description = "Permanently deletes a supplier by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Supplier deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete-supplier")
    public ResponseEntity<?> deleteSupplier(@RequestParam UUID id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
