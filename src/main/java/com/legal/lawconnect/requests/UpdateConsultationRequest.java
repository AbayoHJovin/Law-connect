package com.legal.lawconnect.requests;

import com.legal.lawconnect.model.Consultation;
import lombok.Data;

@Data
public class UpdateConsultationRequest {
    String subject;
    String description;
    Consultation.ConsultationStatus status;
}
