package com.legal.lawconnect.services.lawyer;

import com.legal.lawconnect.model.Consultation;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.requests.AddLawyerRequest;
import com.legal.lawconnect.requests.UpdateLawyerRequest;

import java.util.List;
import java.util.UUID;

public interface ILawyerService {
    Lawyer save(AddLawyerRequest lawyer);
    List<Lawyer> findAll();
    Lawyer findById(UUID id);
    Lawyer findByEmail(String email);
    Lawyer findByPhone(String phone);
    Lawyer updateLawyer(UpdateLawyerRequest lawyer, UUID id);
    void deleteLawyer(UUID id);
    void setAvailabilityForWork(UUID lawyerId, boolean availability);
    Lawyer findLawyerByEmailAndPassword(String email, String password);
    Lawyer findLawyerByPhoneAndPassword(String phone, String password);
    List<Lawyer> findLawyersByRatingScoresBelow(int score);
    List<Lawyer> findLawyersByRatingScoresAbove(int score);
    List<Lawyer> findLawyersByRatingScoresEqualsTo(int score);
    void changeLanguagePreference(String languagePreference, UUID citizenId);
    void changePassword(String oldPassword, String newPassword, UUID citizenId);
}
