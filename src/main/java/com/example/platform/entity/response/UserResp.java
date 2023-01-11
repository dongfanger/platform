package com.example.platform.entity.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserResp {
    private Long id;
    private String username;
    private String nickname;
    private Byte isSuperuser;
    private Byte isDeleted;
}
