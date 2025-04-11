package com.legal.lawconnect.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "lawyer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LawyerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String licenseNumber;
    private String bio; // Short description or biography of the lawyer

    private int yearsOfExperience;

    private String location; // E.g. "Kigali", "Rubavu", etc.

    private boolean isAvailableForWork;

    private double rating; // The average rating for this lawyer

    private Long createdAt;
    private Long updatedAt;
}
