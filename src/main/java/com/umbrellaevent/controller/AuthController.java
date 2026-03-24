package com.umbrellaevent.controller;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umbrellaevent.config.jwt.JwtUtils;
import com.umbrellaevent.entity.AppUser;
import com.umbrellaevent.entity.Permissions;
import com.umbrellaevent.entity.dtos.auth.JwtResponse;
import com.umbrellaevent.entity.dtos.auth.MfaRequest;
import com.umbrellaevent.entity.dtos.auth.OTPRequest;
import com.umbrellaevent.entity.dtos.auth.PermissionDto;
import com.umbrellaevent.entity.dtos.auth.RoleDto;
import com.umbrellaevent.repository.AppUserRepository;
import com.umbrellaevent.service.AppUserService;
import com.umbrellaevent.service.jwt.MyUserDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/2fa")
@CrossOrigin("*")
@RequiredArgsConstructor
@Tag(name = "2FA Authentication", description = "Endpoints for managing two-factor authentication")
public class AuthController {

    private final AppUserService userService;
    private final AppUserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final MyUserDetailsService userDetailsService;


    @Operation(summary = "Enable 2FA", description = "Enables two-factor authentication for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "2FA enabled successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/enable")
    public ResponseEntity<?> enable2FA(@RequestBody MfaRequest request) throws Exception {
        return ResponseEntity.ok(userService.enableTwoFactorAuthentication(request.getUsername()));
    }

    @Operation(summary = "Disable 2FA", description = "Disables two-factor authentication for a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "2FA disabled successfully"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/disable")
    public ResponseEntity<?> disable2FA(@RequestBody MfaRequest request) {
        return ResponseEntity.ok(userService.disableTwoFactorAuthentication(request.getUsername()));
    }

    @Operation(summary = "Get QR Code", description = "Retrieves the QR code for 2FA setup")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "QR code retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "2FA not enabled or QR code not available")
    })
    @PostMapping("/get-qr")
    public ResponseEntity<?> getQRCode(@RequestBody MfaRequest request) {
        Optional<AppUser> user = userRepository.getMyUserByUsername(request.getUsername());
        if (user.isPresent() && user.get().getIsMultiFactor() && user.get().getQrCodeImage() != null) {
            return ResponseEntity.ok(user.get().getQrCodeImage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("2FA not enabled or QR code not available");
    }

    @Operation(summary = "Verify OTP", description = "Verifies the one-time password for 2FA")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid OTP")
    })
    @PostMapping("/verify")
    public ResponseEntity<?> verify2FA(@RequestBody OTPRequest otpRequest) {
        return ResponseEntity.ok(userService.verify2FA(otpRequest));
    }

    @Operation(summary = "Login with OTP", description = "Logs in a user using OTP and returns JWT token")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid OTP or user not found")
    })
    @PostMapping("/login-with-otp")
    public ResponseEntity<?> loginWithOTP(@RequestBody OTPRequest otpRequest) {
        // First verify the OTP
        Boolean isValidOTP = userService.verify2FA(otpRequest);
        if (!isValidOTP) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        // OTP is valid, now issue JWT
        Optional<AppUser> optionalUser = userRepository.getMyUserByUsername(otpRequest.getUsername());
        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            String token = jwtUtils.generateToken(userDetails.getUsername());

            // Update last login
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            // Build full JWT response
            JwtResponse response = new JwtResponse();
            response.setIsLoggedIn(true);
            response.setJwtToken(token);
            response.setRequires2FA(true); // User has 2FA enabled and verified
            response.setUserId(user.getId());
            response.setUsername(user.getUsername());

            // Set role details
            RoleDto roleDto = new RoleDto();
            roleDto.setRoleId(user.getRole().getId());
            roleDto.setRoleName(user.getRole().getRoleName());
            roleDto.setRoleDescription(user.getRole().getRoleDescription());

            List<PermissionDto> permissionDtos = new ArrayList<>();
            for (Permissions permission : user.getRole().getPermissions()) {
                PermissionDto permissionDto = new PermissionDto();
                List<String> privileges = new ArrayList<>();
                permissionDto.setUserPermission(permission.getUserPermission());
                privileges.add(permission.getPrivilege().getReadPermission());
                privileges.add(permission.getPrivilege().getWritePermission());
                privileges.add(permission.getPrivilege().getUpdatePermission());
                privileges.add(permission.getPrivilege().getDeletePermission());
                permissionDto.setPrivileges(privileges);
                permissionDtos.add(permissionDto);
            }
            roleDto.setPermissions(permissionDtos);
            response.setRole(roleDto);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
    }
}
