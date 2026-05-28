package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.enums.CustomsSyncStatus;
import com.tsingtaohui.common.enums.ErrorCode;
import com.tsingtaohui.common.exception.BusinessException;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.mapper.CustomsSyncRecordMapper;
import com.tsingtaohui.model.entity.CustomsSyncRecordEntity;
import com.tsingtaohui.service.ICustomsSyncService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CustomsSyncServiceImpl implements ICustomsSyncService {

    private static final DateTimeFormatter SYNC_NO_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");

    private final CustomsSyncRecordMapper customsSyncRecordMapper;

    public CustomsSyncServiceImpl(CustomsSyncRecordMapper customsSyncRecordMapper) {
        this.customsSyncRecordMapper = customsSyncRecordMapper;
    }

    @Override
    public CustomsSyncRecordEntity syncNode(Long orderId, String orderNo, String syncNode, String syncLevel) {
        // Create sync record
        CustomsSyncRecordEntity record = new CustomsSyncRecordEntity();
        record.setSyncNo(generateSyncNo());
        record.setOrderId(orderId);
        record.setOrderNo(orderNo);
        record.setSyncNode(syncNode);
        record.setSyncLevel(syncLevel);
        record.setRequestPayload("{\"orderId\":" + orderId + ",\"orderNo\":\"" + orderNo + "\",\"node\":\"" + syncNode + "\"}");
        record.setSyncStatus(CustomsSyncStatus.SYNCING.name());
        record.setRetryCount(0);
        customsSyncRecordMapper.insert(record);

        // MVP: simulate sync success
        record.setSyncStatus(CustomsSyncStatus.SYNC_SUCCESS.name());
        record.setResponsePayload("{\"status\":\"SUCCESS\",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
        customsSyncRecordMapper.updateById(record);

        return record;
    }

    @Override
    public PageResult<Map<String, Object>> getSyncRecords(int page, int pageSize) {
        int safePage = safePage(page);
        int safePageSize = safePageSize(pageSize);
        LambdaQueryWrapper<CustomsSyncRecordEntity> wrapper = new LambdaQueryWrapper<CustomsSyncRecordEntity>()
                .orderByDesc(CustomsSyncRecordEntity::getCreatedAt);
        Page<CustomsSyncRecordEntity> pageResult = customsSyncRecordMapper.selectPage(
                new Page<>(safePage, safePageSize), wrapper
        );

        List<Map<String, Object>> items = new ArrayList<>();
        for (CustomsSyncRecordEntity record : pageResult.getRecords()) {
            items.add(toSyncRecordMap(record));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    @Override
    public void retrySync(Long id) {
        CustomsSyncRecordEntity record = customsSyncRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Sync record not found");
        }

        if (!CustomsSyncStatus.SYNC_FAILED.name().equals(record.getSyncStatus())
                && !CustomsSyncStatus.RETRYING.name().equals(record.getSyncStatus())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(),
                    "Only failed records can be retried");
        }

        // MVP: simulate retry success
        record.setSyncStatus(CustomsSyncStatus.SYNC_SUCCESS.name());
        record.setRetryCount(nullToZero(record.getRetryCount()) + 1);
        record.setResponsePayload("{\"status\":\"SUCCESS\",\"retry\":true,\"timestamp\":\"" + LocalDateTime.now() + "\"}");
        record.setFailureReason(null);
        customsSyncRecordMapper.updateById(record);
    }

    private Map<String, Object> toSyncRecordMap(CustomsSyncRecordEntity record) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", record.getId());
        map.put("syncNo", record.getSyncNo());
        map.put("orderId", record.getOrderId());
        map.put("orderNo", record.getOrderNo());
        map.put("syncNode", record.getSyncNode());
        map.put("nodeType", record.getSyncNode());
        map.put("syncLevel", record.getSyncLevel());
        map.put("level", record.getSyncLevel());
        map.put("syncStatus", record.getSyncStatus());
        map.put("status", record.getSyncStatus());
        map.put("failureReason", record.getFailureReason());
        map.put("retryCount", record.getRetryCount());
        map.put("nextRetryAt", record.getNextRetryAt());
        map.put("createdAt", record.getCreatedAt());
        map.put("updatedAt", record.getUpdatedAt());
        return map;
    }

    private String generateSyncNo() {
        int suffix = ThreadLocalRandom.current().nextInt(100, 1000);
        return "CS" + LocalDateTime.now().format(SYNC_NO_TIME) + suffix;
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

    @Override
    public void retrySyncBySyncNo(String syncNo) {
        CustomsSyncRecordEntity record = customsSyncRecordMapper.selectOne(
                new LambdaQueryWrapper<CustomsSyncRecordEntity>().eq(CustomsSyncRecordEntity::getSyncNo, syncNo)
        );
        if (record == null) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR.getCode(), "Sync record not found: " + syncNo);
        }
        retrySync(record.getId());
    }
}
