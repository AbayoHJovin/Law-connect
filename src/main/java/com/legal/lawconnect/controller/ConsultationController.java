package com.legal.lawconnect.controller;

import com.legal.lawconnect.dto.ConsultationDto;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Consultation;
import com.legal.lawconnect.requests.CreateConsultationRequest;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.consultation.IConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/consultations/lawy-cit/")
public class ConsultationController {
    private final IConsultationService consultationService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addConsultation(@RequestBody CreateConsultationRequest request) {
        try{
            Consultation consultation = consultationService.createConsultation(request);
            ConsultationDto consultationDto = consultationService.convertToDto(consultation);
            return ResponseEntity.ok(new ApiResponse("success", consultationDto));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }catch(RuntimeException e){
            return ResponseEntity.status(500).body(new ApiResponse(e.getMessage(),null));
        }
    }
    @GetMapping("/get-status")
    public ResponseEntity<ApiResponse> getConsultationStatus(@RequestParam UUID ownerId, @RequestParam UUID consultationId) {
        try {
            Consultation.ConsultationStatus status = consultationService.getConsultationStatus(ownerId, consultationId);
            return ResponseEntity.ok(new ApiResponse("Success", status));
        }catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-by-id/{consultationId}")
    public ResponseEntity<ApiResponse> getConsultationById(@PathVariable UUID consultationId) {
        try {
            Consultation consultation = consultationService.getConsultationById(consultationId);
            ConsultationDto convertedConsultation = consultationService.convertToDto(consultation);
            return ResponseEntity.ok(new ApiResponse("Success", convertedConsultation));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-by-lawyer/{lawyerId}")
    public ResponseEntity<ApiResponse> getConsultationsForLawyer(@PathVariable UUID lawyerId) {
        try{
            List<Consultation> consultations = consultationService.getConsultationsForLawyer(lawyerId);
            List<ConsultationDto> convertedConsultations = consultationService.getConvertedConsultations(consultations);
            return ResponseEntity.ok(new ApiResponse("Success", convertedConsultations));
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-by-citizen/{citizenId}")
    public ResponseEntity<ApiResponse> getConsultationsForCitizen(@PathVariable UUID citizenId) {
        try{
            List<Consultation> consultations = consultationService.getConsultationsForCitizen(citizenId);
            List<ConsultationDto> convertedConsultations = consultationService.getConvertedConsultations(consultations);
            return ResponseEntity.ok(new ApiResponse("Success", convertedConsultations));
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-by-lawyer-and-citizen")
    public ResponseEntity<ApiResponse> getConsultationsBetweenLawyerAndCitizen(@RequestParam UUID lawyerId, @RequestParam UUID citizenId) {
        try{
            List<Consultation> consultations = consultationService.getConsultationsBetweenLawyerAndCitizen(lawyerId,citizenId);
            List<ConsultationDto> convertedConsultations = consultationService.getConvertedConsultations(consultations);
            return ResponseEntity.ok(new ApiResponse("Success", convertedConsultations));
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }
}
