package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {
    List<Rating> findRatingsByLawyer_Id(UUID lawyerId);

    List<Rating> findRatingsByCitizen_Id(UUID citizenId);

    Rating findRatingsByCitizen_IdAndLawyer_Id(UUID citizenId, UUID lawyerId);

    List<Rating> findRatingsByLawyer_Email(String lawyerEmail);
}
