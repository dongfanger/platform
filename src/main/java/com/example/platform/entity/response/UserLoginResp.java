package com.example.platform.entity.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserLoginResp {
    private String token;
    private UserResp user;
}
