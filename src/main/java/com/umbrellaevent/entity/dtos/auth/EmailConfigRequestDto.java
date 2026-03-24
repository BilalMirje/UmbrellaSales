package com.umbrellaevent.entity.dtos.auth;

import jakarta.persistence.Column;
import lombok.Data;
@Data
public class EmailConfigRequestDto {
        @Column(nullable = false)
    private String smtpUsername;

    @Column(nullable = false)
    private String smtpPassword;

        @Column(nullable = false)
    private Boolean isActive = true;
    
}
