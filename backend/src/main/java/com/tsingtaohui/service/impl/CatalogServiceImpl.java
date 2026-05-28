package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.InventoryMapper;
import com.tsingtaohui.mapper.ProductCategoryMapper;
import com.tsingtaohui.mapper.ProductMapper;
import com.tsingtaohui.model.entity.InventoryEntity;
import com.tsingtaohui.model.entity.ProductCategoryEntity;
import com.tsingtaohui.model.entity.ProductEntity;
import com.tsingtaohui.model.vo.CategoryVO;
import com.tsingtaohui.model.vo.ProductDetailVO;
import com.tsingtaohui.model.vo.ProductListVO;
import com.tsingtaohui.service.ICatalogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CatalogServiceImpl implements ICatalogService {

    private final ProductCategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;

    public CatalogServiceImpl(ProductCategoryMapper categoryMapper,
                              ProductMapper productMapper,
                              InventoryMapper inventoryMapper) {
        this.categoryMapper = categoryMapper;
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
    }

    @Override
    public List<CategoryVO> getCategories() {
        List<ProductCategoryEntity> entities = categoryMapper.selectList(
                new LambdaQueryWrapper<ProductCategoryEntity>()
                        .eq(ProductCategoryEntity::getStatus, "ENABLED")
                        .orderByAsc(ProductCategoryEntity::getSortOrder)
        );

        return buildCategoryTree(entities);
    }

    @Override
    public PageResult<ProductListVO> getProducts(Long categoryId, String keyword, int page, int pageSize) {
        LambdaQueryWrapper<ProductEntity> wrapper = new LambdaQueryWrapper<ProductEntity>()
                .eq(ProductEntity::getStatus, "ON_SALE");

        if (categoryId != null) {
            wrapper.eq(ProductEntity::getCategoryId, categoryId);
        }

        if (StringUtils.hasText(keyword)) {
            String likeKeyword = "%" + keyword + "%";
            wrapper.and(w -> w
                    .like(ProductEntity::getNameZh, keyword)
                    .or().like(ProductEntity::getNameEn, keyword)
                    .or().like(ProductEntity::getSkuCode, keyword)
            );
        }

        wrapper.orderByDesc(ProductEntity::getCreatedAt);

        Page<ProductEntity> pageResult = productMapper.selectPage(new Page<>(page, pageSize), wrapper);

        Map<String, Integer> availableQtyMap = loadAvailableQty(pageResult.getRecords());
        List<ProductListVO> voList = new ArrayList<>();
        for (ProductEntity entity : pageResult.getRecords()) {
            voList.add(toProductListVO(entity, availableQtyMap.getOrDefault(entity.getSkuCode(), 0)));
        }

        return new PageResult<>(voList, page, pageSize, pageResult.getTotal());
    }

    @Override
    public ProductDetailVO getProductDetail(Long productId) {
        ProductEntity entity = productMapper.selectById(productId);
        if (entity == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Product not found");
        }
        Map<String, Integer> availableQtyMap = loadAvailableQty(List.of(entity));
        return toProductDetailVO(entity, availableQtyMap.getOrDefault(entity.getSkuCode(), 0));
    }

    private List<CategoryVO> buildCategoryTree(List<ProductCategoryEntity> entities) {
        Map<Long, CategoryVO> voMap = new LinkedHashMap<>();
        for (ProductCategoryEntity entity : entities) {
            CategoryVO vo = new CategoryVO();
            vo.setId(entity.getId());
            vo.setParentId(entity.getParentId());
            vo.setNameZh(entity.getNameZh());
            vo.setNameEn(entity.getNameEn());
            vo.setSortOrder(entity.getSortOrder());
            vo.setChildren(new ArrayList<>());
            voMap.put(entity.getId(), vo);
        }

        List<CategoryVO> roots = new ArrayList<>();
        for (CategoryVO vo : voMap.values()) {
            if (vo.getParentId() == null) {
                roots.add(vo);
            } else {
                CategoryVO parent = voMap.get(vo.getParentId());
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        }
        return roots;
    }

    private Map<String, Integer> loadAvailableQty(List<ProductEntity> products) {
        Map<String, Integer> result = new HashMap<>();
        if (products.isEmpty()) return result;
        Set<String> skuCodes = new HashSet<>();
        for (ProductEntity product : products) {
            skuCodes.add(product.getSkuCode());
        }
        List<InventoryEntity> inventoryList = inventoryMapper.selectList(
                new LambdaQueryWrapper<InventoryEntity>().in(InventoryEntity::getSkuCode, skuCodes)
        );
        for (InventoryEntity inventory : inventoryList) {
            result.merge(inventory.getSkuCode(), inventory.getAvailableQty() == null ? 0 : inventory.getAvailableQty(),
                    Integer::sum);
        }
        return result;
    }

    private ProductListVO toProductListVO(ProductEntity entity, int availableQty) {
        ProductListVO vo = new ProductListVO();
        vo.setId(entity.getId());
        vo.setSkuCode(entity.getSkuCode());
        vo.setNameZh(entity.getNameZh());
        vo.setNameEn(entity.getNameEn());
        vo.setPrice(entity.getPrice() != null ? entity.getPrice().toPlainString() : "0");
        vo.setMainImageUrl(entity.getMainImageUrl());
        vo.setDroneDeliverable(entity.getDroneDeliverable() != null && entity.getDroneDeliverable() == 1);
        vo.setWeightKg(entity.getWeightKg() != null ? entity.getWeightKg().toPlainString() : null);
        vo.setVolumeM3(entity.getVolumeM3() != null ? entity.getVolumeM3().toPlainString() : null);
        vo.setAvailableQty(availableQty);
        return vo;
    }

    private ProductDetailVO toProductDetailVO(ProductEntity entity, int availableQty) {
        ProductDetailVO vo = new ProductDetailVO();
        vo.setId(entity.getId());
        vo.setSkuCode(entity.getSkuCode());
        vo.setCategoryId(entity.getCategoryId());
        vo.setNameZh(entity.getNameZh());
        vo.setNameEn(entity.getNameEn());
        vo.setDescriptionZh(entity.getDescriptionZh());
        vo.setDescriptionEn(entity.getDescriptionEn());
        vo.setMainImageUrl(entity.getMainImageUrl());
        vo.setSpecification(entity.getSpecification());
        vo.setPrice(entity.getPrice() != null ? entity.getPrice().toPlainString() : "0");
        vo.setWeightKg(entity.getWeightKg() != null ? entity.getWeightKg().toPlainString() : null);
        vo.setVolumeM3(entity.getVolumeM3() != null ? entity.getVolumeM3().toPlainString() : null);
        vo.setSource(entity.getSource());
        vo.setDroneDeliverable(entity.getDroneDeliverable() != null && entity.getDroneDeliverable() == 1);
        vo.setStatus(entity.getStatus());
        vo.setAvailableQty(availableQty);
        return vo;
    }
}
