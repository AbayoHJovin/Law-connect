package com.legal.lawconnect.services.rating;

import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.exceptions.UnauthorizedActionException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.Rating;
import com.legal.lawconnect.repository.RatingRepository;
import com.legal.lawconnect.requests.AddRatingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RatingService implements IRatingService {
    private final RatingRepository ratingRepository;
    @Override
    public List<Rating> getRatingsOfLawyer(UUID lawyerId) {
        List<Rating> rates = ratingRepository.findRatingsByLawyer_Id(lawyerId);
        if (rates.isEmpty()) {
            throw new ResourceNotFoundException("The lawyer hasn't been rated yet!");
        }
        return rates;
    }

    @Override
    public List<Rating> getRatingsByCitizen(UUID citizenId) {
        List<Rating> rates = ratingRepository.findRatingsByCitizen_Id((citizenId));
        if (rates.isEmpty()) {
            throw new ResourceNotFoundException("The citizen hasn't rated any lawyer");
        }
        return rates;
    }

    @Override
    public void addRating(AddRatingRequest request, Citizen citizen, Lawyer lawyer) {
       Rating hasRated = ratingRepository.findRatingsByCitizen_IdAndLawyer_Id(request.getCitizenId(),request.getLawyerId());
       if (hasRated== null) {
           throw new AlreadyExistsException("The Citizen has already rated the lawyer");
       }
        ratingRepository.save(createRating(request, citizen, lawyer));
    }
    private Rating createRating(AddRatingRequest rating, Citizen cit, Lawyer lawyer) {
        return new Rating(
                cit,
                lawyer,
                rating.getScore(),
                rating.getReviewText()
        );
    }
    @Override
    public void removeRating(UUID ratingId, UUID citizenId) {
        Rating rating = ratingRepository.findById(ratingId)
                .orElseThrow(() -> new ResourceNotFoundException("Rating not found"));

        if (!rating.getCitizen().getId().equals(citizenId)) {
            throw new UnauthorizedActionException("You are not allowed to delete this rating.");
        }

        ratingRepository.delete(rating);
    }

}
