package com.legal.lawconnect.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The refresh token string
    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne
    @JoinColumn(name = "citizen_id", unique = true, referencedColumnName = "id")
    private Citizen citizen;

    @OneToOne
    @JoinColumn(name="lawyer_id", unique = true, referencedColumnName = "id")
    private Lawyer lawyer;

    @Column(nullable = false)
    private Instant expiryDate;

}