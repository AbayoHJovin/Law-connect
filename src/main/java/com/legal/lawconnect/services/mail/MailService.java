package com.legal.lawconnect.services.mail;

import com.legal.lawconnect.exceptions.ResourceNotFoundException;
import com.legal.lawconnect.functions.ToHtml;
import com.legal.lawconnect.model.EmailVerification;
import com.legal.lawconnect.repository.CitizenRepository;
import com.legal.lawconnect.repository.EmailRepository;
import com.legal.lawconnect.repository.LawyerRepository;
import com.legal.lawconnect.util.VerificationEmailUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class MailService implements IMailService {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    private final EmailRepository emailRepository;
    private final JavaMailSender javaMailSender;
    private final VerificationEmailUtil verificationEmailUtil;
    private final LawyerRepository lawyerRepository;
    private final CitizenRepository citizenRepository;
    private final ToHtml toHtml;

    @Override
    @Transactional
    public void sendVerificationEmail(String email) {
        Optional<EmailVerification> existing = emailRepository.findByEmail(email);
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (lawyerRepository.findByEmail(email) != null || citizenRepository.findByEmail(email) != null) {
            throw new IllegalStateException("Email is already verified");
        }
        if (existing.isPresent()) {
           EmailVerification token = existing.get();
           if(token.isVerified()) throw new IllegalStateException("Email is already verified");
           if(token.getCreatedAt().plusMinutes(10).isAfter(LocalDateTime.now())) {
               throw new IllegalStateException("Verification email has been sent recently");
           }
           emailRepository.deleteByEmail(email);
        }
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        EmailVerification token = new EmailVerification();
        token.setEmail(email);
        token.setOtp(otp);
        token.setCreatedAt(LocalDateTime.now());
        emailRepository.save(token);
        try {
            verificationEmailUtil.sendHtmlEmail(email, otp);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public boolean verifyEmail(String email, String otp) {
      EmailVerification token = emailRepository.findByEmail(email)
              .orElseThrow(()->new ResourceNotFoundException("Email not found"));
        if (lawyerRepository.findByEmail(email) != null || citizenRepository.findByEmail(email) != null) {
            throw new IllegalStateException("Email is already verified");
        }
        if (token.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now())) {
            emailRepository.deleteByEmail(email);
            throw new IllegalArgumentException("Verification token expired");
        }

        if (!token.getOtp().equals(otp)) {
            throw new IllegalArgumentException("Invalid verification code");
        }
        token.setVerified(true);
        emailRepository.save(token);
        return true;
    }

    @Override
    public boolean isEmailVerified(String email) {
        return emailRepository.findByEmail(email)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(toEmail);
            helper.setSubject("Password Reset Request");
            helper.setText(toHtml.getVerificationHtmlCode(resetLink), true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

}
