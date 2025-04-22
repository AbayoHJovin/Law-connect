package com.legal.lawconnect.util;


import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.*;

import javax.crypto.SecretKey;


@Component
public class JwtUtil {
    private final String secret = "92c9ecbb7ce30f51b15b64943ebac510e7c59cdadd326965b401f30213433377ab3c38a467d65befb544939d0e1917891abea061a90"; // You should store this securely (not in code)
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    // Generate token with username
    public String generateToken(String email) {
        // 15 minutes
        long expirationMs = 1000 * 60 * 15;
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSignKey() , Jwts.SIG.HS256)
                .compact();
    }

    // Extract username
    public String getUserFromToken(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload().getSubject();
    }
    public Long extractUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return Long.parseLong(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
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
