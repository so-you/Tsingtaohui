package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.mapper.InventoryMapper;
import com.tsingtaohui.mapper.ProductMapper;
import com.tsingtaohui.mapper.UserMapper;
import com.tsingtaohui.mapper.UserProfileMapper;
import com.tsingtaohui.mapper.UserShipMapper;
import com.tsingtaohui.model.entity.InventoryEntity;
import com.tsingtaohui.model.entity.ProductEntity;
import com.tsingtaohui.model.vo.AdminProductVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserMapper userMapper;
    @Mock
    private UserProfileMapper userProfileMapper;
    @Mock
    private UserShipMapper userShipMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void getProductsShouldSummarizeInventoryBySku() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setSkuCode("SKU-001");
        product.setCategoryId(10L);
        product.setNameZh("饮用水");
        product.setNameEn("Water");
        product.setPrice(new BigDecimal("12.50"));
        product.setWeightKg(new BigDecimal("1.000"));
        product.setVolumeM3(new BigDecimal("0.0020"));
        product.setDroneDeliverable(1);
        product.setStatus("ON_SALE");

        Page<ProductEntity> page = new Page<>(1, 10);
        page.setRecords(List.of(product));
        page.setTotal(1);

        InventoryEntity inventoryA = new InventoryEntity();
        inventoryA.setSkuCode("SKU-001");
        inventoryA.setAvailableQty(10);
        inventoryA.setLockedQty(2);
        inventoryA.setOutboundQty(1);

        InventoryEntity inventoryB = new InventoryEntity();
        inventoryB.setSkuCode("SKU-001");
        inventoryB.setAvailableQty(5);
        inventoryB.setLockedQty(3);
        inventoryB.setOutboundQty(4);

        when(productMapper.selectPage(any(Page.class), ArgumentMatchers.<Wrapper<ProductEntity>>any()))
                .thenReturn(page);
        when(inventoryMapper.selectList(ArgumentMatchers.<Wrapper<InventoryEntity>>any()))
                .thenReturn(List.of(inventoryA, inventoryB));

        AdminProductVO item = adminService.getProducts(null, null, null, 1, 10).getItems().get(0);

        assertThat(item.getAvailableQty()).isEqualTo(15);
        assertThat(item.getLockedQty()).isEqualTo(5);
        assertThat(item.getOutboundQty()).isEqualTo(5);
    }

    @Test
    void updateUserStatusShouldRejectInvalidStatus() {
        assertThatThrownBy(() -> adminService.updateUserStatus(1L, "UNKNOWN"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid user status");
    }

    @Test
    void updateProductStatusShouldRejectInvalidStatus() {
        assertThatThrownBy(() -> adminService.updateProductStatus(1L, "UNKNOWN"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Invalid product status");
    }
}
