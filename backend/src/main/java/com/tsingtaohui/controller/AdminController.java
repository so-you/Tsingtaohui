package com.tsingtaohui.controller;

import com.tsingtaohui.common.context.UserContextHolder;
import com.tsingtaohui.common.model.ApiResponse;
import com.tsingtaohui.common.model.PageResult;
import com.tsingtaohui.model.dto.CreateShipDTO;
import com.tsingtaohui.model.dto.UpdateProductDTO;
import com.tsingtaohui.model.dto.UpdateRuleDTO;
import com.tsingtaohui.model.dto.UpdateStatusDTO;
import com.tsingtaohui.model.vo.AdminInventoryVO;
import com.tsingtaohui.model.vo.AdminProductVO;
import com.tsingtaohui.model.vo.AdminProfileVO;
import com.tsingtaohui.model.vo.AdminUserVO;
import com.tsingtaohui.model.vo.AuditLogVO;
import com.tsingtaohui.model.vo.OrderVO;
import com.tsingtaohui.model.vo.RuleConfigVO;
import com.tsingtaohui.model.vo.ShipAdminVO;
import com.tsingtaohui.model.vo.ShippingAgentVO;
import com.tsingtaohui.service.IAdminService;
import com.tsingtaohui.service.IAuditLogService;
import com.tsingtaohui.service.IOrderService;
import com.tsingtaohui.service.IReconciliationService;
import com.tsingtaohui.service.IRuleConfigService;
import com.tsingtaohui.service.IShipService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
public class AdminController {

    private final IAdminService adminService;
    private final IOrderService orderService;
    private final IShipService shipService;
    private final IRuleConfigService ruleConfigService;
    private final IAuditLogService auditLogService;
    private final IReconciliationService reconciliationService;

    public AdminController(IAdminService adminService,
                           IOrderService orderService,
                           IShipService shipService,
                           IRuleConfigService ruleConfigService,
                           IAuditLogService auditLogService,
                           IReconciliationService reconciliationService) {
        this.adminService = adminService;
        this.orderService = orderService;
        this.shipService = shipService;
        this.ruleConfigService = ruleConfigService;
        this.auditLogService = auditLogService;
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/profile")
    public ApiResponse<AdminProfileVO> getProfile() {
        return ApiResponse.ok(adminService.getProfile(UserContextHolder.getUserId()));
    }

    @GetMapping("/users")
    public ApiResponse<PageResult<AdminUserVO>> getUsers(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "user_type", required = false) String userType,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(adminService.getUsers(keyword, userType, status, page, pageSize));
    }

    @PatchMapping("/users/{userId}/status")
    public ApiResponse<AdminUserVO> updateUserStatus(@PathVariable Long userId,
                                                     @Valid @RequestBody UpdateStatusDTO dto) {
        return ApiResponse.ok(adminService.updateUserStatus(userId, dto.getStatus()));
    }

    @GetMapping("/products")
    public ApiResponse<PageResult<AdminProductVO>> getProducts(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category_id", required = false) Long categoryId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(adminService.getProducts(keyword, categoryId, status, page, pageSize));
    }

    @PatchMapping("/products/{productId}/status")
    public ApiResponse<AdminProductVO> updateProductStatus(@PathVariable Long productId,
                                                           @Valid @RequestBody UpdateStatusDTO dto) {
        return ApiResponse.ok(adminService.updateProductStatus(productId, dto.getStatus()));
    }

    @PutMapping("/products/{productId}")
    public ApiResponse<AdminProductVO> updateProduct(@PathVariable Long productId,
                                                     @Valid @RequestBody UpdateProductDTO dto) {
        return ApiResponse.ok(adminService.updateProduct(productId, dto));
    }

    @GetMapping("/inventory")
    public ApiResponse<PageResult<AdminInventoryVO>> getInventory(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "warehouse_id", required = false) Long warehouseId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(adminService.getInventory(keyword, warehouseId, page, pageSize));
    }

    @GetMapping("/orders")
    public ApiResponse<PageResult<OrderVO>> getOrders(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "trade_mode", required = false) String tradeMode,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(orderService.getAdminOrders(keyword, status, tradeMode, page, pageSize));
    }

    @GetMapping("/matching-orders")
    public ApiResponse<PageResult<OrderVO>> getMatchingOrders(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(orderService.getAdminMatchingOrders(keyword, status, page, pageSize));
    }

    @GetMapping("/orders/{orderId}")
    public ApiResponse<OrderVO> getOrderDetail(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.getAdminOrderDetail(orderId));
    }

    @PatchMapping("/orders/{orderId}/status")
    public ApiResponse<OrderVO> updateOrderStatus(@PathVariable Long orderId,
                                                  @Valid @RequestBody UpdateStatusDTO dto) {
        return ApiResponse.ok(orderService.updateAdminOrderStatus(orderId, dto.getStatus()));
    }

    @GetMapping("/ships")
    public ApiResponse<PageResult<ShipAdminVO>> getShips(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "nationality", required = false) String nationality,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(shipService.getShips(keyword, nationality, page, pageSize));
    }

    @PostMapping("/ships")
    public ApiResponse<ShipAdminVO> createShip(@Valid @RequestBody CreateShipDTO dto) {
        return ApiResponse.ok(shipService.createShip(dto));
    }

    @PutMapping("/ships/{shipId}")
    public ApiResponse<ShipAdminVO> updateShip(@PathVariable Long shipId,
                                               @Valid @RequestBody CreateShipDTO dto) {
        return ApiResponse.ok(shipService.updateShip(shipId, dto));
    }

    @GetMapping("/shipping-agents")
    public ApiResponse<PageResult<ShippingAgentVO>> getShippingAgents(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(shipService.getShippingAgents(keyword, status, page, pageSize));
    }

    @GetMapping("/rules")
    public ApiResponse<PageResult<RuleConfigVO>> getRules(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(ruleConfigService.getRules(keyword, status, page, pageSize));
    }

    @PutMapping("/rules/{ruleId}")
    public ApiResponse<RuleConfigVO> updateRule(@PathVariable Long ruleId,
                                                @Valid @RequestBody UpdateRuleDTO dto) {
        return ApiResponse.ok(ruleConfigService.updateRule(ruleId, dto));
    }

    @GetMapping("/audit-logs")
    public ApiResponse<PageResult<AuditLogVO>> getAuditLogs(
            @RequestParam(value = "module", required = false) String module,
            @RequestParam(value = "actor_id", required = false) Long actorId,
            @RequestParam(value = "target_type", required = false) String targetType,
            @RequestParam(value = "target_id", required = false) String targetId,
            @RequestParam(value = "start_time", required = false) String startTime,
            @RequestParam(value = "end_time", required = false) String endTime,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "page_size", defaultValue = "20") int pageSize) {
        return ApiResponse.ok(auditLogService.getAuditLogs(module, actorId, targetType,
                targetId, startTime, endTime, page, pageSize));
    }

    @GetMapping("/reconciliation/export")
    public void exportReconciliation(
            @RequestParam(value = "start_date", required = false) String startDate,
            @RequestParam(value = "end_date", required = false) String endDate,
            HttpServletResponse response) throws IOException {
        byte[] csvBytes = reconciliationService.exportCsv(startDate, endDate);
        String filename = "reconciliation_" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + ".csv";
        response.setContentType("text/csv;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentLength(csvBytes.length);
        response.getOutputStream().write(csvBytes);
        response.getOutputStream().flush();
    }
}
