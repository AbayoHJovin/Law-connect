package com.legal.lawconnect.services.specialization;

import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Specialization;
import com.legal.lawconnect.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpecializationService implements ISpecializationService {
    private final SpecializationRepository specializationRepository;

    @Override
    public Specialization addSpecialization(Specialization specialization) {
            if (specializationRepository.findByName(specialization.getName()) != null) {
            throw  new AlreadyExistsException(specialization.getName() + " already exists!");
            }
            specializationRepository.save(specialization);
            return specialization;
    }

    @Override
    public Specialization updateSpecialization(String newSpecializationName, UUID specializationId) {
        Specialization specialization = specializationRepository.findbyUUID(specializationId);
        if (specialization == null) {
            throw new ResourceNotFoundException(newSpecializationName+ " not found!");
        }
        specialization.setName(newSpecializationName);
        return specializationRepository.save(specialization);

    }


    @Override
    public List<Specialization> getSpecializations() {
        return specializationRepository.findAll();
    }
}
