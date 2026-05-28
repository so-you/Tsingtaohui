package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.enums.OrderStatus;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.CustomsSyncRecordMapper;
import com.tsingtaohui.mapper.InventoryMapper;
import com.tsingtaohui.mapper.OrderItemMapper;
import com.tsingtaohui.mapper.OrderMapper;
import com.tsingtaohui.mapper.PackageMapper;
import com.tsingtaohui.model.entity.InventoryEntity;
import com.tsingtaohui.model.entity.OrderEntity;
import com.tsingtaohui.model.entity.OrderItemEntity;
import com.tsingtaohui.model.entity.PackageEntity;
import com.tsingtaohui.service.ICustomsSyncService;
import com.tsingtaohui.service.IWarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class WarehouseServiceImpl implements IWarehouseService {

    private static final DateTimeFormatter PKG_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final String WAREHOUSE_STATUS_PICKING = "PICKING";
    private static final String WAREHOUSE_STATUS_PICKED = "PICKED";
    private static final String WAREHOUSE_STATUS_PACKED = "PACKED";
    private static final String WAREHOUSE_STATUS_PENDING_OUTBOUND = "PENDING_OUTBOUND";
    private static final String WAREHOUSE_STATUS_OUTBOUND = "OUTBOUND";
    private static final String PACKAGE_STATUS_PACKED = "PACKED";

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final InventoryMapper inventoryMapper;
    private final PackageMapper packageMapper;
    private final ICustomsSyncService customsSyncService;

    public WarehouseServiceImpl(OrderMapper orderMapper,
                                OrderItemMapper orderItemMapper,
                                InventoryMapper inventoryMapper,
                                PackageMapper packageMapper,
                                ICustomsSyncService customsSyncService) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.inventoryMapper = inventoryMapper;
        this.packageMapper = packageMapper;
        this.customsSyncService = customsSyncService;
    }

    @Override
    public Map<String, Object> getDashboard() {
        long pendingPick = orderMapper.selectCount(
                new LambdaQueryWrapper<OrderEntity>()
                        .eq(OrderEntity::getOrderStatus, OrderStatus.WAREHOUSE_PROCESSING.name())
        );
        long pendingReview = orderMapper.selectCount(
                new LambdaQueryWrapper<OrderEntity>()
                        .eq(OrderEntity::getOrderStatus, OrderStatus.WAREHOUSE_PROCESSING.name())
                        .eq(OrderEntity::getWarehouseStatus, WAREHOUSE_STATUS_PICKED)
        );
        long pendingOutbound = orderMapper.selectCount(
                new LambdaQueryWrapper<OrderEntity>()
                        .eq(OrderEntity::getOrderStatus, OrderStatus.PENDING_OUTBOUND.name())
        );
        long exceptionOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<OrderEntity>()
                        .eq(OrderEntity::getOrderStatus, OrderStatus.EXCEPTION.name())
        );

        Map<String, Object> result = new HashMap<>();
        result.put("pendingPick", pendingPick);
        result.put("pendingReview", pendingReview);
        result.put("pendingOutbound", pendingOutbound);
        result.put("exceptionOrders", exceptionOrders);
        return result;
    }

    @Override
    public PageResult<Map<String, Object>> getPickingTasks(int page, int pageSize) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getOrderStatus, OrderStatus.WAREHOUSE_PROCESSING.name())
                .and(w -> w.isNull(OrderEntity::getWarehouseStatus)
                        .or().ne(OrderEntity::getWarehouseStatus, WAREHOUSE_STATUS_PICKED))
                .orderByAsc(OrderEntity::getCreatedAt);

        int safePage = safePage(page);
        int safePageSize = safePageSize(pageSize);
        Page<OrderEntity> pageResult = orderMapper.selectPage(new Page<>(safePage, safePageSize), wrapper);

        List<Map<String, Object>> items = new ArrayList<>();
        for (OrderEntity order : pageResult.getRecords()) {
            items.add(toTaskMap(order));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    @Override
    public void confirmPickingScan(Long taskId, String skuCode) {
        OrderEntity order = orderMapper.selectById(taskId);
        if (order == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }
        if (!OrderStatus.WAREHOUSE_PROCESSING.name().equals(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID.getCode(),
                    ErrorCode.ORDER_STATUS_INVALID.getMessageZh());
        }

        // Verify SKU belongs to this order
        List<OrderItemEntity> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, taskId)
        );
        boolean found = false;
        for (OrderItemEntity item : orderItems) {
            if (skuCode.equals(item.getSkuCode())) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new BusinessException(ErrorCode.WAREHOUSE_SCAN_MISMATCH.getCode(),
                    ErrorCode.WAREHOUSE_SCAN_MISMATCH.getMessageZh());
        }

        // Mark as picking in progress
        order.setWarehouseStatus(WAREHOUSE_STATUS_PICKING);
        orderMapper.updateById(order);
    }

    @Override
    public PageResult<Map<String, Object>> getReviewTasks(int page, int pageSize) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getOrderStatus, OrderStatus.WAREHOUSE_PROCESSING.name())
                .eq(OrderEntity::getWarehouseStatus, WAREHOUSE_STATUS_PICKED)
                .orderByAsc(OrderEntity::getCreatedAt);

        int safePage = safePage(page);
        int safePageSize = safePageSize(pageSize);
        Page<OrderEntity> pageResult = orderMapper.selectPage(new Page<>(safePage, safePageSize), wrapper);

        List<Map<String, Object>> items = new ArrayList<>();
        for (OrderEntity order : pageResult.getRecords()) {
            items.add(toTaskMap(order));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    @Override
    public void scanProductForReview(Long taskId, String skuCode) {
        OrderEntity order = orderMapper.selectById(taskId);
        if (order == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }

        List<OrderItemEntity> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, taskId)
        );
        boolean found = false;
        for (OrderItemEntity item : orderItems) {
            if (skuCode.equals(item.getSkuCode())) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new BusinessException(ErrorCode.WAREHOUSE_SCAN_MISMATCH.getCode(),
                    ErrorCode.WAREHOUSE_SCAN_MISMATCH.getMessageZh());
        }
    }

    @Override
    @Transactional
    public String packOrder(Long taskId) {
        OrderEntity order = orderMapper.selectById(taskId);
        if (order == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }
        if (!OrderStatus.WAREHOUSE_PROCESSING.name().equals(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID.getCode(),
                    ErrorCode.ORDER_STATUS_INVALID.getMessageZh());
        }

        // Create package
        String packageNo = generatePackageNo();
        PackageEntity pkg = new PackageEntity();
        pkg.setPackageNo(packageNo);
        pkg.setOrderId(order.getId());
        pkg.setOrderNo(order.getOrderNo());
        pkg.setActualWeightKg(order.getTotalWeightKg());
        pkg.setActualVolumeM3(order.getTotalVolumeM3());
        pkg.setPackageStatus(PACKAGE_STATUS_PACKED);
        packageMapper.insert(pkg);

        // Update order statuses
        order.setWarehouseStatus(WAREHOUSE_STATUS_PACKED);
        order.setOrderStatus(OrderStatus.PENDING_OUTBOUND.name());
        orderMapper.updateById(order);

        return packageNo;
    }

    @Override
    public PageResult<Map<String, Object>> getOutboundTasks(int page, int pageSize) {
        LambdaQueryWrapper<OrderEntity> wrapper = new LambdaQueryWrapper<OrderEntity>()
                .eq(OrderEntity::getOrderStatus, OrderStatus.PENDING_OUTBOUND.name())
                .orderByAsc(OrderEntity::getCreatedAt);

        int safePage = safePage(page);
        int safePageSize = safePageSize(pageSize);
        Page<OrderEntity> pageResult = orderMapper.selectPage(new Page<>(safePage, safePageSize), wrapper);

        List<Map<String, Object>> items = new ArrayList<>();
        for (OrderEntity order : pageResult.getRecords()) {
            items.add(toTaskMap(order));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    @Override
    @Transactional
    public Map<String, Object> confirmOutbound(Long taskId) {
        OrderEntity order = orderMapper.selectById(taskId);
        if (order == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Order not found");
        }
        if (!OrderStatus.PENDING_OUTBOUND.name().equals(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_INVALID.getCode(),
                    ErrorCode.ORDER_STATUS_INVALID.getMessageZh());
        }

        // Check customs red-card sync status for WAREHOUSE_OUTBOUND node
        try {
            customsSyncService.syncNode(order.getId(), order.getOrderNo(), "WAREHOUSE_OUTBOUND", "RED");
        } catch (BusinessException e) {
            Map<String, Object> blocked = new HashMap<>();
            blocked.put("blocked", true);
            blocked.put("reason", "Customs sync failed: " + e.getMessage());
            blocked.put("orderId", order.getId());
            blocked.put("orderNo", order.getOrderNo());
            return blocked;
        }

        // Update order
        order.setWarehouseStatus(WAREHOUSE_STATUS_OUTBOUND);
        order.setOrderStatus(OrderStatus.OUTBOUND.name());
        orderMapper.updateById(order);

        // Reduce locked inventory, increase outbound
        List<OrderItemEntity> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, taskId)
        );
        for (OrderItemEntity item : orderItems) {
            List<InventoryEntity> inventories = inventoryMapper.selectList(
                    new LambdaQueryWrapper<InventoryEntity>()
                            .eq(InventoryEntity::getSkuCode, item.getSkuCode())
                            .gt(InventoryEntity::getLockedQty, 0)
                            .orderByAsc(InventoryEntity::getUpdatedAt)
            );
            int remaining = item.getQuantity();
            for (InventoryEntity inv : inventories) {
                if (remaining <= 0) break;
                int locked = Math.min(nullToZero(inv.getLockedQty()), remaining);
                inv.setLockedQty(nullToZero(inv.getLockedQty()) - locked);
                inv.setOutboundQty(nullToZero(inv.getOutboundQty()) + locked);
                inventoryMapper.updateById(inv);
                remaining -= locked;
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("blocked", false);
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("orderStatus", order.getOrderStatus());
        return result;
    }

    @Override
    public PageResult<Map<String, Object>> getInventory(int page, int pageSize) {
        int safePage = safePage(page);
        int safePageSize = safePageSize(pageSize);
        LambdaQueryWrapper<InventoryEntity> wrapper = new LambdaQueryWrapper<InventoryEntity>()
                .orderByDesc(InventoryEntity::getUpdatedAt);
        Page<InventoryEntity> pageResult = inventoryMapper.selectPage(new Page<>(safePage, safePageSize), wrapper);

        List<Map<String, Object>> items = new ArrayList<>();
        for (InventoryEntity inv : pageResult.getRecords()) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", inv.getId());
            map.put("warehouseId", inv.getWarehouseId());
            map.put("locationCode", inv.getLocationCode());
            map.put("skuCode", inv.getSkuCode());
            map.put("batchNo", inv.getBatchNo());
            map.put("availableQty", nullToZero(inv.getAvailableQty()));
            map.put("lockedQty", nullToZero(inv.getLockedQty()));
            map.put("outboundQty", nullToZero(inv.getOutboundQty()));
            map.put("updatedAt", inv.getUpdatedAt());
            items.add(map);
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    private Map<String, Object> toTaskMap(OrderEntity order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("orderStatus", order.getOrderStatus());
        map.put("warehouseStatus", order.getWarehouseStatus());
        map.put("deliveryStatus", order.getDeliveryStatus());
        map.put("consigneeName", order.getConsigneeName());
        map.put("cabinNo", order.getCabinNo());
        map.put("shipNo", order.getShipNo());
        map.put("shipName", order.getShipName());
        map.put("totalWeightKg", order.getTotalWeightKg() != null ? order.getTotalWeightKg().toPlainString() : "0");
        map.put("totalVolumeM3", order.getTotalVolumeM3() != null ? order.getTotalVolumeM3().toPlainString() : "0");
        map.put("createdAt", order.getCreatedAt());

        List<OrderItemEntity> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItemEntity>().eq(OrderItemEntity::getOrderId, order.getId())
        );
        List<Map<String, Object>> itemMaps = new ArrayList<>();
        for (OrderItemEntity item : items) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("skuCode", item.getSkuCode());
            itemMap.put("productNameZh", item.getProductNameZh());
            itemMap.put("productNameEn", item.getProductNameEn());
            itemMap.put("quantity", item.getQuantity());
            itemMaps.add(itemMap);
        }
        map.put("items", itemMaps);
        return map;
    }

    private String generatePackageNo() {
        int suffix = ThreadLocalRandom.current().nextInt(100, 1000);
        return "PK" + LocalDateTime.now().format(PKG_NO_TIME) + suffix;
    }

    private int safePage(int page) {
        return Math.max(page, 1);
    }

    private int safePageSize(int pageSize) {
        return Math.min(Math.max(pageSize, 1), 100);
    }

    private int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }
}
