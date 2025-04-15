package com.legal.lawconnect.services.specialization;

import com.legal.lawconnect.model.Specialization;

import java.util.List;
import java.util.UUID;

public interface ISpecializationService {
    Specialization addSpecialization(Specialization specialization);
    Specialization updateSpecialization(String specializationName, UUID specializationId);
    List<Specialization> getSpecializations();
}
