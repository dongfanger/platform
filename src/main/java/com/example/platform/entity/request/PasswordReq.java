package com.example.platform.entity.request;

import lombok.Data;

@Data
public class PasswordReq {
    private String oldPassword;
    private String newPassword;
}
