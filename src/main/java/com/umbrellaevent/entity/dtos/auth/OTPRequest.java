package com.umbrellaevent.entity.dtos.auth;

import lombok.Data;

@Data
public class OTPRequest {
    private String username;
    private String otp;
}
