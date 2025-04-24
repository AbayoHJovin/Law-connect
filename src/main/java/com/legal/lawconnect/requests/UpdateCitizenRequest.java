package com.legal.lawconnect.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCitizenRequest {
        private String fullName;
        private String phoneNumber;
        private String languagePreference;
        private String location;
    }


