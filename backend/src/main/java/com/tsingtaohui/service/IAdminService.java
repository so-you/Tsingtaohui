package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.dto.UpdateProductDTO;
import com.tsingtaohui.model.vo.AdminInventoryVO;
import com.tsingtaohui.model.vo.AdminProductVO;
import com.tsingtaohui.model.vo.AdminProfileVO;
import com.tsingtaohui.model.vo.AdminUserVO;

public interface IAdminService {

    AdminProfileVO getProfile(Long userId);

    PageResult<AdminUserVO> getUsers(String keyword, String userType, String status, int page, int pageSize);

    AdminUserVO updateUserStatus(Long userId, String status);

    PageResult<AdminProductVO> getProducts(String keyword, Long categoryId, String status, int page, int pageSize);

    AdminProductVO updateProduct(Long productId, UpdateProductDTO dto);

    AdminProductVO updateProductStatus(Long productId, String status);

    PageResult<AdminInventoryVO> getInventory(String keyword, Long warehouseId, int page, int pageSize);
}
