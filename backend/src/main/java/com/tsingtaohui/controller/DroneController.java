package com.tsingtaohui.controller;

import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.service.IDroneService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class DroneController {

    private final IDroneService droneService;

    public DroneController(IDroneService droneService) {
        this.droneService = droneService;
    }

    @GetMapping("/admin/drones")
    public ApiResponse<PageResult<Map<String, Object>>> getDrones(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(droneService.getDrones(page, pageSize));
    }

    @PostMapping("/admin/drones")
    public ApiResponse<Void> addDrone(@RequestBody Map<String, Object> droneData) {
        droneService.addDrone(droneData);
        return ApiResponse.ok(null);
    }

    @PostMapping("/integrations/drone/callback")
    public ApiResponse<Void> handleDroneCallback(@RequestBody Map<String, Object> body) {
        String eventId = (String) body.get("eventId");
        String taskNo = (String) body.get("taskNo");
        String status = (String) body.get("status");
        String message = (String) body.get("message");
        LocalDateTime eventTime = null;
        if (body.get("eventTime") != null) {
            eventTime = LocalDateTime.parse(body.get("eventTime").toString());
        }
        droneService.handleDroneCallback(eventId, taskNo, status, message, eventTime);
        return ApiResponse.ok(null);
    }
}
