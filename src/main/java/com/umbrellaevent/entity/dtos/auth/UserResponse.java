package com.umbrellaevent.entity.dtos.auth;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.umbrellaevent.entity.Role;

import lombok.Data;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String address;
    private String username;
    private String password;
    private String contact;
    private String email;
    private LocalDate dateOfJoining;
    private double salary;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Role role;
}
