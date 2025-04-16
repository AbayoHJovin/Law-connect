package com.legal.lawconnect.model;

import jakarta.persistence.Column;
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
    private String password;
    private String location;
    private String profilePhoto;
    private String languagePreference;
    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    public User(String fullName, String email, String phoneNumber, String languagePreference, String password, String location) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.languagePreference = languagePreference;
        this.password = password;
        this.location = location;
    }
}
