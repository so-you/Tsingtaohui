package com.tsingtaohui.service;

import com.tsingtaohui.common.model.PageResult;

import java.util.Map;

public interface IWarehouseService {

    Map<String, Object> getDashboard();

    PageResult<Map<String, Object>> getPickingTasks(int page, int pageSize);

    void confirmPickingScan(Long taskId, String skuCode);

    PageResult<Map<String, Object>> getReviewTasks(int page, int pageSize);

    void scanProductForReview(Long taskId, String skuCode);

    String packOrder(Long taskId);

    PageResult<Map<String, Object>> getOutboundTasks(int page, int pageSize);

    Map<String, Object> confirmOutbound(Long taskId);

    PageResult<Map<String, Object>> getInventory(int page, int pageSize);
}
