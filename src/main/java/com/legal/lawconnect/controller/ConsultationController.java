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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null||!authentication.isAuthenticated() ){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            String email = authentication.getName();
            Consultation consultation = consultationService.createConsultation(request,email);
            ConsultationDto consultationDto = consultationService.convertToDto(consultation);
            return ResponseEntity.ok(new ApiResponse("success", consultationDto));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }catch(RuntimeException e){
            return ResponseEntity.status(500).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @DeleteMapping("/delete/{consultationId}")
    public ResponseEntity<ApiResponse> deleteConsultation(@PathVariable UUID consultationId) {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null||!authentication.isAuthenticated() ){
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            String email = authentication.getName();
            consultationService.deleteConsultation(consultationId,email);
            return ResponseEntity.ok(new ApiResponse("Consultation Deleted Successfully", null));
        }catch (RuntimeException e){
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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication == null||!authentication.isAuthenticated() ){
                throw new ResourceNotFoundException("You are not logged in");
            }
            String email = authentication.getName();
            Consultation consultation = consultationService.getConsultationById(consultationId,email);
            ConsultationDto convertedConsultation = consultationService.convertToDto(consultation);
            return ResponseEntity.ok(new ApiResponse("Success", convertedConsultation));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-by-lawyer")
    public ResponseEntity<ApiResponse> getConsultationsForLawyer() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            String email = authentication.getName();

            List<Consultation> consultations = consultationService.getConsultationsForLawyer(email);
            List<ConsultationDto> convertedConsultations = consultationService.getConvertedConsultations(consultations);
            return ResponseEntity.ok(new ApiResponse("Success", convertedConsultations));
        }catch(RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-by-citizen")
    public ResponseEntity<ApiResponse> getConsultationsForCitizen() {
        try{
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse("You are not logged in", null));
            }
            String email = authentication.getName();
            List<Consultation> consultations = consultationService.getConsultationsForCitizen(email);
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
