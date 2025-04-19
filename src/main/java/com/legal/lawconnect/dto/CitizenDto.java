package com.legal.lawconnect.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CitizenDto {
        private UUID id;
        private String fullName;
        private String email;
        private String phoneNumber;
        private String languagePreference;
        private String location;
}
