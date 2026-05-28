package com.tsingtaohui.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tsingtaohui.common.context.UserContextHolder;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.enums.OrderStatus;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.DeliveryTaskMapper;
import com.tsingtaohui.mapper.OrderMapper;
import com.tsingtaohui.model.dto.CartEstimateDTO;
import com.tsingtaohui.model.dto.CreateOrderDTO;
import com.tsingtaohui.model.entity.DeliveryTaskEntity;
import com.tsingtaohui.model.entity.OrderEntity;
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

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final IOrderService orderService;
    private final OrderMapper orderMapper;
    private final DeliveryTaskMapper deliveryTaskMapper;

    public OrderController(IOrderService orderService, OrderMapper orderMapper,
                           DeliveryTaskMapper deliveryTaskMapper) {
        this.orderService = orderService;
        this.orderMapper = orderMapper;
        this.deliveryTaskMapper = deliveryTaskMapper;
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

    @PostMapping("/{orderNo}/receipt/verify-code")
    public ApiResponse<Map<String, Object>> verifyReceiptCode(@PathVariable String orderNo,
                                                               @RequestBody Map<String, String> body) {
        String verifyCode = body.get("verifyCode");
        if (verifyCode == null || !verifyCode.matches("\\d{6}")) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Invalid verify code, must be 6 digits");
        }

        OrderEntity order = orderMapper.selectOne(
                new LambdaQueryWrapper<OrderEntity>().eq(OrderEntity::getOrderNo, orderNo)
        );
        if (order == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }
        if (!order.getUserId().equals(UserContextHolder.getUserId())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "You can only confirm your own orders");
        }
        if (!OrderStatus.PENDING_RECEIPT.name().equals(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID.getCode(),
                    ErrorCode.ORDER_STATUS_INVALID.getMessageZh());
        }

        // MVP: accept any 6-digit code
        order.setOrderStatus(OrderStatus.COMPLETED.name());
        order.setDeliveryStatus("RECEIVED");
        order.setCompletedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        return ApiResponse.ok(Map.of(
                "orderNo", orderNo,
                "orderStatus", OrderStatus.COMPLETED.name()
        ));
    }

    @PostMapping("/receipt/package-scan")
    public ApiResponse<Map<String, Object>> receiptPackageScan(@RequestBody Map<String, String> body) {
        String packageNo = body.get("packageNo");
        if (packageNo == null || packageNo.isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "packageNo is required");
        }

        DeliveryTaskEntity task = deliveryTaskMapper.selectOne(
                new LambdaQueryWrapper<DeliveryTaskEntity>().eq(DeliveryTaskEntity::getPackageNo, packageNo)
        );
        if (task == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Delivery task not found for package");
        }
        if (!"DELIVERED".equals(task.getTaskStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID.getCode(),
                    "Package has not been delivered yet");
        }

        OrderEntity order = orderMapper.selectById(task.getOrderId());
        if (order == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }

        order.setOrderStatus(OrderStatus.COMPLETED.name());
        order.setDeliveryStatus("RECEIVED");
        order.setCompletedAt(LocalDateTime.now());
        orderMapper.updateById(order);

        return ApiResponse.ok(Map.of(
                "orderNo", order.getOrderNo(),
                "packageNo", packageNo,
                "orderStatus", OrderStatus.COMPLETED.name()
        ));
    }
}
