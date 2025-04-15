package com.legal.lawconnect.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class AddRatingRequest {
    private UUID lawyerId;
    private UUID citizenId;
    int score;
    String reviewText;
}
