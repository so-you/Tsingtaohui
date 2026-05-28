package com.tsingtaohui.common.enums;

public enum ErrorCode {

    SUCCESS("0", "ok", "ok"),

    AUTH_INVALID_CREDENTIALS("AUTH_001", "用户名或密码错误", "Invalid username or password"),
    AUTH_SESSION_EXPIRED("AUTH_002", "登录状态失效", "Session expired"),
    USER_USERNAME_EXISTS("USER_001", "用户名已存在", "Username already exists"),
    ORDER_CART_EMPTY("ORDER_001", "购物车为空", "Cart is empty"),
    ORDER_STOCK_INSUFFICIENT("ORDER_002", "库存不足", "Insufficient stock"),
    ORDER_STATUS_INVALID("ORDER_003", "订单状态不允许当前操作", "Order status does not allow this operation"),
    CUSTOMS_RED_CARD_FAILED("CUSTOMS_001", "海关红牌节点同步失败", "Customs red-card sync failed"),
    DRONE_UNAVAILABLE("DRONE_001", "无可用无人机", "No available drones"),
    WAREHOUSE_SCAN_MISMATCH("WAREHOUSE_001", "扫码结果不匹配", "Scan result mismatch"),

    SHIP_NOT_FOUND("SHIP_001", "船舶不存在", "Ship not found"),
    RULE_NOT_FOUND("RULE_001", "规则不存在", "Rule not found"),
    RULE_STATUS_INVALID("RULE_002", "无效的规则状态", "Invalid rule status"),

    VALIDATION_ERROR("VALIDATION_ERROR", "参数校验失败", "Validation failed"),
    INTERNAL_ERROR("INTERNAL_ERROR", "服务器内部错误", "Internal server error");

    private final String code;
    private final String messageZh;
    private final String messageEn;

    ErrorCode(String code, String messageZh, String messageEn) {
        this.code = code;
        this.messageZh = messageZh;
        this.messageEn = messageEn;
    }

    public String getCode() {
        return code;
    }

    public String getMessageZh() {
        return messageZh;
    }

    public String getMessageEn() {
        return messageEn;
    }

    public String getMessage(String language) {
        if ("en-US".equalsIgnoreCase(language) || "en".equalsIgnoreCase(language)) {
            return messageEn;
        }
        return messageZh;
    }
}
