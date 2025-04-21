package com.legal.lawconnect.model;

import com.legal.lawconnect.enums.UserRoles;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "citizens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Citizen extends User {
    @OneToMany(mappedBy = "citizen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consultation> consultations;

    @OneToMany(mappedBy = "citizen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;


    public Citizen(String fullName, String email, String phoneNumber, String languagePreference, String password, String location, UserRoles role) {
        super(fullName, email, phoneNumber, languagePreference,password,location,role);
    }

}
