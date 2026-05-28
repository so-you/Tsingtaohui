package com.tsingtaohui.controller;

import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.service.ICustomsSyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/customs-sync-records")
public class CustomsController {

    private final ICustomsSyncService customsSyncService;

    public CustomsController(ICustomsSyncService customsSyncService) {
        this.customsSyncService = customsSyncService;
    }

    @GetMapping
    public ApiResponse<PageResult<Map<String, Object>>> getSyncRecords(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(customsSyncService.getSyncRecords(page, pageSize));
    }

    @PostMapping("/{syncNo}/retry")
    public ApiResponse<Void> retrySync(@PathVariable String syncNo) {
        customsSyncService.retrySyncBySyncNo(syncNo);
        return ApiResponse.ok(null);
    }
}
