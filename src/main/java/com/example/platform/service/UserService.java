package com.example.platform.service;


import com.example.platform.entity.request.PasswordReq;
import com.example.platform.entity.response.UserLoginResp;
import com.example.platform.entity.response.UserResp;
import com.example.platform.mbg.model.User;
import com.github.pagehelper.PageInfo;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.List;

/**
 * UserService
 * Created by macro on 2019/4/19.
 */
public interface UserService {
    List<User> listAllUser();

    User updateUser(Long id, User user);

    int deleteUser(Long id);

    PageInfo<UserResp> listUser(String keyword, int pageNum, int pageSize);

    UserResp getUser(Long id);

    User getUserByUsername(String username);

    UserResp getLoginUser(String username);

    UserResp register(User user);

    String login(String username, String password);

    Boolean updatePassword(HttpHeaders headers, PasswordReq passwordReq);

    String resetPassword(Long id);
}
