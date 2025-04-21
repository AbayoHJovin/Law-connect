package com.legal.lawconnect.dto;

import com.legal.lawconnect.model.Consultation;
import lombok.Data;

import java.util.UUID;

@Data
public class ConsultationDto {
    private UUID id;
    private String subject;
    private String description;
    private Long createdAt;
    private Consultation.ConsultationStatus status;
    private UUID citizenId;
    private UUID lawyerID;
}
