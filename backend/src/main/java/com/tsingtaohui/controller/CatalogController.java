package com.tsingtaohui.controller;

import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.vo.CategoryVO;
import com.tsingtaohui.model.vo.ProductDetailVO;
import com.tsingtaohui.model.vo.ProductListVO;
import com.tsingtaohui.service.ICatalogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalog")
public class CatalogController {

    private final ICatalogService catalogService;

    public CatalogController(ICatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryVO>> getCategories() {
        return ApiResponse.ok(catalogService.getCategories());
    }

    @GetMapping("/products")
    public ApiResponse<PageResult<ProductListVO>> getProducts(
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(catalogService.getProducts(categoryId, keyword, page, pageSize));
    }

    @GetMapping("/products/{productId}")
    public ApiResponse<ProductDetailVO> getProductDetail(@PathVariable Long productId) {
        return ApiResponse.ok(catalogService.getProductDetail(productId));
    }
}
