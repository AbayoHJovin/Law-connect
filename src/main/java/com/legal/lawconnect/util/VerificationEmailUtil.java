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
        <style>
          @import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap');
          .copy-btn {
            cursor: pointer;
            transition: all 0.2s ease;
          }
          .copy-btn:hover {
            background-color: #1e3a8a !important;
            color: white !important;
          }
          .copy-btn.copied {
            background-color: #10b981 !important;
            color: white !important;
          }
        </style>
      </head>
      <body style="background-color: #f3f4f6; font-family: 'Inter', sans-serif; color: #1f2937; margin: 0; padding: 0;">
        <div style="max-width: 600px; margin: 2rem auto; padding: 0; background-color: #ffffff; border-radius: 1rem; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden;">
          <div style="background-color: #1e3a8a; color: white; text-align: center; padding: 1.5rem; border-bottom: 4px solid #3b82f6;">
            <h1 style="font-size: 1.5rem; font-weight: 600; margin: 0;">LawConnect</h1>
            <p style="font-size: 1rem; font-weight: 400; margin: 0.5rem 0 0; opacity: 0.9;">Email Verification</p>
          </div>

          <div style="padding: 2rem;">
            <p style="margin: 0 0 1.5rem; line-height: 1.5;">Hello,</p>
            <p style="margin: 0 0 1.5rem; line-height: 1.5;">Please use the OTP code below to verify your email address:</p>

            <div style="display: flex; align-items: center; gap: 0.75rem; margin: 2rem 0;">
              <div id="otp" style="background-color: #f8fafc; color: #1e3a8a; font-weight: 600; font-size: 1.5rem; letter-spacing: 0.1em; padding: 1rem 2rem; border-radius: 0.5rem; border: 1px solid #e2e8f0; flex-grow: 1; text-align: center;">
                """ + otp + """
              </div>
              <button id="copyBtn" onclick="copyOTP()" class="copy-btn" style="padding: 1rem; background-color: #f1f5f9; border: 1px solid #e2e8f0; border-radius: 0.5rem; font-size: 1.25rem; display: flex; align-items: center; justify-content: center;">
                📋
              </button>
            </div>

            <p style="margin: 1.5rem 0 0; font-size: 0.875rem; color: #64748b; line-height: 1.5;">
              This OTP is valid for 10 minutes. Please don't share it with anyone.
            </p>

            <p style="margin: 2rem 0 0; line-height: 1.5;">Thank you for using <strong style="color: #1e3a8a;">LawConnect</strong>.</p>
          </div>

          <div style="text-align: center; font-size: 0.75rem; color: #94a3b8; border-top: 1px solid #f1f5f9; padding: 1.5rem; background-color: #f8fafc;">
            <p style="margin: 0;">&copy; 2025 LawConnect. All rights reserved.</p>
            <p style="margin: 0.5rem 0 0;">Need help? Contact us at support@lawconnect.com</p>
          </div>
        </div>

        <script>
          function copyOTP() {
            const otpText = document.getElementById('otp').innerText;
            const copyBtn = document.getElementById('copyBtn');
            
            navigator.clipboard.writeText(otpText).then(() => {
              copyBtn.innerHTML = '✓';
              copyBtn.classList.add('copied');
              
              setTimeout(() => {
                copyBtn.innerHTML = '📋';
                copyBtn.classList.remove('copied');
              }, 2000);
            }).catch(err => {
              console.error('Failed to copy OTP: ', err);
              copyBtn.innerHTML = 'Failed';
              setTimeout(() => {
                copyBtn.innerHTML = '📋';
              }, 2000);
            });
          }
        </script>
      </body>
    </html>
""";

        helper.setTo(to);
        helper.setSubject("LawConnect Email Verification");
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

}
