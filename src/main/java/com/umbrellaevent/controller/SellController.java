package com.umbrellaevent.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.umbrellaevent.entity.dtos.sell.SellRequest;
import com.umbrellaevent.entity.dtos.sell.SellResponse;
import com.umbrellaevent.config.AuditorAwareImpl;

import com.umbrellaevent.service.SellService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sell")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Sell Management", description = "APIs for managing sells")
public class SellController {

    private final SellService sellService;
    private final AuditorAwareImpl auditorAware;

    @Operation(summary = "Create a new sell", description = "Creates a new sell and updates product stock and customer credit")
    @PostMapping(value = "/create-sell", consumes = "application/json")
    public ResponseEntity<?> createSell(@RequestBody SellRequest request) {
        String username = auditorAware.getCurrentAuditor().orElse("system");
        SellResponse response = sellService.createSell(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get sell by ID")
    @GetMapping("/get-sell")
    public ResponseEntity<?> getSell(@RequestParam UUID id) {
        SellResponse response = sellService.getSellById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get sell by invoice no")
    @GetMapping("/get-sell-by-invoice-no")
    public ResponseEntity<?> getSellByInvoice(@RequestParam String invoiceNo) {
        SellResponse response = sellService.getSellByInvoiceNo(invoiceNo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all sells (optional pagination)")
    @GetMapping("/get-all-sells")
    public ResponseEntity<?> getAllSells(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        if (page != null && size != null) {
            Pageable pageable = PageRequest.of(page, size);
            Page<SellResponse> responses = sellService.getAllSells(pageable);
            return responses.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(responses);
        } else {
            List<SellResponse> list = sellService.getAllSells();
            return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
        }
    }

    @Operation(summary = "Delete sell")
    @DeleteMapping("/delete-sell")
    public ResponseEntity<?> deleteSell(@RequestParam UUID id) {
        sellService.deleteSell(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get next sell invoice number")
    @GetMapping("/get-next-sell-invoice-no")
    public ResponseEntity<String> getNextInvoiceNo() {
        String next = sellService.getNextSellInvoiceNumber();
        return ResponseEntity.ok(next);
    }

    @GetMapping("/search-by-invoice")
public ResponseEntity<?> searchByInvoice(@RequestParam String invoiceNo) {
    Optional<SellResponse> resp = sellService.findByInvoiceNo(invoiceNo);

    if (resp.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("No record found with invoice: " + invoiceNo);
    }

    return ResponseEntity.ok(resp.get());
}


   
}
