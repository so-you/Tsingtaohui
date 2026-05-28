package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.vo.AuditLogVO;

public interface IAuditLogService {

    PageResult<AuditLogVO> getAuditLogs(String module, Long actorId, String targetType,
                                         String targetId, String startTime, String endTime,
                                         int page, int pageSize);

    void log(String module, String action, String targetType, String targetId,
             String beforeValue, String afterValue, String clientIp, String userAgent);
}
