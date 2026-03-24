package com.umbrellaevent.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umbrellaevent.entity.AppUser;
import com.umbrellaevent.entity.dtos.auth.ForgetPasswordRequest;
import com.umbrellaevent.entity.dtos.auth.newPasswordRequest;
import com.umbrellaevent.entity.dtos.auth.validateTokenRequest;
import com.umbrellaevent.repository.AppUserRepository;
import com.umbrellaevent.service.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/password")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "Endpoints for password reset functionality")
public class PasswordResetController {

    private final AppUserRepository userRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    @Operation(summary = "Forgot Password", description = "Initiates password reset by sending a reset link to the user's email")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset link sent successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgetPasswordRequest request) {
        String email = request.getEmail();
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        Optional<AppUser> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Don't reveal if email exists or not for security
            return ResponseEntity.status(HttpStatus.CONFLICT).body("no valid email found");
        }

        AppUser user = userOpt.get();

        // Generate reset token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusMinutes(15); // 15 minutes expiry

        user.setResetToken(resetToken);
        user.setResetTokenExpiry(expiry);
        userRepository.save(user);

        // Send email
        try {
            mailService.sendPasswordResetEmail(user.getEmail(), resetToken);
            return ResponseEntity.ok("password reset link has been sent.");
        } catch (Exception e) {
            // Clean up token if email fails
            user.setResetToken(null);
            user.setResetTokenExpiry(null);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send password reset email. Please check your email configuration and try again.");
        }
    }

    @Operation(summary = "Reset Password", description = "Resets the user's password using a valid reset token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request or invalid token")
    })
    @PostMapping(value = "/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resetPassword(@RequestBody newPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Reset token is required");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("New password is required");
        }

        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Confirm password is required");
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("New password and confirm password do not match");
        }

        // Basic password strength validation (optional, adjust as needed)
        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters long");
        }

        Optional<AppUser> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or expired reset token");
        }

        AppUser user = userOpt.get();

        // Check if token is expired
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Reset token has expired");
        }

        // Update password and clear reset token
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);

        return ResponseEntity.ok("Password has been reset successfully");
    }

    @Operation(summary = "Validate Reset Token", description = "Validates if a reset token is valid and not expired")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token is valid"),
        @ApiResponse(responseCode = "400", description = "Invalid or expired token")
    })
    @PostMapping(value = "/validate-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validateResetToken(@RequestBody validateTokenRequest request) {
        String token = request.getToken();

        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Reset token is required");
        }

        Optional<AppUser> userOpt = userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid reset token");
        }

        AppUser user = userOpt.get();

        // Check if token is expired
        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Reset token has expired");
        }

        return ResponseEntity.ok("Token is valid");
    }
}
