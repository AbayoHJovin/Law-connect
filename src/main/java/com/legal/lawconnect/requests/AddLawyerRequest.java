package com.legal.lawconnect.requests;

import com.legal.lawconnect.model.Specialization;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AddLawyerRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String languagePreference;
    private String licenseNumber;
    private int yearsOfExperience;
    private String location;
    private String password;
    private List<SpecializationRequest> specialization;
}
