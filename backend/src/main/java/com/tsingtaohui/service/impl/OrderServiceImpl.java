package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.enums.OrderStatus;
import com.tsingtaohui.common.enums.TradeMode;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.InventoryMapper;
import com.tsingtaohui.mapper.OrderItemMapper;
import com.tsingtaohui.mapper.OrderMapper;
import com.tsingtaohui.mapper.ProductMapper;
import com.tsingtaohui.mapper.UserShipMapper;
import com.tsingtaohui.model.dto.CreateOrderDTO;
import com.tsingtaohui.model.dto.OrderItemDTO;
import com.tsingtaohui.model.entity.InventoryEntity;
import com.tsingtaohui.model.entity.OrderEntity;
import com.tsingtaohui.model.entity.OrderItemEntity;
import com.tsingtaohui.model.entity.ProductEntity;
import com.tsingtaohui.model.entity.UserShipEntity;
import com.tsingtaohui.model.vo.OrderEstimateVO;
import com.tsingtaohui.model.vo.OrderItemVO;
import com.tsingtaohui.model.vo.OrderVO;
import com.tsingtaohui.service.IOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrderServiceImpl implements IOrderService {

    private static final BigDecimal AUTO_TRADE_MAX_WEIGHT_KG = new BigDecimal("20.000");
    private static final BigDecimal AUTO_TRADE_MAX_VOLUME_M3 = new BigDecimal("1.0000");
    private static final DateTimeFormatter ORDER_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final Set<String> ADMIN_ORDER_STATUSES = Set.of(
            OrderStatus.PENDING_CONFIRM.name(),
            OrderStatus.CONFIRMED.name(),
            OrderStatus.WAREHOUSE_PROCESSING.name(),
            OrderStatus.PENDING_OUTBOUND.name(),
            OrderStatus.OUTBOUND.name(),
            OrderStatus.PENDING_LOADING.name(),
            OrderStatus.IN_DELIVERY.name(),
            OrderStatus.PENDING_RECEIPT.name(),
            OrderStatus.COMPLETED.name(),
            OrderStatus.CANCELLED.name(),
            OrderStatus.EXCEPTION.name()
    );

    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final UserShipMapper userShipMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public OrderServiceImpl(ProductMapper productMapper,
                            InventoryMapper inventoryMapper,
                            UserShipMapper userShipMapper,
                            OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper) {
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
        this.userShipMapper = userShipMapper;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public OrderEstimateVO estimate(Long userId, List<OrderItemDTO> items) {
        CartSnapshot snapshot = buildSnapshot(items);
        return toEstimateVO(snapshot);
    }

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, CreateOrderDTO dto) {
        CartSnapshot snapshot = buildSnapshot(dto.getItems());
        if (!snapshot.stockSufficient) {
            throw new BusinessException(ErrorCode.ORDER_STOCK_INSUFFICIENT.getCode(),
                    ErrorCode.ORDER_STOCK_INSUFFICIENT.getMessageZh());
        }

        UserShipEntity defaultShip = loadDefaultShip(userId);
        OrderEntity order = new OrderEntity();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTotalPrice(snapshot.totalPrice);
        order.setTotalWeightKg(snapshot.totalWeightKg);
        order.setTotalVolumeM3(snapshot.totalVolumeM3);
        order.setTradeMode(snapshot.tradeMode);
        order.setOrderStatus(TradeMode.AUTO_TRADE.name().equals(snapshot.tradeMode)
                ? OrderStatus.CONFIRMED.name()
                : OrderStatus.PENDING_CONFIRM.name());
        order.setConsigneeName(dto.getConsigneeName());
        order.setCabinNo(dto.getCabinNo());
        order.setContactInfo(dto.getContactInfo());
        order.setExpectedDeliveryTime(dto.getExpectedDeliveryTime());
        order.setRemark(dto.getRemark());
        order.setShipNo(firstText(dto.getShipNo(), defaultShip != null ? defaultShip.getShipNo() : null));
        order.setShipName(firstText(dto.getShipName(), defaultShip != null ? defaultShip.getShipName() : null));
        order.setShipNationality(firstText(dto.getShipNationality(),
                defaultShip != null ? defaultShip.getShipNationality() : null));
        order.setImo(firstText(dto.getImo(), defaultShip != null ? defaultShip.getImo() : null));
        order.setMmsi(firstText(dto.getMmsi(), defaultShip != null ? defaultShip.getMmsi() : null));
        order.setBerthOrAnchorage(dto.getBerthOrAnchorage());
        order.setTargetGps(dto.getTargetGps());
        order.setShippingAgentId(dto.getShippingAgentId() != null ? dto.getShippingAgentId() : 0L);
        order.setShippingAgentName(firstText(dto.getShippingAgentName(), "SELF"));

        if (!StringUtils.hasText(order.getShipNo()) || !StringUtils.hasText(order.getShipNationality())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Ship information is required");
        }

        orderMapper.insert(order);
        List<OrderItemEntity> orderItems = toOrderItems(order, snapshot);
        for (OrderItemEntity item : orderItems) {
            orderItemMapper.insert(item);
        }
        lockInventory(snapshot);

        return toOrderVO(order, orderItems);
    }

    @Override
    public PageResult<OrderVO> getMyOrders(Long userId, String status, int page, int pageSize) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getUserId, userId);
        if (StringUtils.hasText(status)) {
            wrapper.eq(OrderEntity::getOrderStatus, status.trim());
        }
        wrapper.orderByDesc(OrderEntity::getCreatedAt);
        return pageOrders(wrapper, page, pageSize, true);
    }

    @Override
    public OrderVO getMyOrderDetail(Long userId, Long orderId) {
        OrderEntity order = orderMapper.selectById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }
        return toOrderVO(order, loadItems(List.of(order.getId())).getOrDefault(order.getId(), List.of()));
    }

    @Override
    public PageResult<OrderVO> getAdminOrders(String keyword, String status, String tradeMode, int page, int pageSize) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<>();
        applyAdminFilters(wrapper, keyword, status, tradeMode);
        wrapper.orderByDesc(OrderEntity::getCreatedAt);
        return pageOrders(wrapper, page, pageSize, true);
    }

    @Override
    public PageResult<OrderVO> getAdminMatchingOrders(String keyword, String status, int page, int pageSize) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getTradeMode, TradeMode.MATCHING_ORDER.name());
        if (StringUtils.hasText(status)) {
            wrapper.eq(OrderEntity::getOrderStatus, status.trim());
        } else {
            wrapper.eq(OrderEntity::getOrderStatus, OrderStatus.PENDING_CONFIRM.name());
        }
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(w -> w.like(OrderEntity::getOrderNo, value)
                    .or().like(OrderEntity::getShipNo, value)
                    .or().like(OrderEntity::getConsigneeName, value));
        }
        wrapper.orderByDesc(OrderEntity::getCreatedAt);
        return pageOrders(wrapper, page, pageSize, true);
    }

    @Override
    public OrderVO getAdminOrderDetail(Long orderId) {
        OrderEntity order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }
        return toOrderVO(order, loadItems(List.of(order.getId())).getOrDefault(order.getId(), List.of()));
    }

    @Override
    public OrderVO updateAdminOrderStatus(Long orderId, String status) {
        if (!ADMIN_ORDER_STATUSES.contains(status)) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID.getCode(),
                    ErrorCode.ORDER_STATUS_INVALID.getMessageZh());
        }
        OrderEntity order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }
        order.setOrderStatus(status);
        if (OrderStatus.COMPLETED.name().equals(status)) {
            order.setCompletedAt(LocalDateTime.now());
        }
        orderMapper.updateById(order);
        return toOrderVO(order, loadItems(List.of(order.getId())).getOrDefault(order.getId(), List.of()));
    }

    private CartSnapshot buildSnapshot(List<OrderItemDTO> items) {
        if (items == null || items.isEmpty()) {
            throw new BusinessException(ErrorCode.ORDER_CART_EMPTY.getCode(), ErrorCode.ORDER_CART_EMPTY.getMessageZh());
        }

        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderItemDTO item : items) {
            if (item.getProductId() == null || item.getQuantity() == null || item.getQuantity() < 1) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Invalid order item");
            }
            quantities.merge(item.getProductId(), item.getQuantity(), Integer::sum);
        }

        List<ProductEntity> products = productMapper.selectList(
                new LambdaQueryWrapper<ProductEntity>()
                        .in(ProductEntity::getId, quantities.keySet())
                        .eq(ProductEntity::getStatus, "ON_SALE")
        );
        Map<Long, ProductEntity> productMap = new HashMap<>();
        for (ProductEntity product : products) {
            productMap.put(product.getId(), product);
        }
        if (productMap.size() != quantities.size()) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Product not found or off sale");
        }

        Map<String, Integer> stock = loadAvailableStock(products);
        CartSnapshot snapshot = new CartSnapshot();
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            ProductEntity product = productMap.get(entry.getKey());
            int quantity = entry.getValue();
            BigDecimal lineAmount = safeMoney(product.getPrice()).multiply(BigDecimal.valueOf(quantity));
            BigDecimal lineWeight = safeDecimal(product.getWeightKg()).multiply(BigDecimal.valueOf(quantity));
            BigDecimal lineVolume = safeDecimal(product.getVolumeM3()).multiply(BigDecimal.valueOf(quantity));

            snapshot.totalPrice = snapshot.totalPrice.add(lineAmount);
            snapshot.totalWeightKg = snapshot.totalWeightKg.add(lineWeight);
            snapshot.totalVolumeM3 = snapshot.totalVolumeM3.add(lineVolume);
            snapshot.lines.add(new CartLine(product, quantity, lineAmount));

            int availableQty = stock.getOrDefault(product.getSkuCode(), 0);
            if (availableQty < quantity) {
                snapshot.stockSufficient = false;
                snapshot.reasons.add("insufficient_stock:" + product.getSkuCode());
            }
            if (product.getDroneDeliverable() == null || product.getDroneDeliverable() != 1) {
                snapshot.reasons.add("not_drone_deliverable:" + product.getSkuCode());
            }
        }

        if (snapshot.totalWeightKg.compareTo(AUTO_TRADE_MAX_WEIGHT_KG) > 0) {
            snapshot.reasons.add("weight_exceeds_auto_trade_limit");
        }
        if (snapshot.totalVolumeM3.compareTo(AUTO_TRADE_MAX_VOLUME_M3) > 0) {
            snapshot.reasons.add("volume_exceeds_auto_trade_limit");
        }

        snapshot.tradeMode = snapshot.stockSufficient && snapshot.reasons.isEmpty()
                ? TradeMode.AUTO_TRADE.name()
                : TradeMode.MATCHING_ORDER.name();
        return snapshot;
    }

    private Map<String, Integer> loadAvailableStock(List<ProductEntity> products) {
        Map<String, Integer> result = new HashMap<>();
        if (products.isEmpty()) {
            return result;
        }
        List<String> skuCodes = products.stream().map(ProductEntity::getSkuCode).toList();
        List<InventoryEntity> inventories = inventoryMapper.selectList(
                new LambdaQueryWrapper<InventoryEntity>().in(InventoryEntity::getSkuCode, skuCodes)
        );
        for (InventoryEntity inventory : inventories) {
            result.merge(inventory.getSkuCode(), nullToZero(inventory.getAvailableQty()), Integer::sum);
        }
        return result;
    }

    private UserShipEntity loadDefaultShip(Long userId) {
        return userShipMapper.selectOne(
                new LambdaQueryWrapper<UserShipEntity>()
                        .eq(UserShipEntity::getUserId, userId)
                        .eq(UserShipEntity::getIsDefault, 1)
                        .last("limit 1")
        );
    }

    private void lockInventory(CartSnapshot snapshot) {
        for (CartLine line : snapshot.lines) {
            int remaining = line.quantity;
            List<InventoryEntity> inventories = inventoryMapper.selectList(
                    new LambdaQueryWrapper<InventoryEntity>()
                            .eq(InventoryEntity::getSkuCode, line.product.getSkuCode())
                            .gt(InventoryEntity::getAvailableQty, 0)
                            .orderByAsc(InventoryEntity::getUpdatedAt)
            );
            for (InventoryEntity inventory : inventories) {
                if (remaining <= 0) {
                    break;
                }
                int available = nullToZero(inventory.getAvailableQty());
                int locked = Math.min(available, remaining);
                inventory.setAvailableQty(available - locked);
                inventory.setLockedQty(nullToZero(inventory.getLockedQty()) + locked);
                inventoryMapper.updateById(inventory);
                remaining -= locked;
            }
            if (remaining > 0) {
                throw new BusinessException(ErrorCode.ORDER_STOCK_INSUFFICIENT.getCode(),
                        ErrorCode.ORDER_STOCK_INSUFFICIENT.getMessageZh());
            }
        }
    }

    private List<OrderItemEntity> toOrderItems(OrderEntity order, CartSnapshot snapshot) {
        List<OrderItemEntity> items = new ArrayList<>();
        for (CartLine line : snapshot.lines) {
            ProductEntity product = line.product;
            OrderItemEntity item = new OrderItemEntity();
            item.setOrderId(order.getId());
            item.setOrderNo(order.getOrderNo());
            item.setProductId(product.getId());
            item.setSkuCode(product.getSkuCode());
            item.setProductNameZh(product.getNameZh());
            item.setProductNameEn(product.getNameEn());
            item.setUnitPrice(safeMoney(product.getPrice()));
            item.setQuantity(line.quantity);
            item.setUnitWeightKg(safeDecimal(product.getWeightKg()));
            item.setUnitVolumeM3(safeDecimal(product.getVolumeM3()));
            item.setLineAmount(line.lineAmount);
            items.add(item);
        }
        return items;
    }

    private PageResult<OrderVO> pageOrders(LambdaQueryWrapper<OrderEntity> wrapper, int page, int pageSize,
                                           boolean includeItems) {
        int safePage = safePage(page);
        int safePageSize = safePageSize(pageSize);
        Page<OrderEntity> pageResult = orderMapper.selectPage(new Page<>(safePage, safePageSize), wrapper);
        Map<Long, List<OrderItemEntity>> itemMap = includeItems
                ? loadItems(pageResult.getRecords().stream().map(OrderEntity::getId).toList())
                : Map.of();
        List<OrderVO> items = new ArrayList<>();
        for (OrderEntity order : pageResult.getRecords()) {
            items.add(toOrderVO(order, itemMap.getOrDefault(order.getId(), List.of())));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    private Map<Long, List<OrderItemEntity>> loadItems(List<Long> orderIds) {
        Map<Long, List<OrderItemEntity>> result = new HashMap<>();
        if (orderIds == null || orderIds.isEmpty()) {
            return result;
        }
        List<OrderItemEntity> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemEntity>()
                        .in(OrderItemEntity::getOrderId, orderIds)
                        .orderByAsc(OrderItemEntity::getId)
        );
        for (OrderItemEntity item : items) {
            result.computeIfAbsent(item.getOrderId(), key -> new ArrayList<>()).add(item);
        }
        return result;
    }

    private void applyAdminFilters(LambdaQueryWrapper<OrderEntity> wrapper, String keyword,
                                   String status, String tradeMode) {
        if (StringUtils.hasText(keyword)) {
            String value = keyword.trim();
            wrapper.and(w -> w.like(OrderEntity::getOrderNo, value)
                    .or().like(OrderEntity::getShipNo, value)
                    .or().like(OrderEntity::getConsigneeName, value));
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(OrderEntity::getOrderStatus, status.trim());
        }
        if (StringUtils.hasText(tradeMode)) {
            wrapper.eq(OrderEntity::getTradeMode, tradeMode.trim());
        }
    }

    private OrderEstimateVO toEstimateVO(CartSnapshot snapshot) {
        OrderEstimateVO vo = new OrderEstimateVO();
        vo.setTotalPrice(snapshot.totalPrice.toPlainString());
        vo.setTotalWeightKg(snapshot.totalWeightKg.toPlainString());
        vo.setTotalVolumeM3(snapshot.totalVolumeM3.toPlainString());
        vo.setTradeMode(snapshot.tradeMode);
        vo.setCanAutoTrade(TradeMode.AUTO_TRADE.name().equals(snapshot.tradeMode));
        vo.setReasons(snapshot.reasons);
        List<OrderItemVO> items = new ArrayList<>();
        for (CartLine line : snapshot.lines) {
            OrderItemVO item = new OrderItemVO();
            item.setProductId(line.product.getId());
            item.setSkuCode(line.product.getSkuCode());
            item.setProductNameZh(line.product.getNameZh());
            item.setProductNameEn(line.product.getNameEn());
            item.setUnitPrice(safeMoney(line.product.getPrice()).toPlainString());
            item.setUnitWeightKg(safeDecimal(line.product.getWeightKg()).toPlainString());
            item.setUnitVolumeM3(safeDecimal(line.product.getVolumeM3()).toPlainString());
            item.setQuantity(line.quantity);
            item.setLineAmount(line.lineAmount.toPlainString());
            items.add(item);
        }
        vo.setItems(items);
        return vo;
    }

    private OrderVO toOrderVO(OrderEntity order, List<OrderItemEntity> items) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setUserId(order.getUserId());
        vo.setTotalPrice(toPlainString(order.getTotalPrice()));
        vo.setTotalWeightKg(toPlainString(order.getTotalWeightKg()));
        vo.setTotalVolumeM3(toPlainString(order.getTotalVolumeM3()));
        vo.setTradeMode(order.getTradeMode());
        vo.setOrderStatus(order.getOrderStatus());
        vo.setWarehouseStatus(order.getWarehouseStatus());
        vo.setDeliveryStatus(order.getDeliveryStatus());
        vo.setCustomsSyncStatus(order.getCustomsSyncStatus());
        vo.setConsigneeName(order.getConsigneeName());
        vo.setCabinNo(order.getCabinNo());
        vo.setContactInfo(order.getContactInfo());
        vo.setExpectedDeliveryTime(order.getExpectedDeliveryTime());
        vo.setRemark(order.getRemark());
        vo.setShipNo(order.getShipNo());
        vo.setShipName(order.getShipName());
        vo.setShipNationality(order.getShipNationality());
        vo.setImo(order.getImo());
        vo.setMmsi(order.getMmsi());
        vo.setBerthOrAnchorage(order.getBerthOrAnchorage());
        vo.setTargetGps(order.getTargetGps());
        vo.setShippingAgentId(order.getShippingAgentId());
        vo.setShippingAgentName(order.getShippingAgentName());
        vo.setCompletedAt(order.getCompletedAt());
        vo.setCreatedAt(order.getCreatedAt());

        List<OrderItemVO> itemVOs = new ArrayList<>();
        for (OrderItemEntity item : items) {
            itemVOs.add(toOrderItemVO(item));
        }
        vo.setItems(itemVOs);
        return vo;
    }

    private OrderItemVO toOrderItemVO(OrderItemEntity item) {
        OrderItemVO vo = new OrderItemVO();
        vo.setId(item.getId());
        vo.setProductId(item.getProductId());
        vo.setSkuCode(item.getSkuCode());
        vo.setProductNameZh(item.getProductNameZh());
        vo.setProductNameEn(item.getProductNameEn());
        vo.setUnitPrice(toPlainString(item.getUnitPrice()));
        vo.setQuantity(item.getQuantity());
        vo.setUnitWeightKg(toPlainString(item.getUnitWeightKg()));
        vo.setUnitVolumeM3(toPlainString(item.getUnitVolumeM3()));
        vo.setLineAmount(toPlainString(item.getLineAmount()));
        return vo;
    }

    private String generateOrderNo() {
        int suffix = ThreadLocalRandom.current().nextInt(100, 1000);
        return "TH" + LocalDateTime.now().format(ORDER_NO_TIME) + suffix;
    }

    private String firstText(String first, String second) {
        return StringUtils.hasText(first) ? first.trim() : second;
    }

    private BigDecimal safeMoney(BigDecimal value) {
        return value == null ? new BigDecimal("0.00") : value;
    }

    private BigDecimal safeDecimal(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String toPlainString(BigDecimal value) {
        return value == null ? "0" : value.toPlainString();
    }

    private int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    private int safePage(int page) {
        return Math.max(page, 1);
    }

    private int safePageSize(int pageSize) {
        return Math.min(Math.max(pageSize, 1), 100);
    }

    private static class CartSnapshot {
        private BigDecimal totalPrice = BigDecimal.ZERO;
        private BigDecimal totalWeightKg = BigDecimal.ZERO;
        private BigDecimal totalVolumeM3 = BigDecimal.ZERO;
        private String tradeMode = TradeMode.MATCHING_ORDER.name();
        private boolean stockSufficient = true;
        private List<String> reasons = new ArrayList<>();
        private List<CartLine> lines = new ArrayList<>();
    }

    private record CartLine(ProductEntity product, int quantity, BigDecimal lineAmount) {
    }
}
