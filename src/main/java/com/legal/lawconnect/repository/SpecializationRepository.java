package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.Specialization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpecializationRepository extends JpaRepository<Specialization, UUID> {
    Specialization findByName(String name);
    Specialization findbyUUID(UUID uuid);
}
