package com.legal.lawconnect.services.auth;

import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.RefreshToken;
import com.legal.lawconnect.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final RefreshTokenRepository refreshTokenRepository;

    // 🔁 Create new refresh token
    public RefreshToken createRefreshTokenByLawyer(Lawyer lawyer) {
        // Delete existing token (rotate)
        refreshTokenRepository.deleteByLawyer(lawyer);

        RefreshToken token = new RefreshToken();
        token.setLawyer(lawyer);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(604800)); // 7 days
        return refreshTokenRepository.save(token);
    }

    public RefreshToken createRefreshTokenByCitizen(Citizen citizen) {
        refreshTokenRepository.deleteByCitizen(citizen);

        RefreshToken token = new RefreshToken();
        token.setCitizen(citizen);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusSeconds(604800)); // 7 days
        return refreshTokenRepository.save(token);
    }
    // 🧪 Verify and return if token is valid
    public Optional<RefreshToken> verifyRefreshToken(String tokenStr) {
        return refreshTokenRepository.findByToken(tokenStr)
                .filter(token -> token.getExpiryDate().isAfter(Instant.now()));
    }

    // ❌ For logout
    public void revokeRefreshTokenLawyer(Lawyer user) {
        refreshTokenRepository.deleteByLawyer(user);
    }

    public void revokeRefreshTokenCitizen(Citizen user) {
        refreshTokenRepository.deleteByCitizen(user);
    }
}
