package com.legal.lawconnect.validation;

import com.legal.lawconnect.requests.AddLawyerRequest;
import org.springframework.stereotype.Component;

@Component
public class ValidateLawyerFields {
    public void validateLawyerRequestFields(AddLawyerRequest request) {
        if (isBlank(request.getFullName())) {
            throw new IllegalArgumentException("Full name is required.");
        }
        if (isBlank(request.getEmail())) {
            throw new IllegalArgumentException("Email is required.");
        }
        if (isBlank(request.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is required.");
        }
        if (isBlank(request.getLanguagePreference())) {
            throw new IllegalArgumentException("Language preference is required.");
        }
        if (isBlank(request.getLicenseNumber())) {
            throw new IllegalArgumentException("License number is required.");
        }
        if (request.getYearsOfExperience() <= 0) {
            throw new IllegalArgumentException("Years of experience must be greater than 0.");
        }
        if (isBlank(request.getLocation())) {
            throw new IllegalArgumentException("Location is required.");
        }
        if (isBlank(request.getPassword())) {
            throw new IllegalArgumentException("Password is required.");
        }
        if (isBlank(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Confirm password is required.");
        }
        if (request.getSpecialization() == null || request.getSpecialization().isEmpty()) {
            throw new IllegalArgumentException("At least one specialization is required.");
        }
        if (isBlank(request.getLawyerBio())) {
            throw new IllegalArgumentException("Lawyer bio is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
