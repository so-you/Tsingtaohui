package com.tsingtaohui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tsingtaohui.common.context.UserContextHolder;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.common.model.UserContext;
import com.tsingtaohui.mapper.AuditLogMapper;
import com.tsingtaohui.model.entity.AuditLogEntity;
import com.tsingtaohui.model.vo.AuditLogVO;
import com.tsingtaohui.service.IAuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AuditLogServiceImpl implements IAuditLogService {

    private final AuditLogMapper auditLogMapper;

    public AuditLogServiceImpl(AuditLogMapper auditLogMapper) {
        this.auditLogMapper = auditLogMapper;
    }

    @Override
    public PageResult<AuditLogVO> getAuditLogs(String module, Long actorId, String targetType,
                                                String targetId, String startTime, String endTime,
                                                int page, int pageSize) {
        LambdaQueryWrapper<AuditLogEntity> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            wrapper.eq(AuditLogEntity::getModule, module.trim());
        }
        if (actorId != null) {
            wrapper.eq(AuditLogEntity::getActorId, actorId);
        }
        if (StringUtils.hasText(targetType)) {
            wrapper.eq(AuditLogEntity::getTargetType, targetType.trim());
        }
        if (StringUtils.hasText(targetId)) {
            wrapper.eq(AuditLogEntity::getTargetId, targetId.trim());
        }
        if (StringUtils.hasText(startTime)) {
            LocalDateTime start = LocalDate.parse(startTime.trim()).atStartOfDay();
            wrapper.ge(AuditLogEntity::getCreatedAt, start);
        }
        if (StringUtils.hasText(endTime)) {
            LocalDateTime end = LocalDate.parse(endTime.trim()).atTime(LocalTime.MAX);
            wrapper.le(AuditLogEntity::getCreatedAt, end);
        }
        wrapper.orderByDesc(AuditLogEntity::getCreatedAt);

        int safePage = Math.max(page, 1);
        int safePageSize = Math.min(Math.max(pageSize, 1), 100);
        Page<AuditLogEntity> pageResult = auditLogMapper.selectPage(
                new Page<>(safePage, safePageSize), wrapper);

        List<AuditLogVO> items = new ArrayList<>();
        for (AuditLogEntity entity : pageResult.getRecords()) {
            items.add(toAuditLogVO(entity));
        }
        return new PageResult<>(items, safePage, safePageSize, pageResult.getTotal());
    }

    @Override
    public void log(String module, String action, String targetType, String targetId,
                    String beforeValue, String afterValue, String clientIp, String userAgent) {
        AuditLogEntity entity = new AuditLogEntity();
        UserContext ctx = UserContextHolder.get();
        entity.setActorId(ctx != null ? ctx.getUserId() : null);
        entity.setActorName(ctx != null ? ctx.getUsername() : "system");
        entity.setModule(module);
        entity.setAction(action);
        entity.setTargetType(targetType);
        entity.setTargetId(targetId);
        entity.setBeforeValue(beforeValue);
        entity.setAfterValue(afterValue);
        entity.setClientIp(clientIp);
        entity.setUserAgent(userAgent);
        auditLogMapper.insert(entity);
    }

    private AuditLogVO toAuditLogVO(AuditLogEntity entity) {
        AuditLogVO vo = new AuditLogVO();
        vo.setId(entity.getId());
        vo.setActorId(entity.getActorId());
        vo.setActorName(entity.getActorName());
        vo.setModule(entity.getModule());
        vo.setAction(entity.getAction());
        vo.setTargetType(entity.getTargetType());
        vo.setTargetId(entity.getTargetId());
        vo.setBeforeValue(entity.getBeforeValue());
        vo.setAfterValue(entity.getAfterValue());
        vo.setClientIp(entity.getClientIp());
        vo.setUserAgent(entity.getUserAgent());
        vo.setCreatedAt(entity.getCreatedAt());
        return vo;
    }
}
