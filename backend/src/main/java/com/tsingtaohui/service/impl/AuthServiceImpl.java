package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.util.JwtUtil;
import com.tsingtaohui.mapper.UserMapper;
import com.tsingtaohui.model.dto.LoginDTO;
import com.tsingtaohui.model.dto.RegisterDTO;
import com.tsingtaohui.model.entity.UserEntity;
import com.tsingtaohui.model.vo.AuthVO;
import com.tsingtaohui.model.vo.UserVO;
import com.tsingtaohui.service.IAuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements IAuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthVO register(RegisterDTO dto) {
        // Check username uniqueness
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_USERNAME_EXISTS.getCode(),
                    ErrorCode.USER_USERNAME_EXISTS.name());
        }

        // Create user
        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setUserType("CUSTOMER");
        user.setStatus("ENABLED");
        user.setPreferredLanguage(dto.getPreferredLanguage());
        userMapper.insert(user);

        // Generate token
        String token = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getUserType());
        UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getUserType(), user.getPreferredLanguage());

        return new AuthVO(token, userVO);
    }

    @Override
    public AuthVO login(LoginDTO dto) {
        UserEntity user = userMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, dto.getUsername())
        );
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_CREDENTIALS.getCode(),
                    ErrorCode.AUTH_INVALID_CREDENTIALS.getMessageZh());
        }

        if (!"ENABLED".equals(user.getStatus())) {
            throw new BusinessException(ErrorCode.AUTH_SESSION_EXPIRED.getCode(),
                    "Account is " + user.getStatus().toLowerCase());
        }

        // Update last login
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        String token = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getUserType());
        UserVO userVO = new UserVO(user.getId(), user.getUsername(), user.getUserType(), user.getPreferredLanguage());

        return new AuthVO(token, userVO);
    }
}
