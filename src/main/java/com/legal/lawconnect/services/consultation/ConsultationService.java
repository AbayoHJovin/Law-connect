package com.legal.lawconnect.services.consultation;

import com.legal.lawconnect.dto.CitizenDto;
import com.legal.lawconnect.dto.ConsultationDto;
import com.legal.lawconnect.enums.UserRoles;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.exceptions.UnauthorizedActionException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Consultation;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.repository.ConsultationRepository;
import com.legal.lawconnect.requests.ChangeConsultationStatus;
import com.legal.lawconnect.requests.CreateConsultationRequest;
import com.legal.lawconnect.requests.UpdateConsultationRequest;
import com.legal.lawconnect.services.citizen.CitizenService;
import com.legal.lawconnect.services.citizen.ICitizenService;
import com.legal.lawconnect.services.lawyer.ILawyerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConsultationService implements IConsultationService {
    private final ConsultationRepository consultationRepository;
    private final ILawyerService lawyerService;
    private final ICitizenService citizenService;
    private final ModelMapper modelMapper;

    @Override
    public Consultation.ConsultationStatus getConsultationStatus(UUID ownerId, UUID consultationId) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(()-> new ResourceNotFoundException("Consultation with id " + consultationId + " not found"));


        boolean isOwner = consultation.getLawyer().getId().equals(ownerId)
                || consultation.getCitizen().getId().equals(ownerId);

        if (!isOwner) {
            throw new UnauthorizedActionException("You are not authorized to access this consultation");
        }

        return consultation.getStatus();
    }


    @Override
    public Consultation getConsultationById(UUID consultationId,String email) {
        Consultation consultation= consultationRepository.findById(consultationId)
                .orElseThrow(()-> new ResourceNotFoundException("Consultation with id " + consultationId + " not found"));
        if(!consultation.getLawyer().getEmail().equals(email) && !consultation.getCitizen().getEmail().equals(email)) {
            throw new UnauthorizedActionException("You are not authorized to access this consultation" + email);
        }
        return consultation;
    }

    @Override
    public List<Consultation> getAllConsultations() {
        return consultationRepository.findAll();
    }

    @Override
    public List<Consultation> getConsultationsForLawyer(String lawyerEmail) {
        Lawyer owner = lawyerService.findByEmail(lawyerEmail);
        if(owner == null) {
            throw new ResourceNotFoundException("Lawyer with email " + lawyerEmail + " not found");
        }
        return owner.getConsultations();
    }

    @Override
    public List<Consultation> getConsultationsForCitizen(String citizenEmail) {
        Citizen owner = citizenService.getCitizenByEmail(citizenEmail);
        if(owner == null) {
            throw new ResourceNotFoundException("Citizen with email " + citizenEmail + " not found");
        }
        return owner.getConsultations();
    }

    @Override
    public List<Consultation> getConsultationsBetweenLawyerAndCitizen(UUID lawyerId, UUID citizenId) {
        Citizen citizenOwner = citizenService.getCitizenById(citizenId);
        Lawyer lawyerOwner = lawyerService.findById(lawyerId);

        if (lawyerOwner == null) {
            throw new ResourceNotFoundException("Lawyer with id " + lawyerId + " not found");
        }

        if (citizenOwner == null) {
            throw new ResourceNotFoundException("Citizen with id " + citizenId + " not found");
        }

        List<Consultation> consultations = lawyerOwner.getConsultations();

        return consultations.stream()
                .filter(consultation ->
                        consultation.getLawyer().getId().equals(lawyerId) &&
                                consultation.getCitizen().getId().equals(citizenId))
                .toList();
    }

    @Override
    public List<Consultation> getConsultationsByStatus(Consultation.ConsultationStatus status) {
        return consultationRepository.findByStatus(status);
    }

    @Override
    public List<Consultation> getConsultationsByStatusForLawyer(Consultation.ConsultationStatus status, UUID lawyerId) {
        Lawyer owner = lawyerService.findById(lawyerId);
        if(owner == null) {
            throw new ResourceNotFoundException("Lawyer with id " + lawyerId + " not found");
        }
        List<Consultation> consultations = owner.getConsultations();
        return consultations.stream()
                .filter(cons-> cons.getStatus().equals(status))
                .toList();
    }

    @Override
    @Transactional
    public Consultation createConsultation(CreateConsultationRequest consultation,String citizenEmail) {
        Lawyer ownerLawyer = lawyerService.findById(consultation.getLawyerId());
        Citizen ownerCitizen = citizenService.getCitizenByEmail(citizenEmail);
        if (ownerLawyer == null || ownerCitizen == null) {
            throw new ResourceNotFoundException("Missing lawyer or citizen");
        }
        if(!ownerLawyer.isAvailableForWork()){
            throw new UnauthorizedActionException("Lawyer Not available for work!");
        }
        try {
            return consultationRepository.save(createNewConsultation(consultation, ownerLawyer, ownerCitizen));
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Input value too long, please shorten the subject or description.");
        }
    }
    private Consultation createNewConsultation(CreateConsultationRequest consultation, Lawyer ownerLawyer, Citizen ownerCitizen) {
        Consultation consultation1 = new Consultation();
        consultation1.setCitizen(ownerCitizen);
        consultation1.setLawyer(ownerLawyer);
        consultation1.setSubject(consultation.getSubject());
        if(!consultation.getDescription().isEmpty()){
            consultation1.setDescription(consultation.getDescription());
        }
        consultation1.setStatus(Consultation.ConsultationStatus.PENDING);
        consultation1.setCreatedAt(System.currentTimeMillis());
        return consultation1;
    }
    @Override
    @Transactional
    public Consultation updateConsultation(UUID consultationId, UpdateConsultationRequest updatedConsultation) {
    return consultationRepository.findById(consultationId)
            .map(existingConsultation-> updateExistingConsultation(existingConsultation, updatedConsultation))
            .map(consultationRepository::save)
            .orElseThrow(() -> new ResourceNotFoundException("Consultation not found"));
    }
    private Consultation updateExistingConsultation(Consultation existingConsultation , UpdateConsultationRequest request){
       existingConsultation.setSubject(request.getSubject());
       existingConsultation.setDescription(request.getDescription());
       existingConsultation.setStatus(request.getStatus());
       return existingConsultation;
    }
    @Override
    @Transactional
    public void changeStatus(ChangeConsultationStatus request, String lawyerEmail) {
        Consultation target = getConsultationById(request.getConsultationId(),lawyerEmail);
        if(target == null) {
            throw new ResourceNotFoundException("Consultation with id " + request.getConsultationId() + " not found");
        }
        if(!target.getLawyer().getEmail().equals(lawyerEmail) && !target.getLawyer().getRole().equals(UserRoles.LAWYER)) {
            throw new UnauthorizedActionException("You are not allowed to update the status");
        }
        target.setStatus(request.getStatus());
        consultationRepository.save(target);
    }

    @Override
    @Transactional
    public void deleteConsultation(UUID consultationId,String email) {
        Consultation target = getConsultationById(consultationId,email);
        if(target == null) {
            throw new ResourceNotFoundException("Consultation with id " + consultationId + " not found");
        }
        consultationRepository.delete(target);
    }

    @Override
    @Transactional
    public ConsultationDto convertToDto(Consultation consultation) {
        ConsultationDto dto = modelMapper.map(consultation, ConsultationDto.class);
        dto.setCitizenId(consultation.getCitizen().getId());
        dto.setLawyerID(consultation.getLawyer().getId());
        return dto;
    }

    @Override
    @Transactional
    public List<ConsultationDto> getConvertedConsultations(List<Consultation> consultations) {
        return consultations.stream().map(this:: convertToDto).toList();
    }
}
