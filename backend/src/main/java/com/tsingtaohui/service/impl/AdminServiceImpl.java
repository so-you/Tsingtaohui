package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.InventoryMapper;
import com.tsingtaohui.mapper.ProductMapper;
import com.tsingtaohui.mapper.UserMapper;
import com.tsingtaohui.mapper.UserProfileMapper;
import com.tsingtaohui.mapper.UserShipMapper;
import com.tsingtaohui.model.entity.InventoryEntity;
import com.tsingtaohui.model.entity.ProductEntity;
import com.tsingtaohui.model.entity.UserEntity;
import com.tsingtaohui.model.entity.UserProfileEntity;
import com.tsingtaohui.model.entity.UserShipEntity;
import com.tsingtaohui.model.vo.AdminInventoryVO;
import com.tsingtaohui.model.vo.AdminProductVO;
import com.tsingtaohui.model.vo.AdminProfileVO;
import com.tsingtaohui.model.vo.AdminUserVO;
import com.tsingtaohui.service.IAdminService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AdminServiceImpl implements IAdminService {

    private static final Set<String> USER_STATUSES = Set.of("ENABLED", "DISABLED", "LOCKED");
    private static final Set<String> PRODUCT_STATUSES = Set.of("ON_SALE", "OFF_SALE");

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final UserShipMapper userShipMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;

    public AdminServiceImpl(UserMapper userMapper,
                            UserProfileMapper userProfileMapper,
                            UserShipMapper userShipMapper,
                            ProductMapper productMapper,
                            InventoryMapper inventoryMapper) {
        this.userMapper = userMapper;
        this.userProfileMapper = userProfileMapper;
        this.userShipMapper = userShipMapper;
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public AdminProfileVO getProfile(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "User not found");
        }
        UserProfileEntity profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfileEntity>().eq(UserProfileEntity::getUserId, userId)
        );
        String nickname = profile != null && StringUtils.hasText(profile.getDisplayName())
                ? profile.getDisplayName()
                : user.getUsername();
        return new AdminProfileVO(user.getId(), user.getUsername(), nickname, user.getUserType(),
                user.getPreferredLanguage());
    }

    @Override
    public PageResult<AdminUserVO> getUsers(String keyword, String userType, String status, int page, int pageSize) {
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(UserEntity::getUsername, keyword.trim());
        }
        if (StringUtils.hasText(userType)) {
            wrapper.eq(UserEntity::getUserType, userType.trim());
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(UserEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(UserEntity::getCreatedAt);

        Page<UserEntity> pageResult = userMapper.selectPage(new Page<>(safePage(page), safePageSize(pageSize)), wrapper);
        List<UserEntity> users = pageResult.getRecords();
        Map<Long, UserProfileEntity> profiles = loadProfiles(users);
        Map<Long, UserShipEntity> ships = loadDefaultShips(users);

        List<AdminUserVO> items = new ArrayList<>();
        for (UserEntity user : users) {
            items.add(toAdminUserVO(user, profiles.get(user.getId()), ships.get(user.getId())));
        }
        return new PageResult<>(items, safePage(page), safePageSize(pageSize), pageResult.getTotal());
    }

    @Override
    public AdminUserVO updateUserStatus(Long userId, String status) {
        if (!USER_STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Invalid user status");
        }
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "User not found");
        }
        user.setStatus(status);
        userMapper.updateById(user);
        UserProfileEntity profile = userProfileMapper.selectOne(
                new LambdaQueryWrapper<UserProfileEntity>().eq(UserProfileEntity::getUserId, userId)
        );
        UserShipEntity ship = userShipMapper.selectOne(
                new LambdaQueryWrapper<UserShipEntity>()
                        .eq(UserShipEntity::getUserId, userId)
                        .eq(UserShipEntity::getIsDefault, 1)
                        .last("limit 1")
        );
        return toAdminUserVO(user, profile, ship);
    }

    @Override
    public PageResult<AdminProductVO> getProducts(String keyword, Long categoryId, String status, int page, int pageSize) {
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(w -> w
                    .like(ProductEntity::getNameZh, value)
                    .or().like(ProductEntity::getNameEn, value)
                    .or().like(ProductEntity::getSkuCode, value)
            );
        }
        if (categoryId != null) {
            wrapper.eq(ProductEntity::getCategoryId, categoryId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(ProductEntity::getStatus, status.trim());
        }
        wrapper.orderByDesc(ProductEntity::getCreatedAt);

        Page<ProductEntity> pageResult = productMapper.selectPage(
                new Page<>(safePage(page), safePageSize(pageSize)), wrapper
        );
        Map<String, InventorySummary> inventory = loadInventorySummary(pageResult.getRecords());
        List<AdminProductVO> items = new ArrayList<>();
        for (ProductEntity product : pageResult.getRecords()) {
            items.add(toAdminProductVO(product, inventory.get(product.getSkuCode())));
        }
        return new PageResult<>(items, safePage(page), safePageSize(pageSize), pageResult.getTotal());
    }

    @Override
    public AdminProductVO updateProductStatus(Long productId, String status) {
        if (!PRODUCT_STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Invalid product status");
        }
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Product not found");
        }
        product.setStatus(status);
        productMapper.updateById(product);
        Map<String, InventorySummary> inventory = loadInventorySummary(List.of(product));
        return toAdminProductVO(product, inventory.get(product.getSkuCode()));
    }

    @Override
    public PageResult<AdminInventoryVO> getInventory(String keyword, Long warehouseId, int page, int pageSize) {
        LambdaQueryWrapper<InventoryEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(InventoryEntity::getSkuCode, keyword.trim());
        }
        if (warehouseId != null) {
            wrapper.eq(InventoryEntity::getWarehouseId, warehouseId);
        }
        wrapper.orderByDesc(InventoryEntity::getUpdatedAt);

        Page<InventoryEntity> pageResult = inventoryMapper.selectPage(
                new Page<>(safePage(page), safePageSize(pageSize)), wrapper
        );
        Map<String, ProductEntity> products = loadProductsBySku(pageResult.getRecords());
        List<AdminInventoryVO> items = new ArrayList<>();
        for (InventoryEntity inventory : pageResult.getRecords()) {
            items.add(toAdminInventoryVO(inventory, products.get(inventory.getSkuCode())));
        }
        return new PageResult<>(items, safePage(page), safePageSize(pageSize), pageResult.getTotal());
    }

    private Map<Long, UserProfileEntity> loadProfiles(List<UserEntity> users) {
        Map<Long, UserProfileEntity> profiles = new HashMap<>();
        if (users.isEmpty()) return profiles;
        List<Long> userIds = users.stream().map(UserEntity::getId).toList();
        List<UserProfileEntity> entities = userProfileMapper.selectList(
                new LambdaQueryWrapper<UserProfileEntity>().in(UserProfileEntity::getUserId, userIds)
        );
        for (UserProfileEntity entity : entities) {
            profiles.put(entity.getUserId(), entity);
        }
        return profiles;
    }

    private Map<Long, UserShipEntity> loadDefaultShips(List<UserEntity> users) {
        Map<Long, UserShipEntity> ships = new HashMap<>();
        if (users.isEmpty()) return ships;
        List<Long> userIds = users.stream().map(UserEntity::getId).toList();
        List<UserShipEntity> entities = userShipMapper.selectList(
                new LambdaQueryWrapper<UserShipEntity>()
                        .in(UserShipEntity::getUserId, userIds)
                        .eq(UserShipEntity::getIsDefault, 1)
        );
        for (UserShipEntity entity : entities) {
            ships.putIfAbsent(entity.getUserId(), entity);
        }
        return ships;
    }

    private Map<String, InventorySummary> loadInventorySummary(List<ProductEntity> products) {
        Map<String, InventorySummary> result = new HashMap<>();
        if (products.isEmpty()) return result;
        Set<String> skuCodes = new HashSet<>();
        for (ProductEntity product : products) {
            skuCodes.add(product.getSkuCode());
        }
        List<InventoryEntity> inventoryList = inventoryMapper.selectList(
                new LambdaQueryWrapper<InventoryEntity>().in(InventoryEntity::getSkuCode, skuCodes)
        );
        for (InventoryEntity inventory : inventoryList) {
            InventorySummary summary = result.computeIfAbsent(inventory.getSkuCode(), key -> new InventorySummary());
            summary.availableQty += nullToZero(inventory.getAvailableQty());
            summary.lockedQty += nullToZero(inventory.getLockedQty());
            summary.outboundQty += nullToZero(inventory.getOutboundQty());
        }
        return result;
    }

    private Map<String, ProductEntity> loadProductsBySku(List<InventoryEntity> inventoryList) {
        Map<String, ProductEntity> result = new HashMap<>();
        if (inventoryList.isEmpty()) return result;
        Set<String> skuCodes = new HashSet<>();
        for (InventoryEntity inventory : inventoryList) {
            skuCodes.add(inventory.getSkuCode());
        }
        List<ProductEntity> products = productMapper.selectList(
                new LambdaQueryWrapper<ProductEntity>().in(ProductEntity::getSkuCode, skuCodes)
        );
        for (ProductEntity product : products) {
            result.put(product.getSkuCode(), product);
        }
        return result;
    }

    private AdminUserVO toAdminUserVO(UserEntity user, UserProfileEntity profile, UserShipEntity ship) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setUserType(user.getUserType());
        vo.setStatus(user.getStatus());
        vo.setPreferredLanguage(user.getPreferredLanguage());
        vo.setLastLoginAt(user.getLastLoginAt());
        vo.setCreatedAt(user.getCreatedAt());
        if (profile != null) {
            vo.setDisplayName(profile.getDisplayName());
            vo.setContactPhone(profile.getContactPhone());
            vo.setEmail(profile.getEmail());
            vo.setNationality(profile.getNationality());
        }
        if (ship != null) {
            vo.setShipNo(ship.getShipNo());
            vo.setShipName(ship.getShipName());
            vo.setShipNationality(ship.getShipNationality());
            vo.setImo(ship.getImo());
            vo.setMmsi(ship.getMmsi());
        }
        return vo;
    }

    private AdminProductVO toAdminProductVO(ProductEntity product, InventorySummary inventory) {
        AdminProductVO vo = new AdminProductVO();
        vo.setId(product.getId());
        vo.setSkuCode(product.getSkuCode());
        vo.setCategoryId(product.getCategoryId());
        vo.setNameZh(product.getNameZh());
        vo.setNameEn(product.getNameEn());
        vo.setPrice(product.getPrice() != null ? product.getPrice().toPlainString() : "0");
        vo.setWeightKg(product.getWeightKg() != null ? product.getWeightKg().toPlainString() : null);
        vo.setVolumeM3(product.getVolumeM3() != null ? product.getVolumeM3().toPlainString() : null);
        vo.setSource(product.getSource());
        vo.setDroneDeliverable(product.getDroneDeliverable() != null && product.getDroneDeliverable() == 1);
        vo.setStatus(product.getStatus());
        vo.setCreatedAt(product.getCreatedAt());
        if (inventory != null) {
            vo.setAvailableQty(inventory.availableQty);
            vo.setLockedQty(inventory.lockedQty);
            vo.setOutboundQty(inventory.outboundQty);
        }
        return vo;
    }

    private AdminInventoryVO toAdminInventoryVO(InventoryEntity inventory, ProductEntity product) {
        AdminInventoryVO vo = new AdminInventoryVO();
        vo.setId(inventory.getId());
        vo.setWarehouseId(inventory.getWarehouseId());
        vo.setLocationCode(inventory.getLocationCode());
        vo.setSkuCode(inventory.getSkuCode());
        vo.setBatchNo(inventory.getBatchNo());
        vo.setAvailableQty(nullToZero(inventory.getAvailableQty()));
        vo.setLockedQty(nullToZero(inventory.getLockedQty()));
        vo.setOutboundQty(nullToZero(inventory.getOutboundQty()));
        vo.setUpdatedAt(inventory.getUpdatedAt());
        if (product != null) {
            vo.setProductNameZh(product.getNameZh());
            vo.setProductNameEn(product.getNameEn());
        }
        return vo;
    }

    private int safePage(int page) {
        return Math.max(page, 1);
    }

    private int safePageSize(int pageSize) {
        return Math.min(Math.max(pageSize, 1), 100);
    }

    private int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    private static class InventorySummary {
        private int availableQty;
        private int lockedQty;
        private int outboundQty;
    }
}
