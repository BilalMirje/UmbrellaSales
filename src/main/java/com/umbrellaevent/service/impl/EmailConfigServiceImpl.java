package com.umbrellaevent.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.umbrellaevent.entity.EmailConfig;
import com.umbrellaevent.repository.EmailConfigRepository;
import com.umbrellaevent.service.EmailConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailConfigServiceImpl implements EmailConfigService {

    private final EmailConfigRepository emailConfigRepository;

    @Override
    public EmailConfig saveEmailConfig(EmailConfig emailConfig) {
        // If setting this as active, deactivate others
        if (emailConfig.getIsActive() != null && emailConfig.getIsActive()) {
            List<EmailConfig> activeConfigs = emailConfigRepository.findAll().stream()
                    .filter(EmailConfig::getIsActive)
                    .toList();
            activeConfigs.forEach(config -> {
                config.setIsActive(false);
                emailConfigRepository.save(config);
            });
        }
        return emailConfigRepository.save(emailConfig);
    }

    @Override
    public Optional<EmailConfig> getActiveEmailConfig() {
        return emailConfigRepository.findByIsActiveTrue();
    }

    @Override
    public List<EmailConfig> getAllEmailConfigs() {
        return emailConfigRepository.findAll();
    }

    @SuppressWarnings("null")
    @Override
    public Optional<EmailConfig> getEmailConfigById(UUID id) {
        return emailConfigRepository.findById(id);
    }

    @SuppressWarnings("null")
    @Override
    public void deleteEmailConfig(UUID id) {
        emailConfigRepository.deleteById(id);
    }
}
