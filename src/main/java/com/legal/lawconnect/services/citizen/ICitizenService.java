package com.legal.lawconnect.services.citizen;

import com.legal.lawconnect.dto.CitizenDto;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.requests.*;

import java.util.List;
import java.util.UUID;

public interface ICitizenService {
    Citizen addCitizen(AddCitizenRequest citizen);
    List<Citizen> getAllCitizens();
    Citizen getCitizenById(UUID id);
    List<Citizen> getCitizensByLocation(String location);
    Citizen getCitizenByPhoneNumber(String phoneNumber);
    Citizen getCitizenByEmail(String email);
    void rateLawyer(AddRatingRequest request,String email);
    void changePassword(ChangePasswordRequest request);
    void changeLanguagePreference(String languagePreference, String citizenEmail);
    Citizen updateCitizen(UpdateCitizenRequest citizen, String email);
    void deleteCitizen(UUID id);
    Citizen findCitizenByPhoneNumberAndPassword(PhoneLoginRequest phoneLoginRequest);
    Citizen findCitizenByEmailAndPassword(EmailLoginRequest emailLoginRequest);
    CitizenDto convertCitizenToDto(Citizen citizen);
    List<CitizenDto> getConvertedCitizens(List<Citizen> citizens);

}
