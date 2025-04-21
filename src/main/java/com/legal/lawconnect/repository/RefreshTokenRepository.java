package com.legal.lawconnect.repository;

import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByCitizen(Citizen citizen);

    Optional<RefreshToken> findByLawyer(Lawyer lawyer);

    void deleteByCitizen(Citizen citizen);
    void deleteByLawyer(Lawyer lawyer);
    
}
