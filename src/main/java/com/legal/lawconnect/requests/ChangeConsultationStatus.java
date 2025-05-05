package com.legal.lawconnect.requests;

import com.legal.lawconnect.model.Consultation;
import lombok.Data;

import java.util.UUID;

@Data
public class ChangeConsultationStatus {
    UUID consultationId;
    Consultation.ConsultationStatus status;
}
