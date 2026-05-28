package com.tsingtaohui.common.enums;

public enum CustomsSyncNode {
    ORDER_CREATED("RED"),
    ORDER_CONFIRMED("YELLOW"),
    WAREHOUSE_OUTBOUND("RED"),
    DELIVERY_TASK_CREATED("YELLOW"),
    DRONE_LOADED("YELLOW"),
    IN_DELIVERY("YELLOW"),
    DELIVERED("YELLOW"),
    RECEIPT_CONFIRMED("YELLOW"),
    ORDER_CANCELLED("YELLOW"),
    ORDER_EXCEPTION("YELLOW");

    private final String level;

    CustomsSyncNode(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
}
