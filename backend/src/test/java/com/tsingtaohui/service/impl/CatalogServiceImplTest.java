package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.mapper.InventoryMapper;
import com.tsingtaohui.mapper.ProductCategoryMapper;
import com.tsingtaohui.mapper.ProductMapper;
import com.tsingtaohui.model.entity.InventoryEntity;
import com.tsingtaohui.model.entity.ProductEntity;
import com.tsingtaohui.model.vo.ProductDetailVO;
import com.tsingtaohui.model.vo.ProductListVO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceImplTest {

    @Mock
    private ProductCategoryMapper categoryMapper;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private InventoryMapper inventoryMapper;

    @InjectMocks
    private CatalogServiceImpl catalogService;

    @Test
    void getProductsShouldExposeAvailableInventory() {
        ProductEntity product = product();
        Page<ProductEntity> page = new Page<>(1, 10);
        page.setRecords(List.of(product));
        page.setTotal(1);

        InventoryEntity inventory = inventory(7);
        when(productMapper.selectPage(any(Page.class), ArgumentMatchers.<Wrapper<ProductEntity>>any()))
                .thenReturn(page);
        when(inventoryMapper.selectList(ArgumentMatchers.<Wrapper<InventoryEntity>>any()))
                .thenReturn(List.of(inventory));

        ProductListVO item = catalogService.getProducts(null, null, 1, 10).getItems().get(0);

        assertThat(item.getAvailableQty()).isEqualTo(7);
    }

    @Test
    void getProductDetailShouldExposeAvailableInventory() {
        ProductEntity product = product();
        when(productMapper.selectById(1L)).thenReturn(product);
        when(inventoryMapper.selectList(ArgumentMatchers.<Wrapper<InventoryEntity>>any()))
                .thenReturn(List.of(inventory(5), inventory(2)));

        ProductDetailVO detail = catalogService.getProductDetail(1L);

        assertThat(detail.getAvailableQty()).isEqualTo(7);
    }

    private ProductEntity product() {
        ProductEntity product = new ProductEntity();
        product.setId(1L);
        product.setSkuCode("SKU-001");
        product.setNameZh("饮用水");
        product.setNameEn("Water");
        product.setPrice(new BigDecimal("12.50"));
        product.setWeightKg(new BigDecimal("1.000"));
        product.setVolumeM3(new BigDecimal("0.0020"));
        product.setDroneDeliverable(1);
        product.setStatus("ON_SALE");
        return product;
    }

    private InventoryEntity inventory(int availableQty) {
        InventoryEntity inventory = new InventoryEntity();
        inventory.setSkuCode("SKU-001");
        inventory.setAvailableQty(availableQty);
        return inventory;
    }
}
