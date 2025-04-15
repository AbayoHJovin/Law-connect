package com.legal.lawconnect.services.lawyer;

import com.legal.lawconnect.model.Consultation;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.requests.AddLawyerRequest;

import java.util.List;
import java.util.UUID;

public interface ILawyerService {
    Lawyer save(AddLawyerRequest lawyer);
    List<Lawyer> findAll();
    Lawyer findById(UUID id);
    Lawyer findByEmail(String email);
    Lawyer findByPhone(String phone);
    Lawyer updateLawyer(Lawyer lawyer, UUID id);
    void deleteLawyer(UUID id);
    void setAvailabilityForWork(UUID lawyerId, boolean availability);
//    Consultation.ConsultationStatus getConsultationStatus(UUID lawyerId, UUID consultationId);
//    List<Consultation> getConsultationsForLawyer(UUID lawyerId);
//    List<Consultation> getConsultationsBetweenLawyerAndCitizen(UUID lawyerId, UUID citizenId);
}
