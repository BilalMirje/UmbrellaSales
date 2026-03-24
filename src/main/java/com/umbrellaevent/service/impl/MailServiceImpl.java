package com.umbrellaevent.service.impl;

import java.util.Optional;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.umbrellaevent.entity.EmailConfig;
import com.umbrellaevent.service.EmailConfigService;
import com.umbrellaevent.service.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final EmailConfigService emailConfigService;

    @SuppressWarnings("null")
    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        Optional<EmailConfig> configOpt = emailConfigService.getActiveEmailConfig();
        if (configOpt.isEmpty()) {
            throw new RuntimeException("No active email configuration found");
        }

        EmailConfig config = configOpt.get();
        JavaMailSender mailSender = createMailSender(config);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(config.getSmtpUsername());
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");

            String resetLink = "http://192.168.1.36:8080/reset-password?token=" + resetToken; // Update this URL to match your application URL
            String htmlContent = "<html><body><h2>Password Reset Request</h2><p>You have requested to reset your password. Click the link below to reset your password:</p><a href=\"" + resetLink + "\" style=\"background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">Reset Password</a><p>This link will expire in 15 minutes.</p><p>If you didn't request this, please ignore this email.</p></body></html>";

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private JavaMailSender createMailSender(EmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(config.getSmtpUsername());
        mailSender.setPassword(config.getSmtpPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.debug", "false");

        return mailSender;
    }
}
