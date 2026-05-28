package com.tsingtaohui.controller;

import com.tsingtaohui.common.context.UserContextHolder;
import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.dto.UpdateStatusDTO;
import com.tsingtaohui.model.vo.AdminInventoryVO;
import com.tsingtaohui.model.vo.AdminProductVO;
import com.tsingtaohui.model.vo.AdminProfileVO;
import com.tsingtaohui.model.vo.AdminUserVO;
import com.tsingtaohui.service.IAdminService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
public class AdminController {

    private final IAdminService adminService;

    public AdminController(IAdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/profile")
    public ApiResponse<AdminProfileVO> getProfile() {
        return ApiResponse.ok(adminService.getProfile(UserContextHolder.getUserId()));
    }

    @GetMapping("/users")
    public ApiResponse<PageResult<AdminUserVO>> getUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "user_type", required = false) String userType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(adminService.getUsers(keyword, userType, status, page, pageSize));
    }

    @PatchMapping("/users/{userId}/status")
    public ApiResponse<AdminUserVO> updateUserStatus(@PathVariable Long userId,
                                                     @Valid @RequestBody UpdateStatusDTO dto) {
        return ApiResponse.ok(adminService.updateUserStatus(userId, dto.getStatus()));
    }

    @GetMapping("/products")
    public ApiResponse<PageResult<AdminProductVO>> getProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(adminService.getProducts(keyword, categoryId, status, page, pageSize));
    }

    @PatchMapping("/products/{productId}/status")
    public ApiResponse<AdminProductVO> updateProductStatus(@PathVariable Long productId,
                                                           @Valid @RequestBody UpdateStatusDTO dto) {
        return ApiResponse.ok(adminService.updateProductStatus(productId, dto.getStatus()));
    }

    @GetMapping("/inventory")
    public ApiResponse<PageResult<AdminInventoryVO>> getInventory(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "warehouse_id", required = false) Long warehouseId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(adminService.getInventory(keyword, warehouseId, page, pageSize));
    }
}
