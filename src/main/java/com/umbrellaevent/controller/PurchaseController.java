package com.umbrellaevent.controller;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umbrellaevent.config.AuditorAwareImpl;
import com.umbrellaevent.entity.dtos.purchase.ProductStockRequest;
import com.umbrellaevent.entity.dtos.purchase.ProductStockWithPurchaseResponse;
import com.umbrellaevent.entity.dtos.purchase.PurchaseRequest;
import com.umbrellaevent.entity.dtos.purchase.PurchaseResponse;
import com.umbrellaevent.service.PurchaseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

    @RestController
    @RequestMapping("/api/purchase")
    @RequiredArgsConstructor
    @CrossOrigin("*")
    @Tag(name = "Purchase Management", description = "APIs for managing purchases")
    public class PurchaseController {

        private final PurchaseService purchaseService;
        private final AuditorAwareImpl auditorAware;
        private final ObjectMapper objectMapper;

        @Operation(summary = "Create a new purchase", description = "Creates a new purchase with supplier credit and product stock information")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "201", description = "Purchase created successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "404", description = "Supplier not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PostMapping(value = "/create-purchase", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> createPurchase(@RequestParam("purchaseData") String purchaseData,
                                                @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {
            PurchaseRequest request = objectMapper.readValue(purchaseData, PurchaseRequest.class);

            // Assign images to product stocks if provided
            if (images != null && images.length > 0) {
                List<ProductStockRequest> productStocks = request.getProductStocks();
                for (int i = 0; i < productStocks.size() && i < images.length; i++) {
                    productStocks.get(i).setImage(images[i]);
                }
            }

            String username = auditorAware.getCurrentAuditor().get();
            PurchaseResponse response = purchaseService.createPurchase(request, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        

        @Operation(summary = "Get purchase by ID", description = "Retrieves a purchase by its ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Purchase found"),
                @ApiResponse(responseCode = "404", description = "Purchase not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/get-purchase")
        public ResponseEntity<?> getPurchase(@RequestParam UUID id) {
            PurchaseResponse response = purchaseService.getPurchaseById(id);
            return ResponseEntity.ok(response);
        }

        // @Operation(summary = "Get purchase by article number", description = "Retrieves a purchase by its article number")
        // @ApiResponses(value = {
        //         @ApiResponse(responseCode = "200", description = "Purchase found"),
        //         @ApiResponse(responseCode = "404", description = "Purchase not found"),
        //         @ApiResponse(responseCode = "500", description = "Internal server error")
        // })
        // @GetMapping("/get-purchase-by-article-no")
        // public ResponseEntity<?> getPurchaseByArticleNo(@RequestParam String articleNo) {
        //     PurchaseResponse response = purchaseService.getPurchaseByArticleNo(articleNo);
        //     return ResponseEntity.ok(response);
        // }

        @Operation(summary = "Get all purchases", description = "Retrieves all purchases with optional pagination")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Purchases retrieved successfully"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/get-all-purchases")
        public ResponseEntity<?> getAllPurchases(
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
                Page<PurchaseResponse> responses = purchaseService.getAllPurchases(pageable);
                return responses.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(responses);
            } else {
                // Return all purchases without pagination
                List<PurchaseResponse> responses = purchaseService.getAllPurchases();
                return responses.isEmpty()
                        ? ResponseEntity.noContent().build()
                        : ResponseEntity.ok(responses);
            }
        }

        @Operation(summary = "Update purchase", description = "Updates an existing purchase by ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Purchase updated successfully"),
                @ApiResponse(responseCode = "400", description = "Invalid input data"),
                @ApiResponse(responseCode = "404", description = "Purchase or supplier not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @PutMapping(value = "/update-purchase", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<?> updatePurchase(@RequestParam UUID id,
                                                @RequestParam String purchaseData) throws IOException {
            PurchaseRequest request = objectMapper.readValue(purchaseData, PurchaseRequest.class);
            String username = auditorAware.getCurrentAuditor().get();
            PurchaseResponse response = purchaseService.updatePurchase(id, request, username);
            return ResponseEntity.ok(response);
        }

        @Operation(summary = "Get product stock by barcode", description = "Retrieves product stock information with purchase details by its barcode")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Product stock found"),
                @ApiResponse(responseCode = "404", description = "Product stock not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/get-stock-by-barcode")
        public ResponseEntity<?> getStockByBarcode(@RequestParam String barcode) {
            ProductStockWithPurchaseResponse response = purchaseService.getStockByBarcodeWithPurchase(barcode);
            return ResponseEntity.ok(response);
        }

        @Operation(summary = "Delete purchase", description = "Deletes a purchase by ID")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "204", description = "Purchase deleted successfully"),
                @ApiResponse(responseCode = "404", description = "Purchase not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @DeleteMapping("/delete-purchase")
        public ResponseEntity<?> deletePurchase(@RequestParam UUID id) {
            purchaseService.deletePurchase(id);
            return ResponseEntity.ok().build();
        }

        @Operation(summary = "Get next purchase invoice number", description = "Returns the next available purchase invoice number without saving")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Next invoice number retrieved successfully"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/get-next-purchase-invoice-no")
        public ResponseEntity<String> getNextPurchaseInvoiceNo() {
            String nextInvoiceNo = purchaseService.getNextPurchaseInvoiceNo();
            return ResponseEntity.ok(nextInvoiceNo);
        }

        @Operation(summary = "Get purchase by invoice number", description = "Retrieves a purchase by its invoice number")
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "Purchase found"),
                @ApiResponse(responseCode = "404", description = "Purchase not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
        })
        @GetMapping("/get-purchase-by-invoice-no")
        public ResponseEntity<?> getPurchaseByInvoiceNo(@RequestParam String invoiceNo) {
            PurchaseResponse response = purchaseService.getPurchaseByInvoiceNo(invoiceNo);
            return ResponseEntity.ok(response);
        }


    }
