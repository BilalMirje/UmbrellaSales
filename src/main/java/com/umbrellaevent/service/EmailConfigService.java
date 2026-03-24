package com.umbrellaevent.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.umbrellaevent.entity.EmailConfig;

public interface EmailConfigService {
    EmailConfig saveEmailConfig(EmailConfig emailConfig);
    Optional<EmailConfig> getActiveEmailConfig();
    List<EmailConfig> getAllEmailConfigs();
    Optional<EmailConfig> getEmailConfigById(UUID id);
    void deleteEmailConfig(UUID id);
}
