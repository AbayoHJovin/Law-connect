package com.legal.lawconnect.services.rating;

import com.legal.lawconnect.dto.RatingDto;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.Rating;
import com.legal.lawconnect.requests.AddRatingRequest;

import java.util.List;
import java.util.UUID;

public interface IRatingService {
    List<RatingDto> getRatingsOfLawyer(UUID lawyerId);
    List<Rating> getRatingsByCitizen(UUID citizenId);
    void addRating(AddRatingRequest request, Citizen citizen, Lawyer lawyer,UUID citizenId);
    void removeRating(UUID ratingId, UUID citizenId);
    double calculateAverageRating(UUID lawyerId);
}
