package com.umbrellaevent.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umbrellaevent.entity.dtos.purchase.ProductStockGroupResponse;
import com.umbrellaevent.entity.dtos.sell.CustomerLedgerResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierCreditResponse;
import com.umbrellaevent.entity.dtos.supplier.SupplierPurchaseHistoryListResponse;
import com.umbrellaevent.service.ReportsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Reports Management", description = "APIs for generating various reports")
public class ReportsController {

    private final ReportsService reportsService;

    @Operation(summary = "Get supplier purchase history", description = "Retrieves the complete purchase history for a specific supplier including material and supplier credit information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier purchase history retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Supplier not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/supplier-purchase-history")
    public ResponseEntity<?> getSupplierPurchaseHistory(@RequestParam UUID supplierId) {
        SupplierPurchaseHistoryListResponse response = reportsService.getSupplierPurchaseHistory(supplierId);
        if (response == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all product stocks with purchase details", description = "Retrieves all product stocks along with their associated purchase information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product stocks with purchase details retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-product-stocks-with-product")
    public ResponseEntity<?> getProductStocksWithPurchase() {
        List<ProductStockGroupResponse> response = reportsService.getProductStocksWithPurchase();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get supplier credit data", description = "Retrieves all raw data from the SupplierCredit table including all fields as is")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Supplier credit data retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-supplier-ledger")
    public ResponseEntity<?> getSupplierCreditData() {
        List<SupplierCreditResponse> response = reportsService.getSupplierCreditData();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get customer receivable ledger", description = "Retrieves the receivable ledger for all customers including total amount receivable, customer name, date, and contact number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer receivable ledger retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    
    @GetMapping("/get-customer-ledger")
    public ResponseEntity<?> getCustomerSellHistory(@RequestParam UUID customerId) {
        CustomerLedgerResponse response = reportsService.getCustomerSellLedger(customerId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/get-all-customer-ledgers")
public ResponseEntity<?> getAllCustomerSellLedgers() {
    List<CustomerLedgerResponse> response = reportsService.getAllCustomerSellLedgers();
    return ResponseEntity.ok(response);
}
 @GetMapping("/customer-ledger-summary")
 public ResponseEntity<?> getCustomerLedgerSummary() {
     return ResponseEntity.ok(reportsService.getCustomerLedgerSummary());
 }



   @GetMapping("/customer-ledger-detail")
    public ResponseEntity<?> getCustomerLedgerDetail(@RequestParam UUID customerId) {
        return ResponseEntity.ok(
            reportsService.getCustomerSellLedger(customerId)
        );
    }
}
