package com.legal.lawconnect.services.password;

import com.legal.lawconnect.requests.ResetPasswordRequest;

public interface IPasswordService {
    void initiateReset(String email);
    void resetPassword(ResetPasswordRequest request);
}
