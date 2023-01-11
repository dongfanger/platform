package com.example.platform.service.impl;

import com.example.platform.common.utils.JwtTokenUtil;
import com.example.platform.entity.request.PasswordReq;
import com.example.platform.entity.response.UserResp;
import com.example.platform.mbg.mapper.UserMapper;
import com.example.platform.mbg.model.User;
import com.example.platform.mbg.model.UserExample;
import com.example.platform.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * UserService实现类
 * Created by macro on 2019/4/19.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private UserMapper userMapper;
    @Resource
    private JwtTokenUtil jwtTokenUtil;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserDetailsService userDetailsService;
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Override
    public List<User> listAllUser() {
        return userMapper.selectByExample(new UserExample());
    }

    @Override
    public User updateUser(Long id, User userReq) {
        User user = userMapper.selectByPrimaryKey(id);
        user.setUsername(userReq.getUsername());
        user.setNickname(userReq.getNickname());
        user.setPassword(passwordEncoder.encode(userReq.getPassword()));
        int count = userMapper.updateByPrimaryKeySelective(user);
        return count == 1 ? user : null;
    }

    @Override
    public int deleteUser(Long id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<UserResp> listUser(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> userList;
        if (keyword.length() > 0) {
            UserExample example = new UserExample();
            UserExample.Criteria cUsername = example.createCriteria().andUsernameEqualTo(keyword);
            example.or(cUsername);
            userList = userMapper.selectByExample(example);
        } else {
            userList = userMapper.selectByExample(new UserExample());
        }
        PageInfo<UserResp> pageInfo = new PageInfo<>();
        BeanUtils.copyProperties(new PageInfo<>(userList), pageInfo);
        List<UserResp> userRespList = new ArrayList<>();
        for (User user : userList) {
            UserResp userResp = new UserResp();
            BeanUtils.copyProperties(user, userResp);
            userRespList.add(userResp);
        }
        pageInfo.setList(userRespList);
        return pageInfo;
    }

    @Override
    public UserResp getUser(Long id) {
        UserResp userResp = new UserResp();
        BeanUtils.copyProperties(userMapper.selectByPrimaryKey(id), userResp);
        return userResp;
    }

    @Override
    public User getUserByUsername(String username) {
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<User> userList = userMapper.selectByExample(example);
        if (userList != null && userList.size() > 0) {
            return userList.get(0);
        }
        return null;
    }

    @Override
    public UserResp getLoginUser(String username) {
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(username);
        List<User> userList = userMapper.selectByExample(example);
        if (userList != null && userList.size() > 0) {
            UserResp userResp = new UserResp();
            BeanUtils.copyProperties(userList.get(0), userResp);
            return userResp;
        }
        return null;
    }

    @Override
    public UserResp register(User user) {
        //查询是否有相同用户名的用户
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(user.getUsername());
        List<User> userList = userMapper.selectByExample(example);
        if (userList.size() > 0) {
            return null;
        }
        //将密码进行加密操作
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        userMapper.insert(user);
        UserResp userResp = new UserResp();
        BeanUtils.copyProperties(user, userResp);
        return userResp;
    }


    @Override
    public String login(String username, String password) {
        String token = null;
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new BadCredentialsException("密码不正确");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录异常:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public Boolean updatePassword(HttpHeaders headers, PasswordReq passwordReq) {
        String jwt = Objects.requireNonNull(headers.get("Authorization")).get(0);
        String token = jwt.substring(this.tokenHead.length());
        String username = jwtTokenUtil.getUserNameFromToken(token);
        UserExample example = new UserExample();
        example.createCriteria().andUsernameEqualTo(username);
        User user = userMapper.selectByExample(example).get(0);
        if (!passwordEncoder.matches(passwordReq.getOldPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(passwordReq.getNewPassword()));
        userMapper.updateByPrimaryKey(user);
        return true;
    }

    @Override
    public String resetPassword(Long id) {
        String password = "qa123456";
        User user = userMapper.selectByPrimaryKey(id);
        String encodePassword = passwordEncoder.encode(password);
        user.setPassword(encodePassword);
        userMapper.updateByPrimaryKey(user);
        return password;
    }
}
