package com.legal.lawconnect.services.lawyer;

import com.legal.lawconnect.dto.CitizenDto;
import com.legal.lawconnect.dto.LawyerDto;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Consultation;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.requests.*;

import java.util.List;
import java.util.UUID;

public interface ILawyerService {
    Lawyer save(AddLawyerRequest lawyer);
    List<Lawyer> findAll();
    Lawyer findById(UUID id);
    Lawyer findByEmail(String email);
    Lawyer findByPhone(String phone);
    Lawyer updateLawyer(UpdateLawyerRequest lawyer,String email);
    void deleteLawyer(UUID id);
    void setAvailabilityForWork(UUID lawyerId, boolean availability);
    Lawyer findLawyerByEmailAndPassword(EmailLoginRequest request);
    Lawyer findLawyerByPhoneAndPassword(PhoneLoginRequest request);
    List<Lawyer> findLawyersByRatingScoresBelow(int score);
    List<Lawyer> findLawyersByRatingScoresAbove(int score);
    List<Lawyer> findLawyersByRatingScoresEqualsTo(int score);
    void changeLanguagePreference(String languagePreference, UUID citizenId);
    void changePassword(ChangePasswordRequest request);
    LawyerDto convertLawyerToDto(Lawyer lawyer);
    List<LawyerDto> getConvertedLawyers(List<Lawyer> citizens);
    String getLawyerPhoneNumber(UUID lawyerId);

}
