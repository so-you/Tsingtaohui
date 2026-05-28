package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.enums.OrderStatus;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.DeliveryTaskMapper;
import com.tsingtaohui.mapper.DroneMapper;
import com.tsingtaohui.mapper.OrderMapper;
import com.tsingtaohui.model.entity.DeliveryTaskEntity;
import com.tsingtaohui.model.entity.DroneEntity;
import com.tsingtaohui.model.entity.OrderEntity;
import com.tsingtaohui.service.IDroneService;
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
public class DroneServiceImpl implements IDroneService {

    private static final DateTimeFormatter TASK_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final String DRONE_STATUS_AVAILABLE = "AVAILABLE";
    private static final String DRONE_STATUS_DISPATCHED = "DISPATCHED";
    private static final String TASK_STATUS_DISPATCHED = "DISPATCHED";

    private final DroneMapper droneMapper;
    private final DeliveryTaskMapper deliveryTaskMapper;
    private final OrderMapper orderMapper;

    public DroneServiceImpl(DroneMapper droneMapper,
                            DeliveryTaskMapper deliveryTaskMapper,
                            OrderMapper orderMapper) {
        this.droneMapper = droneMapper;
        this.deliveryTaskMapper = deliveryTaskMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public PageResult<Map<String, Object>> getDrones(int page, int pageSize) {
        int safePage = safePage(page);
        int safePageSize = safePageSize(pageSize);
        LambdaQueryWrapper<DroneEntity> wrapper = new LambdaQueryWrapper<DroneEntity>()
                .orderByDesc(DroneEntity::getCreatedAt);
        Page<DroneEntity> pageResult = droneMapper.selectPage(new Page<>(safePage, safePageSize), wrapper);

        List<Map<String, Object>> items = new ArrayList<>();
        for (DroneEntity drone : pageResult.getRecords()) {
            items.add(toDroneMap(drone));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    @Override
    public void addDrone(Map<String, Object> droneData) {
        DroneEntity drone = new DroneEntity();
        drone.setDroneCode((String) droneData.get("droneCode"));
        drone.setModel((String) droneData.get("model"));
        drone.setFlightNo((String) droneData.get("flightNo"));
        if (droneData.get("maxPayloadKg") != null) {
            drone.setMaxPayloadKg(new BigDecimal(droneData.get("maxPayloadKg").toString()));
        }
        if (droneData.get("maxVolumeM3") != null) {
            drone.setMaxVolumeM3(new BigDecimal(droneData.get("maxVolumeM3").toString()));
        }
        if (droneData.get("maxRangeKm") != null) {
            drone.setMaxRangeKm(new BigDecimal(droneData.get("maxRangeKm").toString()));
        }
        drone.setDeliverableCategories((String) droneData.get("deliverableCategories"));
        drone.setStatus(DRONE_STATUS_AVAILABLE);
        droneMapper.insert(drone);
    }

    @Override
    @Transactional
    public DeliveryTaskEntity matchAndCreateTask(Long orderId, String packageNo, BigDecimal weight, BigDecimal volume) {
        // Find an available drone that can handle the payload
        LambdaQueryWrapper<DroneEntity> droneWrapper = new LambdaQueryWrapper<DroneEntity>()
                .eq(DroneEntity::getStatus, DRONE_STATUS_AVAILABLE)
                .ge(DroneEntity::getMaxPayloadKg, weight)
                .ge(DroneEntity::getMaxVolumeM3, volume)
                .last("limit 1");
        DroneEntity drone = droneMapper.selectOne(droneWrapper);

        if (drone == null) {
            throw new BusinessException(ErrorCode.DRONE_UNAVAILABLE.getCode(),
                    ErrorCode.DRONE_UNAVAILABLE.getMessageZh());
        }

        // Mark drone as dispatched
        drone.setStatus(DRONE_STATUS_DISPATCHED);
        droneMapper.updateById(drone);

        // Load order info for ship details
        OrderEntity order = orderMapper.selectById(orderId);

        // Create delivery task
        DeliveryTaskEntity task = new DeliveryTaskEntity();
        task.setTaskNo(generateTaskNo());
        task.setOrderId(orderId);
        task.setOrderNo(order != null ? order.getOrderNo() : null);
        task.setPackageNo(packageNo);
        task.setTargetShipNo(order != null ? order.getShipNo() : null);
        task.setTargetLocation(order != null ? order.getBerthOrAnchorage() : null);
        task.setDroneId(drone.getId());
        task.setDroneCode(drone.getDroneCode());
        task.setTaskStatus(TASK_STATUS_DISPATCHED);
        task.setEstimatedArrival(LocalDateTime.now().plusMinutes(30));
        deliveryTaskMapper.insert(task);

        // Update order delivery status
        if (order != null) {
            order.setOrderStatus(OrderStatus.PENDING_LOADING.name());
            orderMapper.updateById(order);
        }

        return task;
    }

    @Override
    @Transactional
    public void handleDroneCallback(String eventId, String taskNo, String status, String message, LocalDateTime eventTime) {
        // Idempotent: check if task already in target status
        DeliveryTaskEntity task = deliveryTaskMapper.selectOne(
                new LambdaQueryWrapper<DeliveryTaskEntity>().eq(DeliveryTaskEntity::getTaskNo, taskNo)
        );
        if (task == null) {
            return; // Unknown task, ignore
        }

        // Already processed to same or terminal status
        if (status.equals(task.getTaskStatus())) {
            return;
        }

        // Update task status
        task.setTaskStatus(status);
        if ("DELIVERED".equals(status)) {
            task.setActualArrival(eventTime != null ? eventTime : LocalDateTime.now());
        }
        deliveryTaskMapper.updateById(task);

        // Map drone status to order status
        OrderEntity order = orderMapper.selectById(task.getOrderId());
        if (order == null) {
            return;
        }

        switch (status) {
            case "LOADED":
                order.setOrderStatus(OrderStatus.IN_DELIVERY.name());
                order.setDeliveryStatus("IN_DELIVERY");
                break;
            case "IN_FLIGHT":
                order.setDeliveryStatus("IN_FLIGHT");
                break;
            case "DELIVERED":
                order.setOrderStatus(OrderStatus.PENDING_RECEIPT.name());
                order.setDeliveryStatus("DELIVERED");
                break;
            case "FAILED":
                order.setOrderStatus(OrderStatus.EXCEPTION.name());
                order.setDeliveryStatus("FAILED");
                break;
            default:
                break;
        }
        orderMapper.updateById(order);

        // If delivered or failed, free up the drone
        if ("DELIVERED".equals(status) || "FAILED".equals(status)) {
            if (task.getDroneId() != null) {
                DroneEntity drone = droneMapper.selectById(task.getDroneId());
                if (drone != null) {
                    drone.setStatus(DRONE_STATUS_AVAILABLE);
                    droneMapper.updateById(drone);
                }
            }
        }
    }

    private Map<String, Object> toDroneMap(DroneEntity drone) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", drone.getId());
        map.put("droneCode", drone.getDroneCode());
        map.put("model", drone.getModel());
        map.put("flightNo", drone.getFlightNo());
        map.put("maxPayloadKg", drone.getMaxPayloadKg() != null ? drone.getMaxPayloadKg().toPlainString() : null);
        map.put("maxVolumeM3", drone.getMaxVolumeM3() != null ? drone.getMaxVolumeM3().toPlainString() : null);
        map.put("maxRangeKm", drone.getMaxRangeKm() != null ? drone.getMaxRangeKm().toPlainString() : null);
        map.put("deliverableCategories", drone.getDeliverableCategories());
        map.put("status", drone.getStatus());
        map.put("createdAt", drone.getCreatedAt());
        return map;
    }

    private String generateTaskNo() {
        int suffix = ThreadLocalRandom.current().nextInt(100, 1000);
        return "DT" + LocalDateTime.now().format(TASK_NO_TIME) + suffix;
    }

    private int safePage(int page) {
        return Math.max(page, 1);
    }

    private int safePageSize(int pageSize) {
        return Math.min(Math.max(pageSize, 1), 100);
    }
}
