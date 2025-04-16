package com.legal.lawconnect.requests;

import com.legal.lawconnect.model.Consultation;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateConsultationRequest {
      UUID citizenId;
      UUID lawyerId;
      String subject;
      String description;
      Consultation.ConsultationStatus status;
}
