package com.legal.lawconnect.controller;


import com.legal.lawconnect.dto.CitizenDto;
import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.requests.*;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.citizen.ICitizenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/citizens")
public class CitizenController {
    private final ICitizenService citizenService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addCitizen(@RequestBody AddCitizenRequest citizen) {
        try{
            Citizen theCitizen = citizenService.addCitizen(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", theCitizen));
        }catch(AlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllCitizens(){
        try{
            List<Citizen> citizens = citizenService.getAllCitizens();
            List<CitizenDto> convertedCitizens = citizenService.getConvertedCitizens(citizens);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizens));
        }catch(Exception e){
            return ResponseEntity.ok(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-citizen-by-id/{citizenId}")
    public ResponseEntity<ApiResponse> getCitizenById(@PathVariable("citizenId") UUID citizenId) {
        try{
            Citizen citizen = citizenService.getCitizenById(citizenId);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-citizen-by-location")
    public ResponseEntity<ApiResponse> getCitizensByLocation(@RequestParam("location") String location) {
        try{
            List<Citizen> citizen = citizenService.getCitizensByLocation(location);
            List<CitizenDto> convertedCitizens = citizenService.getConvertedCitizens(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizens));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-citizen-by-phone")
    public ResponseEntity<ApiResponse> getCitizensByPhone(@RequestParam("phone") String phone) {
        try{
            Citizen citizen = citizenService.getCitizenByPhoneNumber(phone);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/get-citizen-by-email")
    public ResponseEntity<ApiResponse> getCitizenByEmail(@RequestParam("email") String email) {
        try{
            Citizen citizen = citizenService.getCitizenByEmail(email);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PostMapping("/rate-lawyer")
    public ResponseEntity<ApiResponse> rateLawyer(@RequestBody AddRatingRequest request){
        try {
            citizenService.rateLawyer(request);
            return ResponseEntity.ok(new ApiResponse("You've rated the lawyer successfully!", null));
        }catch(ResourceNotFoundException | AlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody ChangePasswordRequest request){
        try {
            citizenService.changePassword(request);
            return ResponseEntity.ok(new ApiResponse("Password changed Successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PutMapping("/changeLanguage/{citizenId}")
    public ResponseEntity<ApiResponse> changeLanguage(@RequestParam String newLanguage, @PathVariable UUID citizenId){
        try{
            citizenService.changeLanguagePreference(newLanguage, citizenId);
            return ResponseEntity.ok(new ApiResponse("Language preference changed Successfully", null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }

    }

    @PutMapping("/update-citizen/{citizenId}")
    public ResponseEntity<ApiResponse> updateCitizen(@RequestBody UpdateCitizenRequest request , @PathVariable UUID citizenId){
        try {
            Citizen cit = citizenService.updateCitizen(request, citizenId);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(cit);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @DeleteMapping("/deleteCitizen/{citizenId}")
    public ResponseEntity<ApiResponse> deleteCitizen(@PathVariable UUID citizenId){
        try {
            citizenService.deleteCitizen(citizenId);
            return ResponseEntity.ok(new ApiResponse("Success", null));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PostMapping("/login-by-phone")
    public ResponseEntity<ApiResponse> loginByPhone(@RequestBody PhoneLoginRequest phoneLoginRequest){
        try{
            Citizen citizen = citizenService.findCitizenByPhoneNumberAndPassword(phoneLoginRequest);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @PostMapping("/login-by-email")
    public ResponseEntity<ApiResponse> loginByEmail(@RequestBody EmailLoginRequest request){
        try{
            Citizen citizen = citizenService.findCitizenByEmailAndPassword(request);
            CitizenDto convertedCitizen = citizenService.convertCitizenToDto(citizen);
            return ResponseEntity.ok(new ApiResponse("Success", convertedCitizen));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }


}
