package com.legal.lawconnect.services.mail;

public interface IMailService {
    void sendVerificationEmail(String email);
    boolean verifyEmail(String email, String otp);
    boolean isEmailVerified(String email);
    void sendPasswordResetEmail(String toEmail, String resetLink);
}
