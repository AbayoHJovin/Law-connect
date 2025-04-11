package com.legal.lawconnect.model;

import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String fullName;
    private String email;
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private Role role; // 'citizen', 'lawyer', or 'admin'

    private String profilePhoto;  // Optional field for profile picture
    private String languagePreference; // 'kinyarwanda' or 'english'

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus; // 'pending', 'approved', 'rejected'

    private boolean isActive; // Whether the user is active

    private Long createdAt;
    private Long updatedAt;

    public enum Role {
        CITIZEN,
        LAWYER,
        ADMIN
    }

    public enum AccountStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
