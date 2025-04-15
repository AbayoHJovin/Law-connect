package com.legal.lawconnect.services.lawyer;

import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.Specialization;
import com.legal.lawconnect.repository.LawyerRepository;
import com.legal.lawconnect.repository.SpecializationRepository;
import com.legal.lawconnect.requests.AddLawyerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LawyerService implements ILawyerService {
    private final LawyerRepository lawyerRepository;
    private final SpecializationRepository specializationRepository;
    @Override
    public Lawyer save(AddLawyerRequest lawyer) {
        boolean exists = lawyerRepository.existsByLicenseNumberOrEmailIgnoreCase(
                lawyer.getLicenseNumber(),
                lawyer.getEmail()
        );

        if (exists) {
            throw new AlreadyExistsException("Lawyer already exists!");
        }

        List<Specialization> updatedSpecializations = lawyer.getSpecialization().stream()
                .map(s -> {
                    Specialization existing = specializationRepository.findByName(s.getName());
                    return existing != null ? existing : specializationRepository.save(s);
                })
                .collect(Collectors.toList());

        Lawyer newLawyer = createLawyer(lawyer, updatedSpecializations);
        return lawyerRepository.save(newLawyer);
    }
    private Lawyer createLawyer(AddLawyerRequest request, List<Specialization> specialization){
    return new Lawyer(
            request.getFullName(),
            request.getPassword(),
            request.getEmail(),
            request.getPhoneNumber(),
            request.getLanguagePreference(),
            request.getLicenseNumber(),
            request.getYearsOfExperience(),
            request.getLocation(),
            specialization
            );
    }
    @Override
    public List<Lawyer> findAll() {
        return lawyerRepository.findAll();
    }

    @Override
    public Lawyer findById(UUID id) {
        return lawyerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Lawyer not found!"));
    }

    @Override
    public Lawyer findByEmail(String email) {
        return lawyerRepository.findByEmail(email);
    }

    @Override
    public Lawyer findByPhone(String phone) {
        return lawyerRepository.findByPhoneNumber(phone);
    }

    @Override
    public Lawyer updateLawyer(Lawyer lawyer, UUID id) {
        return null;
    }

    @Override
    public void deleteLawyer(UUID id) {
    lawyerRepository.findById(id).ifPresentOrElse(lawyerRepository::delete,
            ()-> {throw new ResourceNotFoundException("The lawyer with this id doesn't exist");
    });
    }

    @Override
    public void setAvailabilityForWork(UUID lawyerId, boolean availability) {
        lawyerRepository.findById(lawyerId)
                .map(lawyer -> {
                    lawyer.setAvailableForWork(availability);
                    return lawyerRepository.save(lawyer);
                })
                .orElseThrow(()-> new ResourceNotFoundException("The lawyer doesn't exist!"));

    }
}
