-- V20260528_008: Create delivery_task table

CREATE TABLE t_delivery_task (
    id                  BIGINT          NOT NULL COMMENT '配送任务 ID',
    task_no             VARCHAR(64)     NOT NULL COMMENT '任务编号，唯一',
    order_id            BIGINT          NOT NULL COMMENT '订单 ID',
    order_no            VARCHAR(64)     NOT NULL COMMENT '订单编号',
    package_no          VARCHAR(64)     NOT NULL COMMENT '包裹编号',
    warehouse_id        BIGINT          NOT NULL COMMENT '起点仓库',
    target_ship_no      VARCHAR(64)     NOT NULL COMMENT '目标船号',
    target_location     VARCHAR(255)    NOT NULL COMMENT '目标位置',
    drone_id            BIGINT          NULL COMMENT '无人机 ID',
    drone_code          VARCHAR(64)     NULL COMMENT '无人机编码',
    task_status         VARCHAR(32)     NOT NULL DEFAULT 'CREATED' COMMENT '状态: CREATED, DISPATCHED, IN_DELIVERY, DELIVERED, FAILED, CANCELLED',
    estimated_arrival   DATETIME        NULL COMMENT '预计送达',
    actual_arrival      DATETIME        NULL COMMENT '实际送达',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by          VARCHAR(64)     NULL COMMENT '创建人',
    updated_by          VARCHAR(64)     NULL COMMENT '更新人',
    deleted             TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_delivery_task_task_no (task_no),
    KEY idx_delivery_task_order_id (order_id),
    KEY idx_delivery_task_drone_id (drone_id),
    KEY idx_delivery_task_status (task_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='配送任务表';
