package com.legal.lawconnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String token;
    @Column(unique = true)
    private String email;
    private LocalDateTime expiryDate;

    public PasswordResetToken(String token, String email, LocalDateTime localDateTime) {
        this.token = token;
        this.email = email;
        this.expiryDate = localDateTime;
    }
}
