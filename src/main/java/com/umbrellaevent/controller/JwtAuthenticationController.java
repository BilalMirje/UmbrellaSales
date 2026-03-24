package com.umbrellaevent.controller;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.umbrellaevent.config.jwt.JwtUtils;
import com.umbrellaevent.entity.AppUser;
import com.umbrellaevent.entity.Permissions;
import com.umbrellaevent.entity.Privilege;
import com.umbrellaevent.entity.Role;
import com.umbrellaevent.entity.dtos.auth.JwtRequest;
import com.umbrellaevent.entity.dtos.auth.JwtResponse;
import com.umbrellaevent.entity.dtos.auth.PermissionDto;
import com.umbrellaevent.entity.dtos.auth.RoleDto;
import com.umbrellaevent.repository.AppUserRepository;
import com.umbrellaevent.repository.PermissionRepository;
import com.umbrellaevent.repository.RoleRepository;
import com.umbrellaevent.service.RoleService;
import com.umbrellaevent.service.jwt.MyUserDetailsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/service")
@CrossOrigin("*")
@Tag(name = "Authentication", description = "APIs for user authentication and token management")
@SuppressWarnings("null")
public class JwtAuthenticationController {

    private final BCryptPasswordEncoder passwordEncoder;
    private final AppUserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final MyUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;


    @PostConstruct
    public void createAdmin() throws IOException {
        Optional<AppUser> optionalUser = userRepository.getMyUserByUsername("superadmin.com");
        if (optionalUser.isEmpty()) {

            Role savedRole = roleRepository.findByRoleName("SUPER_ADMIN")
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setRoleName("SUPER_ADMIN");
                        role.setRoleDescription("This is super admin role");
                        return roleRepository.save(role);
                    });

            if (permissionRepository.getPermissionsByRole(savedRole).isEmpty()) {
                Privilege privilege = new Privilege();
                privilege.setWritePermission("WRITE");
                privilege.setReadPermission("READ");
                privilege.setDeletePermission("DELETE");
                privilege.setUpdatePermission("UPDATE");

                Permissions permissions = new Permissions();
                permissions.setUserPermission("ALL_PERMISSIONS");
                permissions.setRole(savedRole);
                permissions.setPrivilege(privilege);
                roleService.createPermissions(List.of(permissions));
            }

            AppUser user = new AppUser();
            user.setName("harsh");
            user.setUsername("superadmin.com");
            user.setContact("1234567890");
            user.setAddress("-");
            user.setEmail("-");
            user.setDateOfJoining(null);
            user.setSalary(0);
            // user.setIsDeleted(false);
            user.setRole(savedRole);
            user.setPassword(passwordEncoder.encode("superadmin@123"));
            userRepository.save(user);
        }
    }

    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token along with user details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody JwtRequest jwtRequest){
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Incorrect Username or Password.");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());
        Optional<AppUser> optionalUser = userRepository.getMyUserByUsername(userDetails.getUsername());
        JwtResponse response = new JwtResponse();

        if (optionalUser.isPresent()) {
            AppUser user = optionalUser.get();
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            response.setUserId(user.getId());
            response.setUsername(user.getUsername());
            response.setIsLoggedIn(true);

            // Always return JWT token regardless of 2FA status
            String token = jwtUtils.generateToken(userDetails.getUsername());
            response.setJwtToken(token);

            // Set requires2FA based on whether user has 2FA enabled
            response.setRequires2FA(user.getIsMultiFactor() != null && user.getIsMultiFactor());

            Role role = user.getRole();
            RoleDto roleDto = new RoleDto();
            roleDto.setRoleId(role.getId());
            roleDto.setRoleName(role.getRoleName());
            roleDto.setRoleDescription(role.getRoleDescription());

            List<PermissionDto> permissionDtos = new ArrayList<>();
            for (Permissions permission : role.getPermissions()) {
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
        return null;
    }


    @Operation(summary = "Check token expiration", description = "Checks if the provided JWT token is expired")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token status checked successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/is-token-expired")
    public ResponseEntity<?> isTokenExpired(@RequestBody JwtResponse jwtResponse){
        try {
            return ResponseEntity.status(HttpStatus.OK).body(jwtUtils.isTokenExpired(jwtResponse.getJwtToken()));
        }catch (Exception e){
            Optional<AppUser> user = userRepository.getMyUserByUsername(jwtResponse.getUsername());
            if(user.isPresent()){
                user.get().setUpdatedAt(LocalDateTime.now());
//                user.get().setIsUserLoggedIn(false);
                AppUser save = userRepository.save(user.get());
                return ResponseEntity.ok(save);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }



    @Operation(summary = "User logout", description = "Logs out the user by updating their last activity timestamp")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout successful"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestParam String username) {
        Optional<AppUser> optionalUser = userRepository.getMyUserByUsername(username);
        if (optionalUser.isPresent()) {
            optionalUser.get().setUpdatedAt(LocalDateTime.now());
//            optionalUser.get().setIsUserLoggedIn(false);
            AppUser save = userRepository.save(optionalUser.get());
            return ResponseEntity.ok(save);
        }
        return ResponseEntity.ok(false);
    }
}

