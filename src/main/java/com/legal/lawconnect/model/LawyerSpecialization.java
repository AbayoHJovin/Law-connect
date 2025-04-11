package com.legal.lawconnect.model;

import lombok.*;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "lawyer_specializations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LawyerSpecialization {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "lawyer_id", nullable = false)
    private LawyerProfile lawyerProfile;

    @ManyToOne
    @JoinColumn(name = "specialization_id", nullable = false)
    private Specialization specialization;
}
