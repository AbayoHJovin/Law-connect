package com.legal.lawconnect.services.specialization;

import com.legal.lawconnect.dto.SpecializationDto;
import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Specialization;
import com.legal.lawconnect.repository.SpecializationRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecializationService implements ISpecializationService {
    private final SpecializationRepository specializationRepository;
    private final ModelMapper modelMapper;

    @Override
    public Specialization addSpecialization(String specializationName) {
            if (specializationRepository.findByName(specializationName) != null) {
            throw  new AlreadyExistsException(specializationName + " already exists!");
            }
            Specialization specialization = new Specialization();
            specialization.setName(specializationName);
            specializationRepository.save(specialization);
            return specialization;
    }

    @Override
    public Specialization updateSpecialization(String newSpecializationName, UUID specializationId) {
        Specialization specialization = specializationRepository.findById(specializationId)
                .orElseThrow(()-> new ResourceNotFoundException("Specialization not found"));
        specialization.setName(newSpecializationName);
        return specializationRepository.save(specialization);

    }


    @Override
    public List<Specialization> getSpecializations() {
        return specializationRepository.findAll();
    }

    @Override
    public Specialization getSpecializationByName(String specializationName) {
        Specialization specialization = specializationRepository.findByName(specializationName);
        if (specialization == null) {
            throw new ResourceNotFoundException(specializationName + " not found!");
        }
        return specialization;
    }

    @Override
    public List<SpecializationDto> getConvertedSpecializations(List<Specialization> specializations) {
    return specializations.stream().map(this::convertSpecializationToDto).toList();
    }

    @Override
    public SpecializationDto convertSpecializationToDto(Specialization specialization) {
    return modelMapper.map(specialization, SpecializationDto.class);
    }
}
