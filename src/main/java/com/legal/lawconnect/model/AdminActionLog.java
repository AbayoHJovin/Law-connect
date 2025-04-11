package com.legal.lawconnect.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "admin_action_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;

    @ManyToOne
    @JoinColumn(name = "lawyer_id", nullable = false)
    private User lawyer;

    @Enumerated(EnumType.STRING)
    private ActionType actionType; // 'approved' or 'denied'

    private String reason; // Optional, the reason for the approval/denial

    private Long createdAt;

    public enum ActionType {
        APPROVED,
        DENIED
    }
}
