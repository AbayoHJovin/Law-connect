package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsultationRepository extends JpaRepository<Consultation, UUID> {
    List<Consultation> findByStatus(Consultation.ConsultationStatus status);
}
