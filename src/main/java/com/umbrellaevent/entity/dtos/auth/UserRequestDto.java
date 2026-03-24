package com.umbrellaevent.entity.dtos.auth;

import java.time.LocalDate;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    private UUID id;
    private String name;
    private String address;
    private String username;
    private String password;
    private String contact;
    private String email;
    private LocalDate dateOfJoining;
    private Double salary;
    private UUID roleId;
}
