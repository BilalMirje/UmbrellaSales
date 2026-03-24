package com.umbrellaevent.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table
public class AppUser extends Audit {
    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    private String name;
    private String address;

    @Column(unique = true)
    private String username;
    private String password;
    private String contact;
    private String email;
    private LocalDate dateOfJoining;
    private double salary;
    private String secretKey;
    private Boolean isMultiFactor = false;
    @Column(length = 100000)
    private String qrCodeImage;

    private String resetToken;
    private LocalDateTime resetTokenExpiry;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;
}
