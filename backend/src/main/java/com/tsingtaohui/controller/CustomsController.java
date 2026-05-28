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
        // For simplicity, parse syncNo as a lookup key. In practice, might use the syncNo field.
        // The service expects a database ID, but the API path uses syncNo.
        // We'll accept the ID via syncNo parameter for MVP.
        Long id;
        try {
            id = Long.parseLong(syncNo);
        } catch (NumberFormatException e) {
            // If not a numeric ID, try to find by syncNo field - but for MVP simplicity,
            // we'll just return an error
            return ApiResponse.error("VALIDATION_ERROR", "Invalid sync record ID");
        }
        customsSyncService.retrySync(id);
        return ApiResponse.ok(null);
    }
}
