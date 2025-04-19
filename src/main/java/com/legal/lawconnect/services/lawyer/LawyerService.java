package com.legal.lawconnect.services.lawyer;

import com.legal.lawconnect.dto.CitizenDto;
import com.legal.lawconnect.dto.LawyerDto;
import com.legal.lawconnect.dto.SpecializationDto;
import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.exceptions.UnauthorizedActionException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.Specialization;
import com.legal.lawconnect.repository.LawyerRepository;
import com.legal.lawconnect.repository.SpecializationRepository;
import com.legal.lawconnect.requests.*;
import com.legal.lawconnect.services.specialization.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LawyerService implements ILawyerService {
    private final LawyerRepository lawyerRepository;
    private final SpecializationRepository specializationRepository;
    private final PasswordEncoder passwordEncoder;
    private final SpecializationService specializationService;
    private final ModelMapper modelMapper;

    @Override
    public Lawyer save(AddLawyerRequest lawyer) {
        boolean exists = lawyerRepository.existsByLicenseNumberOrEmailIgnoreCase(
                lawyer.getLicenseNumber(),
                lawyer.getEmail()
        );

        if (exists) {
            throw new AlreadyExistsException("Lawyer already exists!");
        }

        // Reuse specialization resolution logic
        List<Specialization> specializationList = resolveSpecializations(lawyer.getSpecialization());

        // Create and save the new lawyer
        Lawyer newLawyer = createLawyer(lawyer, specializationList);
        return lawyerRepository.save(newLawyer);
    }

    private Lawyer createLawyer(AddLawyerRequest request, List<Specialization> specialization){
        String hashedPassword = passwordEncoder.encode(request.getPassword());
    return new Lawyer(
            request.getFullName(),
            hashedPassword,
            request.getEmail(),
            request.getPhoneNumber(),
            request.getLanguagePreference(),
            request.getLicenseNumber(),
            request.getYearsOfExperience(),
            request.getLocation(),
            specialization
            );
    }
    private List<Specialization> resolveSpecializations(List<SpecializationRequest> specializationRequests) {
        List<Specialization> specializationList = new ArrayList<>();

        for (SpecializationRequest s : specializationRequests) {
            Specialization exists = specializationRepository.findByName(s.getSpecializationName());
            if (exists == null) {
                exists = specializationService.addSpecialization(s.getSpecializationName());
            }
            specializationList.add(exists);
        }

        return specializationList;
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
    public Lawyer updateLawyer(UpdateLawyerRequest request, UUID id) {
        return lawyerRepository.findById(id)
                .map(existingLawyer-> updateExistingLawyer(existingLawyer,request))
                .map(lawyerRepository::save)
                .orElseThrow(()-> new ResourceNotFoundException("Lawyer not found!"));
    }

    private Lawyer updateExistingLawyer(Lawyer existingLawyer, UpdateLawyerRequest request){
       existingLawyer.setFullName(request.getFullName());
       existingLawyer.setEmail(request.getEmail());
       existingLawyer.setPhoneNumber(request.getPhoneNumber());
       existingLawyer.setLanguagePreference(request.getLanguagePreference());
       existingLawyer.setLicenseNumber(request.getLicenseNumber());
       existingLawyer.setYearsOfExperience(request.getYearsOfExperience());
       existingLawyer.setLocation(request.getLocation());
       List<SpecializationRequest> specialization = request.getSpecialization();
       List<Specialization> specializationList = new ArrayList<Specialization>();
       specialization.forEach(s -> {
           Specialization exists = specializationRepository.findByName(s.getSpecializationName());
           if (exists == null) {
           exists = specializationService.addSpecialization(s.getSpecializationName());
           }
           specializationList.add(exists);
       });
       existingLawyer.setSpecialization(specializationList);
       return existingLawyer;
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

    @Override
    public Lawyer findLawyerByEmailAndPassword(EmailLoginRequest request) {
        Lawyer lawyer = lawyerRepository.findByEmail(request.getEmail());
        if(lawyer == null){
            throw new ResourceNotFoundException("Lawyer not found");
        }
        if(!passwordEncoder.matches(request.getPassword(), lawyer.getPassword())){
            throw new UnauthorizedActionException("Passwords do not match");
        }
        return lawyer;
    }

    @Override
    public Lawyer findLawyerByPhoneAndPassword(PhoneLoginRequest request) {

        Lawyer lawyer = lawyerRepository.findByPhoneNumber(request.getPhoneNumber());
        if(lawyer == null){
            throw new ResourceNotFoundException("Citizen not found");
        }
        if(!passwordEncoder.matches(request.getPassword(), lawyer.getPassword())){
            throw new UnauthorizedActionException("Passwords do not match");
        }
        return lawyer;
    }

    @Override
    public List<Lawyer> findLawyersByRatingScoresBelow(int score) {
    return lawyerRepository.findLawyerWithRatingsBelow(score);
    }

    @Override
    public List<Lawyer> findLawyersByRatingScoresAbove(int score) {
        return lawyerRepository.findLawyerWithRatingsAbove(score);
    }

    @Override
    public List<Lawyer> findLawyersByRatingScoresEqualsTo(int score) {
   return lawyerRepository.findLawyerWithExactRating(score);
    }

    @Override
    public void changeLanguagePreference(String languagePreference, UUID lawyerId) {
        Lawyer lawyer = lawyerRepository.findById(lawyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found"));

        lawyer.setLanguagePreference(languagePreference);
        lawyerRepository.save(lawyer);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        Lawyer oldLawyer = lawyerRepository.findById(request.getOwnerId())
                .orElseThrow(()-> new ResourceNotFoundException("Lawyer not found"));

        if(!passwordEncoder.matches(request.getOldPassword(), oldLawyer.getPassword())){
            throw new UnauthorizedActionException("Passwords do not match");
        }

        oldLawyer.setPassword(passwordEncoder.encode(request.getNewPassword()));
        lawyerRepository.save(oldLawyer);
    }

    @Override
    public LawyerDto convertLawyerToDto(Lawyer lawyer) {
        LawyerDto lawyerDto = modelMapper.map(lawyer, LawyerDto.class);
        List<Specialization> specializations = specializationRepository.findSpecializationByLawyer_Id(lawyer.getId());
        List<SpecializationDto> specializationDtos = specializations.stream()
                .map(image -> modelMapper.map(image, SpecializationDto.class))
                .toList();
        lawyerDto.setSpecializations(specializationDtos);
        return lawyerDto;
    }

    @Override
    public List<LawyerDto> getConvertedLawyers(List<Lawyer> lawyers) {
        return lawyers.stream().map(this::convertLawyerToDto).toList();
    }
}
