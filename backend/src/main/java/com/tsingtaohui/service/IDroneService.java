package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.entity.DeliveryTaskEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public interface IDroneService {

    PageResult<Map<String, Object>> getDrones(int page, int pageSize);

    void addDrone(Map<String, Object> droneData);

    DeliveryTaskEntity matchAndCreateTask(Long orderId, String packageNo, BigDecimal weight, BigDecimal volume);

    void handleDroneCallback(String eventId, String taskNo, String status, String message, LocalDateTime eventTime);
}
