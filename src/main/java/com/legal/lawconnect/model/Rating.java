package com.legal.lawconnect.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "citizen_id", nullable = false)
    private User citizen;

    @ManyToOne
    @JoinColumn(name = "lawyer_id", nullable = false)
    private User lawyer;

    private int rating; // E.g., from 1 to 5

    private String comment; // Optional feedback comment

    private Long createdAt;
}
