package com.legal.lawconnect.requests;

import lombok.Data;

@Data
public class EmailLoginRequest {
    private String email;
    private String password;
}
