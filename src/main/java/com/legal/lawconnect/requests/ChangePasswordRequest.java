package com.legal.lawconnect.requests;

import lombok.Data;

import java.util.UUID;

@Data
public class ChangePasswordRequest {
    String oldPassword;
    String newPassword;
    UUID ownerId;
}
