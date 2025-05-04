package com.legal.lawconnect.controller;

import com.legal.lawconnect.exceptions.UnauthorizedActionException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.RefreshToken;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.auth.AuthService;
import com.legal.lawconnect.services.citizen.ICitizenService;
import com.legal.lawconnect.util.JwtUtil;
import com.legal.lawconnect.validation.AuthValidation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/auth")
public class AuthController {

    private final AuthService authService;
    private final ICitizenService citizenService;
    private final JwtUtil jwtUtil;
    private final AuthValidation authValidation;

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        try {
            String refreshToken = authValidation.extractRefreshTokenFromCookies(request);
            if (refreshToken == null) {
                return authValidation.unauthorized("Refresh token not found in cookies");
            }

            RefreshToken validToken = authService.verifyRefreshToken(refreshToken)
                    .orElseThrow(() -> new UnauthorizedActionException("Refresh token expired! Please login again."));

            String userEmail = validToken.getCitizen() != null
                    ? validToken.getCitizen().getEmail()
                    : validToken.getLawyer().getEmail();


            String newAccessToken = jwtUtil.generateToken(userEmail);

            authValidation.setAccessTokenCookie(response, newAccessToken);

            return ResponseEntity.ok(new ApiResponse("New access token generated", newAccessToken));
        } catch (UnauthorizedActionException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("An error occurred while refreshing the access token: " + e.getMessage(), null));
        }
    }

    @GetMapping("/protected")
    public ResponseEntity<ApiResponse> protectedRoute(HttpServletRequest request) {
        try {
            String token = null;

            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("access_token".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }

            if (token == null || !jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(new ApiResponse("Access token is invalid or missing", null));
            }

            String subject = jwtUtil.getUserFromToken(token); // could be email or user ID
            return ResponseEntity.ok(new ApiResponse("Access granted to protected route", subject));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("Error verifying access token: " + e.getMessage(), null));
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse> validateAccessToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body(new ApiResponse("Authorization header is missing or invalid", null));
            }

            String token = authHeader.substring(7);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(new ApiResponse("Access token is invalid or expired", null));
            }

            String subject = jwtUtil.getUserFromToken(token);
            return ResponseEntity.ok(new ApiResponse("Token is valid", subject));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse("Error while validating token: " + e.getMessage(), null));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Get refresh token from cookies
            String refreshToken = null;
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refresh_token".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            if (refreshToken != null) {
                authService.verifyRefreshToken(refreshToken).ifPresent(token -> {
                    if (token.getCitizen() != null) {
                        authService.revokeRefreshTokenCitizen(token.getCitizen());
                    } else if (token.getLawyer() != null) {
                        authService.revokeRefreshTokenLawyer(token.getLawyer());
                    }
                });
            }

            // Invalidate both cookies on client
            Cookie accessCookie = new Cookie("access_token", null);
            accessCookie.setHttpOnly(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(0); // delete

            Cookie refreshCookie = new Cookie("refresh_token", null);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(0); // delete

            response.addCookie(accessCookie);
            response.addCookie(refreshCookie);

            return ResponseEntity.ok(new ApiResponse("Logged out successfully", null));

        } catch (Exception e) {
            // Catch any unexpected exceptions
            return ResponseEntity.status(500).body(new ApiResponse("An error occurred while logging out: " + e.getMessage(), null));
        }
    }
}
