package com.legal.lawconnect.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "citizen_id", nullable = false)
    private Citizen citizen;

    @ManyToOne
    @JoinColumn(name = "lawyer_id", nullable = false)
    private Lawyer lawyer;

    private String subject;
    private String description;

    @Enumerated(EnumType.STRING)
    private ConsultationStatus status; // 'pending', 'accepted', 'completed'

    private Long scheduledTime;

    private String whatsappLink;

    private Long createdAt;
    private Long updatedAt;

    public enum ConsultationStatus {
        PENDING,
        REJECTED,
        ACCEPTED,
        COMPLETED
    }
}
