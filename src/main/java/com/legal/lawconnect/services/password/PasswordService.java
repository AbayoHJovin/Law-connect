package com.legal.lawconnect.services.password;

import com.legal.lawconnect.exceptions.AlreadyExistsException;
import com.legal.lawconnect.model.Citizen;
import com.legal.lawconnect.model.Lawyer;
import com.legal.lawconnect.model.PasswordResetToken;
import com.legal.lawconnect.repository.CitizenRepository;
import com.legal.lawconnect.repository.LawyerRepository;
import com.legal.lawconnect.repository.PasswordResetRepository;
import com.legal.lawconnect.requests.ResetPasswordRequest;
import com.legal.lawconnect.services.mail.IMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class PasswordService implements IPasswordService {
    private final CitizenRepository citizenRepository;
    private final LawyerRepository lawyerRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final IMailService mailService;
    private final PasswordEncoder passwordEncoder;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    @Override
    public void initiateReset(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        boolean userExists = citizenRepository.findByEmail(email) != null || lawyerRepository.findByEmail(email) != null;
        if (!userExists) {
            throw new IllegalArgumentException("User with this email does not exist.");
        }

        PasswordResetToken existingToken = passwordResetRepository.findByEmail(email);

        if (existingToken != null) {
            if (existingToken.getExpiryDate().isAfter(LocalDateTime.now())) {
                throw new AlreadyExistsException("A reset link has already been sent and is still valid.");
            }
            String newToken = UUID.randomUUID().toString();
            existingToken.setToken(newToken);
            existingToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
            passwordResetRepository.save(existingToken);

            String resetLink = "https://localhost:8080/reset-password?token=" + newToken;
            mailService.sendPasswordResetEmail(email, resetLink);
            return;
        }

        // Create new token if none exists
        String token = UUID.randomUUID().toString();
        PasswordResetToken newResetToken = new PasswordResetToken(token, email, LocalDateTime.now().plusMinutes(15));
        passwordResetRepository.save(newResetToken);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;
        mailService.sendPasswordResetEmail(email, resetLink);
    }


    @Override
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetRepository.findByToken(request.getToken());
        if (resetToken == null) {
            throw new IllegalArgumentException("Invalid or expired token.");
        }

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token has expired.");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // Validate password strength
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        if (!request.getPassword().matches(passwordRegex)) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and include uppercase, lowercase, number, and special character.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String email = resetToken.getEmail();

        Citizen citizen = citizenRepository.findByEmail(email);
        Lawyer lawyer = lawyerRepository.findByEmail(email);
        if (citizen != null) {
            citizen.setPassword(encodedPassword);
            citizenRepository.save(citizen);
        }else if (lawyer != null) {
            lawyer.setPassword(encodedPassword);
            lawyerRepository.save(lawyer);
        }else {
            throw new IllegalArgumentException("User not found.");
        }
        passwordResetRepository.delete(resetToken);
    }

}
