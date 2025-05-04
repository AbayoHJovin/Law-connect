package com.legal.lawconnect.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VerificationEmailUtil {
private final JavaMailSender javaMailSender;

    public void sendHtmlEmail(String to, String otp) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String htmlContent = """
            <!DOCTYPE html>
            <html lang="en">
              <head>
                <meta charset="UTF-8" />
                <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                <title>Email Verification</title>
                <script>
                  function copyOTP() {
                    const otpText = document.getElementById('otp').innerText;
                    navigator.clipboard.writeText(otpText).then(() => {
                      alert('OTP copied to clipboard!');
                    });
                  }
                </script>
              </head>
              <body style="background-color: #f3f4f6; font-family: sans-serif; color: #1f2937;">
                <div style="max-width: 600px; margin: 2rem auto; padding: 1.5rem; background-color: #ffffff; border-radius: 1rem; box-shadow: 0 2px 6px rgba(0,0,0,0.1);">
                  <div style="background-color: #1e3a8a; color: white; text-align: center; padding: 1rem; border-top-left-radius: 1rem; border-top-right-radius: 1rem;">
                    <h1 style="font-size: 1.5rem;">LawConnect Email Verification</h1>
                  </div>

                  <div style="padding: 1.5rem;">
                    <p>Hello,</p>
                    <p>Please use the OTP code below to verify your email address:</p>

                    <div style="display: flex; align-items: center; gap: 0.5rem; margin: 1.5rem 0;">
                      <div id="otp" style="background-color: #e5e7eb; color: #1e3a8a; font-weight: bold; font-size: 1.25rem; letter-spacing: 0.1em; padding: 0.75rem 1.5rem; border-radius: 0.5rem;">
                        """ + otp + """
                      </div>
                      <div style="padding: 0.5rem; background-color: #f3f4f6; border: 1px solid #d1d5db; border-radius: 9999px;" title="Copy OTP">
                        📋
                      </div>
                    </div>

                    <p style="font-size: 0.875rem; color: #6b7280;">
                      This OTP is valid for 10 minutes. Please don’t share it with anyone.
                    </p>

                    <p style="margin-top: 1.5rem;">Thank you for using <strong>LawConnect</strong>.</p>
                  </div>

                  <div style="text-align: center; font-size: 0.75rem; color: #9ca3af; border-top: 1px solid #e5e7eb; padding-top: 1rem;">
                    &copy; 2025 LawConnect. All rights reserved.
                  </div>
                </div>
              </body>
            </html>
        """;

        helper.setTo(to);
        helper.setSubject("LawConnect Email Verification");
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

}
