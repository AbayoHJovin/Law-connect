package com.legal.lawconnect.services.lawyer;

import com.legal.lawconnect.dto.CitizenDto;
import com.legal.lawconnect.dto.LawyerDto;
import com.legal.lawconnect.dto.SpecializationDto;
import com.legal.lawconnect.enums.UserRoles;
import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.exceptions.UnauthorizedActionException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.Specialization;
import com.legal.lawconnect.repository.LawyerRepository;
import com.legal.lawconnect.repository.SpecializationRepository;
import com.legal.lawconnect.requests.*;
import com.legal.lawconnect.services.rating.RatingService;
import com.legal.lawconnect.services.specialization.SpecializationService;
import com.legal.lawconnect.validation.ValidateLawyerFields;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RatingService ratingService;
    private final ValidateLawyerFields validateLawyerFields;

    @Override
    public Lawyer save(AddLawyerRequest lawyer) {
        // Validate required fields
        validateLawyerFields.validateLawyerRequestFields(lawyer);

        boolean exists = lawyerRepository.existsByLicenseNumberOrEmailIgnoreCase(
                lawyer.getLicenseNumber(),
                lawyer.getEmail()
        );

        if (exists) {
            throw new AlreadyExistsException("Lawyer already exists!");
        }

        if (!lawyer.getPassword().equals(lawyer.getConfirmPassword())) {
            throw new UnauthorizedActionException("Passwords don't match!");
        }

        List<String> invalidSpecializations = new ArrayList<>();
        List<Specialization> specializationList = resolveValidSpecializations(lawyer.getSpecialization(), invalidSpecializations);
        if (specializationList.isEmpty()) {
            throw new IllegalArgumentException("None of the provided specializations are valid");
        }
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
                specialization,
                UserRoles.LAWYER,
                request.getLawyerBio()
        );
    }

    private List<Specialization> resolveValidSpecializations(List<SpecializationRequest> specializationRequests, List<String> invalids) {
        List<Specialization> validSpecializations = new ArrayList<>();

        for (SpecializationRequest s : specializationRequests) {
            Specialization specialization = specializationRepository.findByName(s.getSpecializationName());
            if (specialization != null) {
                validSpecializations.add(specialization);
            } else {
                invalids.add(s.getSpecializationName());
            }
        }
        return validSpecializations;
    }

    @Override
    @Transactional
    public List<Lawyer> findAll() {
        List<Lawyer> lawyers= lawyerRepository.findAll();
        System.out.println("Lawyers found!");
        lawyers.forEach(System.out::println);
        return lawyers;
    }

    @Override
    @Transactional
    public Lawyer findById(UUID id) {
        return lawyerRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Lawyer not found!"));
    }

    @Override
    @Transactional
    public Lawyer findByEmail(String email) {
        return lawyerRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Lawyer findByPhone(String phone) {
        return lawyerRepository.findByPhoneNumber(phone);
    }

    @Override
    @Transactional
    public Lawyer updateLawyer(UpdateLawyerRequest request,String email) {
        Lawyer existingLawyer = lawyerRepository.findByEmail(email);
        if (existingLawyer == null) {
           throw new ResourceNotFoundException("Lawyer not found with email: " + email);

        }

        List<String> invalidSpecializations = new ArrayList<>();
        updateExistingLawyer(existingLawyer, request, invalidSpecializations);
        return lawyerRepository.save(existingLawyer);

    }

    private void updateExistingLawyer(Lawyer existingLawyer, UpdateLawyerRequest request, List<String> invalidSpecializations) {
        Optional.ofNullable(request.getFullName()).ifPresent(existingLawyer::setFullName);
        Optional.ofNullable(request.getPhoneNumber()).ifPresent(existingLawyer::setPhoneNumber);
        Optional.ofNullable(request.getLanguagePreference()).ifPresent(existingLawyer::setLanguagePreference);
        Optional.ofNullable(request.getLicenseNumber()).ifPresent(existingLawyer::setLicenseNumber);
        Optional.ofNullable(request.getYearsOfExperience()).ifPresent(existingLawyer::setYearsOfExperience);
        Optional.ofNullable(request.getLocation()).ifPresent(existingLawyer::setLocation);



        if (request.getSpecialization() != null && !request.getSpecialization().isEmpty()) {
            List<Specialization> validSpecializations = new ArrayList<>();

            for (SpecializationRequest s : request.getSpecialization()) {
                Specialization specialization = specializationRepository.findByName(s.getSpecializationName());
                if (specialization != null) {
                    validSpecializations.add(specialization);
                } else {
                    invalidSpecializations.add(s.getSpecializationName());
                }
            }

            existingLawyer.setSpecialization(validSpecializations);
        }
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
    @Transactional
    public Lawyer findLawyerByEmailAndPassword(EmailLoginRequest request) {
        Lawyer lawyer = lawyerRepository.findByEmail(request.getEmail());
        if(lawyer == null){
            throw new ResourceNotFoundException("Invalid Credentials");
        }
        if(!passwordEncoder.matches(request.getPassword(), lawyer.getPassword())){
            throw new UnauthorizedActionException("Invalid Credentials");
        }
        return lawyer;
    }

    @Override
    @Transactional
    public Lawyer findLawyerByPhoneAndPassword(PhoneLoginRequest request) {

        Lawyer lawyer = lawyerRepository.findByPhoneNumber(request.getPhoneNumber());
        if(lawyer == null){
            throw new ResourceNotFoundException("Invalid Credentials");
        }
        if(!passwordEncoder.matches(request.getPassword(), lawyer.getPassword())){
            throw new UnauthorizedActionException("Invalid Credentials");
        }
        return lawyer;
    }

    @Override
    @Transactional
    public List<Lawyer> findLawyersByRatingScoresBelow(int score) {
    return lawyerRepository.findLawyerWithRatingsBelow(score);
    }

    @Override
    @Transactional
    public List<Lawyer> findLawyersByRatingScoresAbove(int score) {
        return lawyerRepository.findLawyerWithRatingsAbove(score);
    }

    @Override
    @Transactional
    public List<Lawyer> findLawyersByRatingScoresEqualsTo(int score) {
   return lawyerRepository.findLawyerWithExactRating(score);
    }

    @Override
    @Transactional
    public void changeLanguagePreference(String languagePreference, UUID lawyerId) {
        Lawyer lawyer = lawyerRepository.findById(lawyerId)
                .orElseThrow(() -> new ResourceNotFoundException("Lawyer not found"));

        lawyer.setLanguagePreference(languagePreference);
        lawyerRepository.save(lawyer);
    }

    @Override
    @Transactional
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
        double averageRating = ratingService.calculateAverageRating(lawyer.getId());
        lawyerDto.setAverageRating(averageRating);
        return lawyerDto;
    }

    @Override
    public List<LawyerDto> getConvertedLawyers(List<Lawyer> lawyers) {
        return lawyers.stream().map(this::convertLawyerToDto).toList();
    }

    @Override
    @Transactional
    public String getLawyerPhoneNumber(UUID lawyerId) {
        Lawyer lawyer = lawyerRepository.findById(lawyerId)
                .orElseThrow(()-> new ResourceNotFoundException("Lawyer Not found!"));
        return lawyer.getPhoneNumber();
    }
}
