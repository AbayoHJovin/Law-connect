package com.legal.lawconnect.model;

import jakarta.persistence.MappedSuperclass;
import lombok.*;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String fullName;
    private String email;
    private String password;
    private String location;
    private String phoneNumber;
    private String profilePhoto;
    private String languagePreference;

    public User(String fullName, String email, String phoneNumber, String languagePreference, String password, String location) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.languagePreference = languagePreference;
        this.password = password;
        this.location = location;
    }
}
