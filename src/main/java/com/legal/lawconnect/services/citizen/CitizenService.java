package com.legal.lawconnect.services.citizen;

import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.exceptions.UnauthorizedActionException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.Rating;
import com.legal.lawconnect.repository.CitizenRepository;
import com.legal.lawconnect.repository.LawyerRepository;
import com.legal.lawconnect.repository.RatingRepository;
import com.legal.lawconnect.requests.AddCitizenRequest;
import com.legal.lawconnect.requests.AddRatingRequest;
import com.legal.lawconnect.requests.UpdateCitizenRequest;
import com.legal.lawconnect.services.lawyer.ILawyerService;
import com.legal.lawconnect.services.rating.IRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CitizenService implements ICitizenService {
  private final CitizenRepository citizenRepository;
  private final PasswordEncoder passwordEncoder;
  private final ILawyerService lawyerService;
  private final IRatingService ratingService;

  @Override
    public Citizen addCitizen(AddCitizenRequest citizen) {
      boolean exists = citizenRepository.existsByEmailOrPhoneNumber(citizen.getEmail(), citizen.getPhoneNumber());
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
                citizen.getLocation()
        );
    }
    @Override
    public List<Citizen> getAllCitizens() {
        return citizenRepository.findAll();
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
    public void rateLawyer(AddRatingRequest request) {
    Citizen citizen = getCitizenById(request.getCitizenId());
    Lawyer lawyer = lawyerService.findById(request.getLawyerId());
    if(citizen == null){
      throw new ResourceNotFoundException("Citizen not found");
    }
    if(lawyer == null){
      throw new ResourceNotFoundException("Lawyer not found");
    }
     ratingService.addRating(request, citizen , lawyer);
    }

    @Override
    public void changePassword(String oldPassword, String newPassword, UUID citizenId) {
    Citizen oldCitizen = getCitizenById(citizenId);
    if(oldCitizen == null){
      throw new ResourceNotFoundException("Citizen not found");
    }
    if(!passwordEncoder.matches(oldPassword, oldCitizen.getPassword())){
      throw new UnauthorizedActionException("Passwords do not match");
    }

    oldCitizen.setPassword(passwordEncoder.encode(newPassword));
    citizenRepository.save(oldCitizen);
    }

    @Override
    public void changeLanguagePreference(String languagePreference, UUID citizenId) {
    Citizen citizen = getCitizenById(citizenId);
    if(citizen == null){
      throw new ResourceNotFoundException("Citizen not found");
    }
    citizen.setLanguagePreference(languagePreference);
    citizenRepository.save(citizen);
    }

    @Override
    public Citizen UpdateCitizen(UpdateCitizenRequest citizen, UUID citizenId) {
        return citizenRepository.findById(citizenId)
                .map(existingCitizen-> updateExistingCitizen(existingCitizen,citizen))
                .map(citizenRepository::save)
                .orElseThrow(()-> new ResourceNotFoundException("Citizen not found"));
    }
    private Citizen updateExistingCitizen(Citizen existingCitizen, UpdateCitizenRequest request){
    existingCitizen.setFullName(request.getFullName());
    existingCitizen.setEmail(request.getEmail());
    existingCitizen.setPhoneNumber(request.getPhoneNumber());
    existingCitizen.setLanguagePreference(request.getLanguagePreference());
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
    public Citizen findCitizenByPhoneNumberAndPassword(String phoneNumber, String password) {
        Citizen citizen = citizenRepository.findByPhoneNumber(phoneNumber);
        if(citizen == null){
          throw new ResourceNotFoundException("Citizen not found");
        }
        if(!passwordEncoder.matches(password, citizen.getPassword())){
          throw new UnauthorizedActionException("Passwords do not match");
        }
        return citizen;
    }

    @Override
    public Citizen findCitizenByEmailAndPassword(String email, String password) {
        Citizen citizen = citizenRepository.findByEmail(email);
        if(citizen == null){
          throw new ResourceNotFoundException("Citizen not found");
        }
        if(!passwordEncoder.matches(password, citizen.getPassword())){
          throw new UnauthorizedActionException("Passwords do not match");
        }
        return citizen;
    }
}
