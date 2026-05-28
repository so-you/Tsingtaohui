package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.entity.CustomsSyncRecordEntity;

import java.util.Map;

public interface ICustomsSyncService {

    CustomsSyncRecordEntity syncNode(Long orderId, String orderNo, String syncNode, String syncLevel);

    PageResult<Map<String, Object>> getSyncRecords(int page, int pageSize);

    void retrySync(Long id);

    void retrySyncBySyncNo(String syncNo);
}
