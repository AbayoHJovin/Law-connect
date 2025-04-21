package com.legal.lawconnect.util;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;


@Component
public class JwtUtil {
    private final String secret = "mySecretKey"; // You should store this securely (not in code)
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // Generate token with username
    public String generateToken(String username) {
        // 15 minutes
        long expirationMs = 1000 * 60 * 15;
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey() , Jwts.SIG.HS256)
                .compact();
    }

    // Extract username
    public String getUserFromToken(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
