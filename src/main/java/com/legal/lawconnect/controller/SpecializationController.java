package com.legal.lawconnect.controller;

import com.legal.lawconnect.dto.SpecializationDto;
import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Specialization;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.specialization.ISpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/specializations")
public class SpecializationController {
    private final ISpecializationService specializationService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllSpecializations() {
        try{
            List<Specialization> allSpecialization = specializationService.getSpecializations();
            List<SpecializationDto> convertedSpecializations = specializationService.getConvertedSpecializations(allSpecialization);
            return ResponseEntity.ok(new ApiResponse("All specializations", convertedSpecializations));
        }catch(RuntimeException e){
            return ResponseEntity.status(500).body(new ApiResponse(e.getMessage(),null));
        }
    }
    @PostMapping("/addSpecialization")
    public ResponseEntity<ApiResponse> addSpecialization(@RequestBody Map<String, String> body) {
    try{
        String specializationName = body.get("specializationName");
        Specialization sp = specializationService.addSpecialization(specializationName);
    SpecializationDto convertedSp = specializationService.convertSpecializationToDto(sp);
    return ResponseEntity.ok(new ApiResponse("Specialization added!", convertedSp));
    }catch (AlreadyExistsException e){
        return ResponseEntity.status(401).body(new ApiResponse(e.getMessage(),null));
    }
    }

    @PutMapping("/lawy-adm/updateSpecialization/{specializationId}")
    public ResponseEntity<ApiResponse> updateSpecialization(@RequestBody String newSpecializationName, @PathVariable UUID specializationId) {
        try {
            Specialization updatedSpecialization = specializationService.updateSpecialization(newSpecializationName, specializationId);
            SpecializationDto convertedSp = specializationService.convertSpecializationToDto(updatedSpecialization);
            return ResponseEntity.ok(new ApiResponse("Specialization updated!", convertedSp));
        }catch(ResourceNotFoundException e){
            return ResponseEntity.status(404).body(new ApiResponse(e.getMessage(),null));
        }
    }

}
