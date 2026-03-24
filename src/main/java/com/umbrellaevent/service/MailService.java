package com.umbrellaevent.service;

public interface MailService {
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
