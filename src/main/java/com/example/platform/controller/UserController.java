package com.example.platform.controller;

import com.example.platform.common.api.CommonPage;
import com.example.platform.common.api.CommonResult;
import com.example.platform.entity.request.PasswordReq;
import com.example.platform.entity.response.UserResp;
import com.example.platform.mbg.model.User;
import com.example.platform.service.UserService;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * UserController
 * Created by macro on 2019/4/19.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @RequestMapping(value = "listAll", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUserList() {
        return new ResponseEntity<>(userService.listAllUser(), HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<?> createUser(@RequestBody User user) {
        UserResp userResp = userService.register(user);
        if (userResp == null) {
            return new ResponseEntity<>(CommonResult.failed("用户已存在"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(userResp, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody User user, BindingResult result) {
        User updatedUser = userService.updateUser(id, user);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(CommonResult.failed("操作失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        int count = userService.deleteUser(id);
        if (count == 1) {
            return new ResponseEntity<>(CommonResult.success(null), HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(CommonResult.failed("操作失败"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/checkAuth", method = RequestMethod.GET)
    @PreAuthorize("hasAuthority('console:user:auth')")
    public ResponseEntity<?> auth() {
        return new ResponseEntity<>(CommonResult.success("true"), HttpStatus.OK);
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ResponseEntity<?> listUser(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                      @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "perPage", defaultValue = "10") Integer pageSize) {
        PageInfo<UserResp> userResultList = userService.listUser(keyword, pageNum, pageSize);

        return new ResponseEntity<>(CommonPage.restPage(userResultList), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> user(@PathVariable("id") Long id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/update-password", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePassword(@RequestHeader HttpHeaders headers,
                                            @RequestBody PasswordReq passwordReq) {
        Boolean isOldPasswordRight = userService.updatePassword(headers, passwordReq);
        if (!isOldPasswordRight) {
            return new ResponseEntity<>(CommonResult.validateFailed("旧密码错误"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>("", HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}/reset-password", method = RequestMethod.PUT)
    public ResponseEntity<?> resetPassword(@PathVariable("id") Long id) {
        String password = userService.resetPassword(id);
        return new ResponseEntity<>(CommonResult.success(password), HttpStatus.OK);
    }
}
