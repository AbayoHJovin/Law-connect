package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LawyerRepository extends JpaRepository<Lawyer, UUID> {

    boolean existsByLicenseNumberOrEmailIgnoreCase(String licenseNumber, String email);

    Lawyer findByEmail(String email);

    Lawyer findByPhoneNumber(String phoneNumber);

    String phoneNumber(String phoneNumber);
    // Get lawyers with average rating BELOW a certain value
    @Query("SELECT l FROM Lawyer l JOIN l.ratings r GROUP BY l HAVING AVG(r.rating) < :rating")
    List<Lawyer> findLawyerWithRatingsBelow(@Param("rating") double rating);

    // Get lawyers with average rating ABOVE a certain value
    @Query("SELECT l FROM Lawyer l JOIN l.ratings r GROUP BY l HAVING AVG(r.rating) > :rating")
    List<Lawyer> findLawyerWithRatingsAbove(@Param("rating") double rating);

    // Get lawyers with average rating EQUAL to a certain value
    @Query("SELECT l FROM Lawyer l JOIN l.ratings r GROUP BY l HAVING AVG(r.rating) = :rating")
    List<Lawyer> findLawyerWithExactRating(@Param("rating") double rating);

    boolean existsByEmailOrPhoneNumber(String email, String phoneNumber);
}
