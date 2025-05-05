package com.legal.lawconnect.model;

import jakarta.validation.constraints.Size;
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

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name="description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private ConsultationStatus status; // 'pending', 'accepted', 'completed'

    private Long createdAt;
    private Long updatedAt;

    public enum ConsultationStatus {
        PENDING,
        REJECTED,
        ACCEPTED,
        ONGOING,
        COMPLETED
    }

    public Consultation(Citizen citizen, Lawyer lawyer, String subject, String description, ConsultationStatus status,Long createdAt) {
        this.citizen = citizen;
        this.lawyer = lawyer;
        this.subject = subject;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }
}
