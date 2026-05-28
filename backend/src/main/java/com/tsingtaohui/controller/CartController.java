package com.tsingtaohui.controller;

import com.tsingtaohui.common.context.UserContextHolder;
import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.model.dto.CartEstimateDTO;
import com.tsingtaohui.model.vo.OrderEstimateVO;
import com.tsingtaohui.service.IOrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final IOrderService orderService;

    public CartController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/estimate")
    public ApiResponse<OrderEstimateVO> estimate(@Valid @RequestBody CartEstimateDTO dto) {
        return ApiResponse.ok(orderService.estimate(UserContextHolder.getUserId(), dto.getItems()));
    }
}
