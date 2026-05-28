package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.dto.CreateOrderDTO;
import com.tsingtaohui.model.dto.OrderItemDTO;
import com.tsingtaohui.model.vo.OrderEstimateVO;
import com.tsingtaohui.model.vo.OrderVO;

import java.util.List;

public interface IOrderService {

    OrderEstimateVO estimate(Long userId, List<OrderItemDTO> items);

    OrderVO createOrder(Long userId, CreateOrderDTO dto);

    PageResult<OrderVO> getMyOrders(Long userId, String status, int page, int pageSize);

    OrderVO getMyOrderDetail(Long userId, Long orderId);

    PageResult<OrderVO> getAdminOrders(String keyword, String status, String tradeMode, int page, int pageSize);

    PageResult<OrderVO> getAdminMatchingOrders(String keyword, String status, int page, int pageSize);

    OrderVO getAdminOrderDetail(Long orderId);

    OrderVO updateAdminOrderStatus(Long orderId, String status);
}
