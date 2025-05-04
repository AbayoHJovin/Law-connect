package com.legal.lawconnect.controller;

import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.requests.ResetPasswordRequest;
import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.password.IPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/password")
public class PasswordResetController {
    private final IPasswordService passwordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestBody Map<String, String> payload) {
        try {
            String email = payload.get("email");
            passwordService.initiateReset(email);
            return ResponseEntity.ok(new ApiResponse("Password reset link sent", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            passwordService.resetPassword(request);
            return ResponseEntity.ok(new ApiResponse("Password reset successful", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Something went wrong", null));
        }
    }


}
