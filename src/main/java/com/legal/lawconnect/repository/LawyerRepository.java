package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {

    boolean existsByLicenseNumberOrEmailIgnoreCase(String licenseNumber, String email);

    Lawyer findByEmail(String email);

    Lawyer findByPhoneNumber(String phoneNumber);
}
