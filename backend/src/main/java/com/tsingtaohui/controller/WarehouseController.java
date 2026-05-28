package com.tsingtaohui.controller;

import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.service.IWarehouseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final IWarehouseService warehouseService;

    public WarehouseController(IWarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> getDashboard() {
        return ApiResponse.ok(warehouseService.getDashboard());
    }

    @GetMapping("/picking-tasks")
    public ApiResponse<PageResult<Map<String, Object>>> getPickingTasks(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(warehouseService.getPickingTasks(page, pageSize));
    }

    @PostMapping("/picking-tasks/{taskId}/scan")
    public ApiResponse<Void> confirmPickingScan(@PathVariable Long taskId,
                                                 @RequestBody Map<String, String> body) {
        warehouseService.confirmPickingScan(taskId, body.get("skuCode"));
        return ApiResponse.ok(null);
    }

    @GetMapping("/review-tasks")
    public ApiResponse<PageResult<Map<String, Object>>> getReviewTasks(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(warehouseService.getReviewTasks(page, pageSize));
    }

    @PostMapping("/review-tasks/{taskId}/scan-product")
    public ApiResponse<Void> scanProductForReview(@PathVariable Long taskId,
                                                   @RequestBody Map<String, String> body) {
        warehouseService.scanProductForReview(taskId, body.get("skuCode"));
        return ApiResponse.ok(null);
    }

    @PostMapping("/review-tasks/{taskId}/pack")
    public ApiResponse<Map<String, Object>> packOrder(@PathVariable Long taskId) {
        String packageNo = warehouseService.packOrder(taskId);
        return ApiResponse.ok(Map.of("packageNo", packageNo));
    }

    @GetMapping("/outbound-tasks")
    public ApiResponse<PageResult<Map<String, Object>>> getOutboundTasks(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(warehouseService.getOutboundTasks(page, pageSize));
    }

    @PostMapping("/outbound-tasks/{taskId}/confirm")
    public ApiResponse<Map<String, Object>> confirmOutbound(@PathVariable Long taskId) {
        return ApiResponse.ok(warehouseService.confirmOutbound(taskId));
    }

    @GetMapping("/inventory")
    public ApiResponse<PageResult<Map<String, Object>>> getInventory(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(warehouseService.getInventory(page, pageSize));
    }
}
