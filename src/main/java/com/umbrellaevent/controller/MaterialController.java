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
import com.umbrellaevent.entity.dtos.material.MaterialRequest;
import com.umbrellaevent.entity.dtos.material.MaterialResponse;
import com.umbrellaevent.service.MaterialService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/material")
@RequiredArgsConstructor
@CrossOrigin("*")
@Tag(name = "Material Management", description = "APIs for managing materials (product categories)")
public class MaterialController {

    private final MaterialService materialService;
    private final AuditorAwareImpl auditorAware;

    @Operation(summary = "Create a new material", description = "Creates a new material with the provided name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Material created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Material name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create-material")
    public ResponseEntity<?> createMaterial(@RequestBody MaterialRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        MaterialResponse response = materialService.createMaterial(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get material by ID", description = "Retrieves a material by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Material found"),
        @ApiResponse(responseCode = "404", description = "Material not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-material")
    public ResponseEntity<?> getMaterial(@RequestParam UUID id) {
        MaterialResponse response = materialService.getMaterialById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all materials", description = "Retrieves all materials with optional pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Materials retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/get-all-materials")
    public ResponseEntity<?> getAllMaterials(
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
            Page<MaterialResponse> responses = materialService.getAllMaterials(pageable);
            return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
        } else {
            // Return all materials without pagination
            List<MaterialResponse> responses = materialService.getAllMaterials();
            return responses.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(responses);
        }
    }

    @Operation(summary = "Update material", description = "Updates an existing material by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Material updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Material not found"),
        @ApiResponse(responseCode = "409", description = "Material name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update-material")
    public ResponseEntity<?> updateMaterial(@RequestParam UUID id, @RequestBody MaterialRequest request) {
        String username = auditorAware.getCurrentAuditor().get();
        MaterialResponse response = materialService.updateMaterial(id, request, username);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete material", description = "Deletes a material by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Material deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Material not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete-material")
    public ResponseEntity<?> deleteMaterial(@RequestParam UUID id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
