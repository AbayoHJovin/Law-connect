package com.legal.lawconnect.requests;

import com.legal.lawconnect.model.Specialization;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UpdateLawyerRequest {
    private UUID lawyerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String languagePreference;
    private String licenseNumber;
    private Integer yearsOfExperience;
    private String location;
    private List<SpecializationRequest> specialization;
}
