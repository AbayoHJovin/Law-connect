package com.legal.lawconnect.controller;

import com.legal.lawconnect.dto.LawyerDto;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.requests.*;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.lawyer.ILawyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/lawyers")
public class LawyerController {
    private final ILawyerService lawyerService;

    @PostMapping("/add-lawyer")
    public ResponseEntity<ApiResponse> addLawyer(@RequestBody AddLawyerRequest lawyer) {
        try{
            Lawyer lawyerSaved = lawyerService.save(lawyer);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyerSaved);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyer));
        }catch(RuntimeException e){
            return ResponseEntity.status(500).body(new ApiResponse(e.getMessage(),null));
        }
    }


    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAllLawyers() {
        try {
            List<Lawyer> lawyers = lawyerService.findAll();
            List<LawyerDto> convertedLawyers = lawyerService.getConvertedLawyers(lawyers);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyers));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<ApiResponse> findLawyerById(@PathVariable UUID id) {
        try {
            Lawyer lawyer = lawyerService.findById(id);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyer));
        }catch (RuntimeException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-by-email")
    public ResponseEntity<ApiResponse> findLawyerByEmail(@RequestParam("email") String email) {
        try {
            Lawyer lawyer = lawyerService.findByEmail((email));
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyer));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/find-by-phone")
    public ResponseEntity<ApiResponse> findLawyerByPhone(@RequestParam("phone") String phone) {
        try {
            Lawyer lawyer = lawyerService.findByPhone(phone);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("success", convertedLawyer));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @PatchMapping("/update")
    public ResponseEntity<ApiResponse> updateLawyer(@RequestBody UpdateLawyerRequest request){
        try{
            Lawyer lawyer = lawyerService.updateLawyer(request);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("Lawyer Updated successfully", convertedLawyer));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteLawyerById(@PathVariable("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            lawyerService.deleteLawyer((uuid));
            return ResponseEntity.ok(new ApiResponse("Lawyer deletion successful", null));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }


    @PutMapping("/changeAvailability")
    public ResponseEntity<ApiResponse> changeAvailabilityForWork(@RequestParam boolean availability, @RequestParam UUID lawyerId) {
        try{
            lawyerService.setAvailabilityForWork(lawyerId, availability);
            return ResponseEntity.ok(new ApiResponse("Availability changed!", null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/phone-login")
    public ResponseEntity<ApiResponse> phoneLogin (@RequestBody PhoneLoginRequest phoneLoginRequest) {
        try{
            Lawyer lawyer = lawyerService.findLawyerByPhoneAndPassword(phoneLoginRequest);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("Success", convertedLawyer));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }


    @PostMapping("/email-login")
    public ResponseEntity<ApiResponse> emailLogin(@RequestBody EmailLoginRequest emailLoginRequest) {
        try{
            Lawyer lawyer = lawyerService.findLawyerByEmailAndPassword(emailLoginRequest);
            LawyerDto convertedLawyer = lawyerService.convertLawyerToDto(lawyer);
            return ResponseEntity.ok(new ApiResponse("Success", convertedLawyer));
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-rating-below/{ratingValue}")
    public ResponseEntity<ApiResponse> findRatingBelow(@PathVariable int ratingValue){
        try{
            List<Lawyer> lawyers = lawyerService.findLawyersByRatingScoresBelow(ratingValue);
            List<LawyerDto> lawyerDtos = lawyerService.getConvertedLawyers(lawyers);
            return ResponseEntity.ok(new ApiResponse("Success", lawyerDtos));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/find-rating-above/{ratingValue}")
    public ResponseEntity<ApiResponse> findRatingAbove(@PathVariable int ratingValue){
        try{
            List<Lawyer> lawyers = lawyerService.findLawyersByRatingScoresAbove(ratingValue);
            List<LawyerDto> lawyerDtos = lawyerService.getConvertedLawyers(lawyers);
            return ResponseEntity.ok(new ApiResponse("Success", lawyerDtos));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }


    @GetMapping("/find-rating-equals-to/{ratingValue}")
    public ResponseEntity<ApiResponse> findRatingEqualsTo(@PathVariable int ratingValue){
        try{
            List<Lawyer> lawyers = lawyerService.findLawyersByRatingScoresEqualsTo(ratingValue);
            List<LawyerDto> lawyerDtos = lawyerService.getConvertedLawyers(lawyers);
            return ResponseEntity.ok(new ApiResponse("Success", lawyerDtos));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PutMapping("/changeLanguage/{lawyerId}")
    public ResponseEntity<ApiResponse> changeLanguagePreference(@RequestParam String newLanguage, @PathVariable UUID lawyerId){
        try{
            lawyerService.changeLanguagePreference(newLanguage, lawyerId);
            return ResponseEntity.ok(new ApiResponse("Language preference changed Successfully", null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }

    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody ChangePasswordRequest request){
        try {
            lawyerService.changePassword(request);
            return ResponseEntity.ok(new ApiResponse("Password changed Successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }


}
