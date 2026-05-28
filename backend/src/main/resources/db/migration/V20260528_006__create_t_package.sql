-- V20260528_006: Create package table

CREATE TABLE t_package (
    id                  BIGINT          NOT NULL COMMENT '包裹 ID',
    package_no          VARCHAR(64)     NOT NULL COMMENT '包裹编号，唯一',
    order_id            BIGINT          NOT NULL COMMENT '订单 ID',
    order_no            VARCHAR(64)     NOT NULL COMMENT '订单编号',
    actual_weight_kg    DECIMAL(10, 3)  NULL COMMENT '实际重量(kg)',
    actual_volume_m3    DECIMAL(10, 4)  NULL COMMENT '实际体积(m³)',
    package_status      VARCHAR(32)     NOT NULL DEFAULT 'CREATED' COMMENT '状态: CREATED, REVIEWED, OUTBOUND, DELIVERED',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by          VARCHAR(64)     NULL COMMENT '创建人',
    updated_by          VARCHAR(64)     NULL COMMENT '更新人',
    deleted             TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_package_package_no (package_no),
    KEY idx_package_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='包裹表';
