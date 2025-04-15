package com.legal.lawconnect.controller;

import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Specialization;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.specialization.ISpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/specializations")
public class SpecializationController {
    private final ISpecializationService specializationService;

    @PostMapping("/addSpecialization")
    public ResponseEntity<ApiResponse> addSpecialization(@RequestBody Specialization specialization) {
    try{
    Specialization sp = specializationService.addSpecialization(specialization);
    return ResponseEntity.ok(new ApiResponse("Specialization added!", sp));
    }catch (AlreadyExistsException e){
        return ResponseEntity.status(401).body(new ApiResponse(e.getMessage(),null));
    }
    }

    @PutMapping("/updateSpecialization/{specializationId}")
    public ResponseEntity<ApiResponse> updateSpecialization(@RequestBody String newSpecializationName, @PathVariable UUID specializationId) {
        try {
            Specialization updatedSpecialization = specializationService.updateSpecialization(newSpecializationName, specializationId);
            return ResponseEntity.ok(new ApiResponse("Specialization updated!", updatedSpecialization));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllSpecializations() {
    try{
        List<Specialization> allSpecialization = specializationService.getSpecializations();
        return ResponseEntity.ok(new ApiResponse("All specializations", allSpecialization));
    }catch(Exception e){
        return ResponseEntity.status(500).body(new ApiResponse(e.getMessage(),null));
    }
    }
}
