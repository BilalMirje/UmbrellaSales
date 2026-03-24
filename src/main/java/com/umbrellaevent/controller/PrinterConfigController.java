package com.umbrellaevent.controller;

import java.util.List;
import java.util.UUID;

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

import com.umbrellaevent.entity.dtos.printer.PrinterConfigRequest;
import com.umbrellaevent.entity.dtos.printer.PrinterConfigResponse;
import com.umbrellaevent.service.PrinterConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/printer")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Printer Config Management", description = "APIs for managing printer configurations")
public class PrinterConfigController {

    private final PrinterConfigService printerConfigService;

    @Operation(summary = "Create a new printer config", description = "Creates a new printer configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Printer config created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create-printer")
    public ResponseEntity<PrinterConfigResponse> createPrinter(@RequestBody PrinterConfigRequest request) {
        PrinterConfigResponse response = printerConfigService.createPrinter(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get all printer configs", description = "Retrieves all printer configurations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Printer configs retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-all-printers")
    public ResponseEntity<List<PrinterConfigResponse>> getAllPrinters() {
        List<PrinterConfigResponse> responses = printerConfigService.getAllPrinters();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get printer config by ID", description = "Retrieves a printer configuration by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Printer config found"),
            @ApiResponse(responseCode = "404", description = "Printer config not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-printer-by-id")
    public ResponseEntity<PrinterConfigResponse> getPrinterById(@RequestParam UUID id) {
        PrinterConfigResponse response = printerConfigService.getPrinterById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update printer config", description = "Updates an existing printer configuration by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Printer config updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "Printer config not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update-printer")
    public ResponseEntity<PrinterConfigResponse> updatePrinter(@RequestParam UUID id, @RequestBody PrinterConfigRequest request) {
        PrinterConfigResponse response = printerConfigService.updatePrinter(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete printer config", description = "Deletes a printer configuration by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Printer config deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Printer config not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete-printer")
    public ResponseEntity<Void> deletePrinter(@RequestParam UUID id) {
        printerConfigService.deletePrinter(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Get active printer config", description = "Retrieves a active printer configuration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Printer config found"),
            @ApiResponse(responseCode = "404", description = "Printer config not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-active-printer")
    public ResponseEntity<PrinterConfigResponse> getActivePrinter() {
        PrinterConfigResponse response = printerConfigService.getActivePrinter();
        return ResponseEntity.ok(response);
    }
}
