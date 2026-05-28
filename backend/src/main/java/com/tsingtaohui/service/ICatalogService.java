package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.vo.CategoryVO;
import com.tsingtaohui.model.vo.ProductDetailVO;
import com.tsingtaohui.model.vo.ProductListVO;

import java.util.List;

public interface ICatalogService {

    List<CategoryVO> getCategories();

    PageResult<ProductListVO> getProducts(Long categoryId, String keyword, int page, int pageSize);

    ProductDetailVO getProductDetail(Long productId);
}
