package com.umbrellaevent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordResetPageController {

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(value = "token", required = false) String token) {
        // The token validation will be handled by the frontend JavaScript
        // This controller just serves the HTML template
        return "reset-password";
    }
}
