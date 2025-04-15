package com.legal.lawconnect.services.citizen;

import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.requests.AddCitizenRequest;
import com.legal.lawconnect.requests.AddRatingRequest;
import com.legal.lawconnect.requests.UpdateCitizenRequest;

import java.util.List;
import java.util.UUID;

public interface ICitizenService {
    Citizen addCitizen(AddCitizenRequest citizen);
    List<Citizen> getAllCitizens();
    Citizen getCitizenById(UUID id);
    List<Citizen> getCitizensByLocation(String location);
    Citizen getCitizenByPhoneNumber(String phoneNumber);
    Citizen getCitizenByEmail(String email);
    void rateLawyer(AddRatingRequest request);
    void changePassword(String oldPassword, String newPassword, UUID citizenId);
    void changeLanguagePreference(String languagePreference, UUID citizenId);
    Citizen UpdateCitizen(UpdateCitizenRequest citizen, UUID citizenId);
    void deleteCitizen(UUID id);
    Citizen findCitizenByPhoneNumberAndPassword(String phoneNumber, String password);
    Citizen findCitizenByEmailAndPassword(String email, String password);

}
