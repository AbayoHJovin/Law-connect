package com.legal.lawconnect.services.specialization;

import com.legal.lawconnect.dto.SpecializationDto;
import com.legal.lawconnect.model.Specialization;

import java.util.List;
import java.util.UUID;

public interface ISpecializationService {
    Specialization addSpecialization(String specialization);
    Specialization updateSpecialization(String specializationName, UUID specializationId);
    List<Specialization> getSpecializations();
    Specialization getSpecializationByName(String specializationName);
    List<SpecializationDto> getConvertedSpecializations(List<Specialization> specializations);
    SpecializationDto convertSpecializationToDto(Specialization specialization);
}
