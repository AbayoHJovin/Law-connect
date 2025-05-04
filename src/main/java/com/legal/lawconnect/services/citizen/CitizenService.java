package com.legal.lawconnect.services.citizen;

import com.legal.lawconnect.enums.UserRoles;
import com.legal.lawconnect.repository.LawyerRepository;
import com.legal.lawconnect.requests.*;
import org.modelmapper.ModelMapper;
import com.legal.lawconnect.dto.CitizenDto;
import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.exceptions.UnauthorizedActionException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.repository.CitizenRepository;
import com.legal.lawconnect.services.lawyer.ILawyerService;
import com.legal.lawconnect.services.rating.IRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CitizenService implements ICitizenService {

  private final CitizenRepository citizenRepository;
  private final PasswordEncoder passwordEncoder;
  private final ILawyerService lawyerService;
  private final IRatingService ratingService;
  private final ModelMapper modelMapper;
    private final LawyerRepository lawyerRepository;

    @Override
    public Citizen addCitizen(AddCitizenRequest citizen) {
        if (citizen.getEmail() == null && citizen.getPhoneNumber() == null) {
            throw new IllegalArgumentException("Either email or phoneNumber must be provided");
        }
        if(!Objects.equals(citizen.getPassword(), citizen.getConfirmPassword())){
            throw new IllegalArgumentException("Passwords do not match");
        }
      boolean exists = citizenRepository.existsByEmailOrPhoneNumber(citizen.getEmail(), citizen.getPhoneNumber());
        if(lawyerRepository.findByEmail(citizen.getEmail()) != null || lawyerRepository.findByPhoneNumber(citizen.getPhoneNumber()) != null) {
            throw new AlreadyExistsException("Email or phone number is already in use");
        }
      if (exists) {
        throw new AlreadyExistsException("Citizen already exists");
      }
      return citizenRepository.save(createCitizen(citizen));

    }

    private Citizen createCitizen(AddCitizenRequest citizen) {
      String hashedPassword = passwordEncoder.encode(citizen.getPassword());
        return new Citizen(
                citizen.getFullName(),
                citizen.getEmail(),
                citizen.getPhoneNumber(),
                citizen.getLanguagePreference(),
                hashedPassword,
                citizen.getLocation(),
                UserRoles.CITIZEN
        );
    }
    @Override
    public List<Citizen> getAllCitizens() {
        List<Citizen> citizens = citizenRepository.findAll();
        if(citizens.isEmpty()){
            throw new ResourceNotFoundException("No Citizens found");
        }
        return citizens;
    }

    @Override
    public Citizen getCitizenById(UUID id) {
        return citizenRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Citizen not found"));
    }

    @Override
    public List<Citizen> getCitizensByLocation(String location) {
        List<Citizen> citizens =  citizenRepository.findByLocation(location);
        if(citizens.isEmpty()){
          throw new ResourceNotFoundException("Citizen not found in that location");
        }
        return citizens;
    }

    @Override
    public Citizen getCitizenByPhoneNumber(String phoneNumber) {
        Citizen cit = citizenRepository.findByPhoneNumber(phoneNumber);
        if(cit == null){
          throw new ResourceNotFoundException("Citizen not found");
        }
        return cit;
    }

    @Override
    public Citizen getCitizenByEmail(String email) {
        Citizen cit = citizenRepository.findByEmail(email);
        if(cit == null){
          throw new ResourceNotFoundException("Citizen not found");
        }
        return cit;
    }

    @Override
    public void rateLawyer(AddRatingRequest request, String email) {
    Citizen citizen = getCitizenByEmail(email);
    Lawyer lawyer = lawyerService.findById(request.getLawyerId());
    if(citizen == null){
      throw new ResourceNotFoundException("Citizen not found");
    }
    if(lawyer == null){
      throw new ResourceNotFoundException("Lawyer not found");
    }
     ratingService.addRating(request, citizen , lawyer, citizen.getId());
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
    Citizen oldCitizen = getCitizenById(request.getOwnerId());
    if(oldCitizen == null){
      throw new ResourceNotFoundException("Citizen not found");
    }
    if(!passwordEncoder.matches(request.getOldPassword(), oldCitizen.getPassword())){
      throw new UnauthorizedActionException("Passwords do not match");
    }

    oldCitizen.setPassword(passwordEncoder.encode(request.getNewPassword()));
    citizenRepository.save(oldCitizen);
    }

    @Override
    public void changeLanguagePreference(String languagePreference, String citizenEmail) {
    Citizen citizen = getCitizenByEmail(citizenEmail);
    if(citizen == null){
      throw new ResourceNotFoundException("Citizen not found");
    }
    citizen.setLanguagePreference(languagePreference);
    citizenRepository.save(citizen);
    }

    @Override
    @Transactional
    public Citizen updateCitizen(UpdateCitizenRequest request, String email) {
        Citizen cit= citizenRepository.findByEmail(email);
        if(cit == null){
            throw new ResourceNotFoundException("Citizen not found");
        }
        Citizen updatedCitizen = updateExistingCitizen(cit,request);
        citizenRepository.save(updatedCitizen);
        return updatedCitizen;
    }
    private Citizen updateExistingCitizen(Citizen existingCitizen, UpdateCitizenRequest request){
        if (request.getFullName() != null)
            existingCitizen.setFullName(request.getFullName());

        if (request.getPhoneNumber() != null)
            existingCitizen.setPhoneNumber(request.getPhoneNumber());

        if (request.getLanguagePreference() != null)
            existingCitizen.setLanguagePreference(request.getLanguagePreference());

        if (request.getLocation() != null)
            existingCitizen.setLocation(request.getLocation());

        return existingCitizen;
    }


    @Override
    public void deleteCitizen(UUID id) {
    citizenRepository.findById(id)
            .ifPresentOrElse(citizenRepository::delete,
                    ()-> {throw new ResourceNotFoundException("Citizen not found");}
                    );
    }

    @Override
    public Citizen findCitizenByPhoneNumberAndPassword(PhoneLoginRequest loginRequest) {
        Citizen citizen = citizenRepository.findByPhoneNumber(loginRequest.getPhoneNumber());
        if(citizen == null){
          throw new ResourceNotFoundException("Invalid Credentials");
        }
        if(!passwordEncoder.matches(loginRequest.getPassword(), citizen.getPassword())){
          throw new ResourceNotFoundException("Invalid Credentials");
        }
        return citizen;
    }

    @Override
    public Citizen findCitizenByEmailAndPassword(EmailLoginRequest emailLoginRequest) {
        Citizen citizen = citizenRepository.findByEmail(emailLoginRequest.getEmail());
        if(citizen == null){
          throw new ResourceNotFoundException("Citizen not found");
        }
        if(!passwordEncoder.matches(emailLoginRequest.getPassword(), citizen.getPassword())){
          throw new UnauthorizedActionException("Passwords do not match");
        }
        return citizen;
    }

    @Override
    public CitizenDto convertCitizenToDto(Citizen citizen) {
        return modelMapper.map(citizen, CitizenDto.class);
    }

    @Override
    public List<CitizenDto> getConvertedCitizens(List<Citizen> citizens) {
        return citizens.stream().map(this::convertCitizenToDto).toList();
    }
}
