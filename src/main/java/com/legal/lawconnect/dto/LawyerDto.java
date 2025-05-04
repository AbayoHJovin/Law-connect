package com.legal.lawconnect.dto;

import com.legal.lawconnect.model.Specialization;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class LawyerDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String languagePreference;
    private String licenseNumber;
    private int yearsOfExperience;
    private String location;
    private boolean isAvailableForWork;
    private List<SpecializationDto> specializations;
    double averageRating;
    private String lawyerBio;
}
