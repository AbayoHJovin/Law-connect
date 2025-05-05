package com.legal.lawconnect.services.rating;

import com.legal.lawconnect.dto.RatingDto;
import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.exceptions.UnauthorizedActionException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.Rating;
import com.legal.lawconnect.repository.LawyerRepository;
import com.legal.lawconnect.repository.RatingRepository;
import com.legal.lawconnect.requests.AddRatingRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService implements IRatingService {
    private final RatingRepository ratingRepository;
    private final LawyerRepository lawyerRepository;

    @Override
    public List<RatingDto> getRatingsOfLawyer(String email) {
    List<Rating> allRatings = ratingRepository.findAll();
    List<Rating> lawyerRatings = new ArrayList<>();
    allRatings.forEach(rating -> {
        if(Objects.equals(rating.getLawyer().getEmail(), email)){
            lawyerRatings.add(rating);
        }
    });
    return lawyerRatings.stream().map(rating -> {
            RatingDto dto = new RatingDto();
            dto.setRatingId(rating.getId());
            dto.setCitizenName(rating.getCitizen().getFullName());
            dto.setRatingScore(rating.getRating());
            dto.setReviewText(rating.getReviewText());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<RatingDto> getAllRatings(UUID lawyerId) {
        List<Rating> allRatings = ratingRepository.findAll();
        List<Rating> lawyerRatings = new ArrayList<>();
        if(lawyerRepository.findById(lawyerId).isEmpty()){
            throw new ResourceNotFoundException("The Lawyer doesn't exist");
        }
        allRatings.forEach(rating -> {
            if(Objects.equals(rating.getLawyer().getId(), lawyerId)){
                lawyerRatings.add(rating);
            }
        });
        return lawyerRatings.stream().map(rating -> {
            RatingDto dto = new RatingDto();
            dto.setRatingId(rating.getId());
            dto.setCitizenName(rating.getCitizen().getFullName());
            dto.setRatingScore(rating.getRating());
            dto.setReviewText(rating.getReviewText());
            return dto;
        }).collect(Collectors.toList());
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
    public void addRating(AddRatingRequest request, Citizen citizen, Lawyer lawyer,UUID citizenId) {
       Rating hasRated = ratingRepository.findRatingsByCitizen_IdAndLawyer_Id(citizenId,request.getLawyerId());
       if (hasRated != null) {
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

    @Override
    public double calculateAverageRating(UUID lawyerId) {
        List<Rating> ratings = ratingRepository.findRatingsByLawyer_Id(lawyerId);
        if (ratings.isEmpty()) {
            return 0;
        }

        return ratings.stream()
                .mapToInt(Rating::getRating)
                .average()
                .orElse(0);
    }


}
