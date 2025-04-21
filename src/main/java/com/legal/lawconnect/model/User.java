package com.legal.lawconnect.model;

import com.legal.lawconnect.enums.UserRoles;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
    private String fullName;
    private String password;
    private String location;
    private String languagePreference;
    @Column(nullable = true, unique = true)
    private String email;

    @Column(nullable = true, unique = true)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoles role;


    public User(String fullName, String email, String phoneNumber, String languagePreference, String password, String location, UserRoles role) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.languagePreference = languagePreference;
        this.password = password;
        this.location = location;
        this.role = role;
    }
}
