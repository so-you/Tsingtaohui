package com.tsingtaohui.controller;

import com.tsingtaohui.common.context.UserContextHolder;
import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.dto.CartEstimateDTO;
import com.tsingtaohui.model.dto.CreateOrderDTO;
import com.tsingtaohui.model.vo.OrderEstimateVO;
import com.tsingtaohui.model.vo.OrderVO;
import com.tsingtaohui.service.IOrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/estimate")
    public ApiResponse<OrderEstimateVO> estimate(@Valid @RequestBody CartEstimateDTO dto) {
        return ApiResponse.ok(orderService.estimate(UserContextHolder.getUserId(), dto.getItems()));
    }

    @PostMapping
    public ApiResponse<OrderVO> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        return ApiResponse.ok(orderService.createOrder(UserContextHolder.getUserId(), dto));
    }

    @GetMapping
    public ApiResponse<PageResult<OrderVO>> getMyOrders(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(orderService.getMyOrders(UserContextHolder.getUserId(), status, page, pageSize));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderVO> getMyOrderDetail(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.getMyOrderDetail(UserContextHolder.getUserId(), orderId));
    }
}
