package com.legal.lawconnect.services.consultation;

import com.legal.lawconnect.dto.ConsultationDto;
import com.legal.lawconnect.model.Consultation;
import com.legal.lawconnect.requests.CreateConsultationRequest;
import com.legal.lawconnect.requests.UpdateConsultationRequest;

import java.util.List;
import java.util.UUID;

public interface IConsultationService {

    // Status and Retrieval
    Consultation.ConsultationStatus getConsultationStatus(UUID lawyerId, UUID consultationId);
    Consultation getConsultationById(UUID consultationId,String email);
    List<Consultation> getAllConsultations();
    List<Consultation> getConsultationsForLawyer(UUID lawyerId);
    List<Consultation> getConsultationsForCitizen(String citizenEmail);
    List<Consultation> getConsultationsBetweenLawyerAndCitizen(UUID lawyerId, UUID citizenId);
    List<Consultation> getConsultationsByStatus(Consultation.ConsultationStatus status);
    List<Consultation> getConsultationsByStatusForLawyer(Consultation.ConsultationStatus status, UUID lawyerId);

    // Creation and Update
    Consultation createConsultation(CreateConsultationRequest consultation,String email);
    Consultation updateConsultation(UUID consultationId, UpdateConsultationRequest updatedConsultation);
    Consultation changeStatus(UUID lawyerId,UUID consultationId, Consultation.ConsultationStatus newStatus,String email);

    // Deletion
    void deleteConsultation(UUID consultationId, UUID lawyerId,String email);

    ConsultationDto convertToDto(Consultation consultation);
    List<ConsultationDto> getConvertedConsultations(List<Consultation> consultations);
}
