package com.legal.lawconnect.requests;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    String token;
    String password;
    String confirmPassword;
}
