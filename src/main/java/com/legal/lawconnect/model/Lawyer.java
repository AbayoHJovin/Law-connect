package com.legal.lawconnect.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lawyers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lawyer extends User {

    @Id
    @GeneratedValue
    private UUID id;

    private String licenseNumber;
    private String bio;
    private int yearsOfExperience;
    private boolean isAvailableForWork;
    @OneToMany(mappedBy = "lawyer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings;

    @OneToMany(mappedBy = "lawyer" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Specialization> specialization;

    @OneToMany(mappedBy = "citizen", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Consultation> consultations;

    private Long createdAt;
    private Long updatedAt;

    public Lawyer(String fullName, String password,String email, String phoneNumber, String languagePreference, String licenseNumber, int yearsOfExperience, String location, List<Specialization> specialization) {
        super(fullName,email,phoneNumber, languagePreference,password,location);
        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
        this.specialization = specialization;
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
