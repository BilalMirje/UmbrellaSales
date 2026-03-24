package com.umbrellaevent.repository;

import com.umbrellaevent.entity.EmailConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailConfigRepository extends JpaRepository<EmailConfig, UUID> {
    Optional<EmailConfig> findByIsActiveTrue();
}
