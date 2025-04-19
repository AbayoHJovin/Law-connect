package com.legal.lawconnect.requests;
import lombok.Data;

@Data
public class PhoneLoginRequest {
    private String phoneNumber;
    private String password;
}
