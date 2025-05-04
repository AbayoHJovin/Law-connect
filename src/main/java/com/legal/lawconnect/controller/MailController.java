package com.legal.lawconnect.controller;

import com.legal.lawconnect.response.ApiResponse;
import com.legal.lawconnect.services.mail.IMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/mail")
public class MailController {
    private final IMailService mailService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendOtp(@RequestBody Map<String, String> body) {
        try{
            mailService.sendVerificationEmail(body.get("email"));
            return ResponseEntity.ok(new ApiResponse("Email sent.", null));
        }catch(Exception e){
            return ResponseEntity.ok(new ApiResponse(e.getMessage(), null));
        }

    }

    @PostMapping("/confirm")
    public ResponseEntity<ApiResponse> verifyOtp(@RequestBody Map<String, String> body) {
        try {
            boolean success = mailService.verifyEmail(body.get("email"), body.get("otp"));
            return ResponseEntity.ok(new ApiResponse(success ? "Email verified." : "Invalid OTP.", null));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(e.getMessage(), null));
        }
    }
}
