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
    private User citizen;

    @ManyToOne
    @JoinColumn(name = "lawyer_id", nullable = false)
    private User lawyer;

    private String subject; // Short description of the issue
    private String description; // Detailed description of the case

    @Enumerated(EnumType.STRING)
    private ConsultationStatus status; // 'pending', 'accepted', 'completed'

    private Long scheduledTime; // Time for the consultation

    private String whatsappLink; // Link to WhatsApp chat (pre-filled)

    private Long createdAt;
    private Long updatedAt;

    public enum ConsultationStatus {
        PENDING,
        ACCEPTED,
        COMPLETED
    }
}
