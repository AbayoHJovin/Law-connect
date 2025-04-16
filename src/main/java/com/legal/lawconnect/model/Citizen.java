package com.legal.lawconnect.model;

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

    @Id
    @GeneratedValue
    private UUID id;

    @OneToMany(mappedBy = "citizen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consultation> consultations;

    @OneToMany(mappedBy = "citizen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;


    public Citizen(String fullName, String email, String phoneNumber, String languagePreference, String password, String location) {
        super(fullName, email, phoneNumber, languagePreference,password,location);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public void setId(UUID id) {
        this.id = id;
    }
}
