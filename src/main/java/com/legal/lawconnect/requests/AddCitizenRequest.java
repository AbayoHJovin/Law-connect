package com.legal.lawconnect.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class AddCitizenRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String languagePreference;
    private String location;
    private String password;
    private String confirmPassword;
}
