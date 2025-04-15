package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.Citizen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CitizenRepository extends JpaRepository<Citizen, UUID> {
    Citizen findByEmail(String email);

    Citizen findByPhoneNumber(String phoneNumber);

    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);

    List<Citizen> findByLocation(String location);
}
