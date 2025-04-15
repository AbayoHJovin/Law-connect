package com.legal.lawconnect.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"citizen_id", "lawyer_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "citizen_id")
    private Citizen citizen;

    @ManyToOne(optional = false)
    @JoinColumn(name = "lawyer_id")
    private Lawyer lawyer;

    private int rating;
    private String reviewText;
    private Long createdAt;

    public Rating(Citizen citizen, Lawyer lawyer, int rating, String reviewText) {
        this.citizen = citizen;
        this.lawyer = lawyer;
        this.rating = rating;
        this.reviewText = reviewText;
        this.createdAt = System.currentTimeMillis();
    }
}
