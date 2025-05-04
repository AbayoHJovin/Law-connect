package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, UUID> {
    PasswordResetToken findByToken(String token);
    PasswordResetToken findByEmail(String email);
}
