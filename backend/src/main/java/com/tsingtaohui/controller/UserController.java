package com.tsingtaohui.controller;

import com.tsingtaohui.common.context.UserContextHolder;
import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.model.dto.UpdateProfileDTO;
import com.tsingtaohui.model.dto.UpdateShipDTO;
import com.tsingtaohui.model.vo.ShipVO;
import com.tsingtaohui.model.vo.UserProfileVO;
import com.tsingtaohui.service.IUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileVO> getMyProfile() {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(userService.getMyProfile(userId));
    }

    @PutMapping("/me/profile")
    public ApiResponse<UserProfileVO> updateProfile(@RequestBody UpdateProfileDTO dto) {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(userService.updateProfile(userId, dto));
    }

    @PutMapping("/me/ship")
    public ApiResponse<ShipVO> updateShip(@Valid @RequestBody UpdateShipDTO dto) {
        Long userId = UserContextHolder.getUserId();
        return ApiResponse.ok(userService.updateShip(userId, dto));
    }
}
