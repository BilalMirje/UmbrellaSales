package com.umbrellaevent.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.MediaType;
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

import com.umbrellaevent.entity.EmailConfig;
import com.umbrellaevent.entity.dtos.auth.EmailConfigRequestDto;
import com.umbrellaevent.service.EmailConfigService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/email-config")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Email Configuration", description = "Endpoints for managing email configurations")
public class EmailConfigController {

    private final EmailConfigService emailConfigService;

    @Operation(summary = "Create Email Config", description = "Creates a new email configuration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email config created successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/create-email")
    public ResponseEntity<?> createEmailConfig(@RequestBody EmailConfigRequestDto emailConfig) {
        EmailConfig newConfig = new EmailConfig();
        newConfig.setSmtpUsername(emailConfig.getSmtpUsername());
        newConfig.setSmtpPassword(emailConfig.getSmtpPassword());
        newConfig.setIsActive(emailConfig.getIsActive());
        EmailConfig savedConfig = emailConfigService.saveEmailConfig(newConfig);
        return ResponseEntity.ok(savedConfig);
    }

    @Operation(summary = "Get Active Email Config", description = "Retrieves the active email configuration")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active email config retrieved"),
        @ApiResponse(responseCode = "404", description = "No active email config found")
    })
    @GetMapping("/active")
    public ResponseEntity<?> getActiveEmailConfig() {
        Optional<EmailConfig> config = emailConfigService.getActiveEmailConfig();
        return config.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get All Email Configs", description = "Retrieves all email configurations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email configs retrieved successfully")
    })
    @GetMapping("/get-all-emails")
    public ResponseEntity<?> getAllEmailConfigs() {
        List<EmailConfig> configs = emailConfigService.getAllEmailConfigs();
        return ResponseEntity.ok(configs);
    }

    @Operation(summary = "Get Email Config by ID", description = "Retrieves an email configuration by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email config retrieved"),
        @ApiResponse(responseCode = "404", description = "Email config not found")
    })
    @GetMapping("/get-email-by-id")
    public ResponseEntity<?> getEmailConfigById(@RequestParam UUID id) {
        Optional<EmailConfig> config = emailConfigService.getEmailConfigById(id);
        return config.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update Email Config", description = "Updates an existing email configuration by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email config updated successfully"),
        @ApiResponse(responseCode = "404", description = "Email config not found")
    })
    @PutMapping(value = "/update-email-by-id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateEmailConfig(@RequestParam UUID id, @RequestBody EmailConfigRequestDto emailConfig) {
        Optional<EmailConfig> existingConfig = emailConfigService.getEmailConfigById(id);
        if (existingConfig.isPresent()) {
            EmailConfig configToUpdate = new EmailConfig();
            configToUpdate.setId(id);
            configToUpdate.setSmtpUsername(emailConfig.getSmtpUsername());
            configToUpdate.setSmtpPassword(emailConfig.getSmtpPassword());
            configToUpdate.setIsActive(emailConfig.getIsActive());
            EmailConfig updatedConfig = emailConfigService.saveEmailConfig(configToUpdate);
            return ResponseEntity.ok(updatedConfig);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Delete Email Config", description = "Deletes an email configuration by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Email config deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Email config not found")
    })
    @DeleteMapping("/delete-email-by-id")
    public ResponseEntity<?> deleteEmailConfig(@RequestParam UUID id) {
        Optional<EmailConfig> config = emailConfigService.getEmailConfigById(id);
        if (config.isPresent()) {
            emailConfigService.deleteEmailConfig(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
