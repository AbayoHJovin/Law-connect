package com.legal.lawconnect.services.auth;

import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.RefreshToken;
import com.legal.lawconnect.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepository;

    // 🔁 Create new refresh token
    @Transactional
    public String createRefreshTokenByLawyer(Lawyer lawyer) {
        RefreshToken token = refreshTokenRepository.findByLawyer(lawyer)
                .orElse(new RefreshToken());

        token.setLawyer(lawyer);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days
        return refreshTokenRepository.save(token).getToken();
    }

    @Transactional
    public String createRefreshTokenByCitizen(Citizen citizen) {
        RefreshToken token = refreshTokenRepository.findByCitizen(citizen)
                .orElse(new RefreshToken());

        token.setCitizen(citizen);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days
        return refreshTokenRepository.save(token).getToken();
    }

    @Transactional
    public Optional<RefreshToken> verifyRefreshToken(String tokenStr) {
        return refreshTokenRepository.findByToken(tokenStr)
                .filter(token -> token.getExpiryDate().isAfter(Instant.now()));
    }

    @Transactional
    public void revokeRefreshTokenLawyer(Lawyer user) {
        refreshTokenRepository.deleteByLawyer(user);
    }

    @Transactional
    public void revokeRefreshTokenCitizen(Citizen user) {
        refreshTokenRepository.deleteByCitizen(user);
    }
}
