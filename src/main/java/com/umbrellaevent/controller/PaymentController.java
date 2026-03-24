package com.umbrellaevent.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umbrellaevent.config.AuditorAwareImpl;
import com.umbrellaevent.entity.dtos.supplier.SupplierPaymentHistoryResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPaymentRequest;
import com.umbrellaevent.service.SupplierPaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Supplier Payment Management", description = "APIs for managing supplier payments")
public class PaymentController {

    private final SupplierPaymentService supplierPaymentService;
    private final AuditorAwareImpl auditorAware;

    @Operation(
            summary = "Pay to supplier",
            description = "Reduce amount from CREDIT (amount we need to pay to supplier)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/to-supplier")
    public ResponseEntity<?> payToSupplier(@RequestBody SupplierPaymentRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        supplierPaymentService.payToSupplier(request, username);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Receive payment from supplier",
            description = "Reduce amount from DEBIT (amount supplier needs to pay us)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Payment processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/from-supplier")
    public ResponseEntity<?> payFromSupplier(@RequestBody SupplierPaymentRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        supplierPaymentService.payFromSupplier(request, username);
        return ResponseEntity.ok().build();
    }


    @Operation(
            summary = "Get supplier payment history",
            description = "Retrieve full payment history for selected supplier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Supplier not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/supplier-payment-history")
    public ResponseEntity<?> getSupplierPaymentHistory(
            @Parameter(description = "Supplier unique ID") @RequestParam UUID supplierId
    ) {
        SupplierPaymentHistoryResponse response = supplierPaymentService.getSupplierPaymentHistory(supplierId);
        if(response == null){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }
}