package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmailRepository extends JpaRepository<EmailVerification, UUID> {
    Optional<EmailVerification> findByEmail(String email);

    void deleteByEmail(String email);
}
