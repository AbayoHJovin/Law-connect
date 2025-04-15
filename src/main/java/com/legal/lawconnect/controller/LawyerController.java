package com.legal.lawconnect.controller;

import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.requests.AddLawyerRequest;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.lawyer.ILawyerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/lawyers")
public class LawyerController {
    private final ILawyerService lawyerService;

    @GetMapping("/getall")
    public ResponseEntity<ApiResponse> getAllLawyers() {
        try {
            List<Lawyer> lawyers = lawyerService.findAll();
            return ResponseEntity.ok(new ApiResponse("success", lawyers));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<ApiResponse> findLawyerById(@PathVariable("id") String id) {
        try {
            UUID uuid = UUID.fromString(id);
            Lawyer lawyer = lawyerService.findById((uuid));
            return ResponseEntity.ok(new ApiResponse("success", lawyer));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/find/{email}")
    public ResponseEntity<ApiResponse> findLawyerByEmail(@PathVariable("email") String email) {
        try {
            Lawyer lawyer = lawyerService.findByEmail((email));
            return ResponseEntity.ok(new ApiResponse("success", lawyer));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
        }
    }

    @GetMapping("/find/{phone}")
    public ResponseEntity<ApiResponse> findLawyerByPhone(@PathVariable("phone") String phone) {
        try {
            Lawyer lawyer = lawyerService.findByPhone(phone);
            return ResponseEntity.ok(new ApiResponse("success", lawyer));
        }catch (Exception e){
            return ResponseEntity.status(500).body(new ApiResponse("error", e.getMessage()));
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

    @PostMapping("/addLawyer")
    public ResponseEntity<ApiResponse> addLawyer(@RequestBody AddLawyerRequest lawyer) {
        try{
            Lawyer lawyerSaved = lawyerService.save(lawyer);
            return ResponseEntity.ok(new ApiResponse("success", lawyerSaved));
        }catch(AlreadyExistsException e){
            return ResponseEntity.status(401).body(new ApiResponse("error", e.getMessage()));
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

}
