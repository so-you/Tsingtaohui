package com.tsingtaohui.controller;

import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.model.dto.LoginDTO;
import com.tsingtaohui.model.dto.RegisterDTO;
import com.tsingtaohui.model.vo.AuthVO;
import com.tsingtaohui.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthVO> register(@Valid @RequestBody RegisterDTO dto) {
        return ApiResponse.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ApiResponse<AuthVO> login(@Valid @RequestBody LoginDTO dto) {
        return ApiResponse.ok(authService.login(dto));
    }
}
