package com.legal.lawconnect.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class RatingDto {
    private UUID ratingId;
    private String citizenName;
    private int ratingScore;
    private String reviewText;
}
