-- V20260528_004: Create inventory table

CREATE TABLE t_inventory (
    id              BIGINT          NOT NULL COMMENT '库存 ID',
    warehouse_id    BIGINT          NOT NULL COMMENT '仓库 ID',
    location_code   VARCHAR(64)     NULL COMMENT '库位编码',
    sku_code        VARCHAR(64)     NOT NULL COMMENT 'SKU 编码',
    batch_no        VARCHAR(64)     NULL COMMENT '批次号',
    available_qty   INT             NOT NULL DEFAULT 0 COMMENT '可用库存',
    locked_qty      INT             NOT NULL DEFAULT 0 COMMENT '锁定库存',
    outbound_qty    INT             NOT NULL DEFAULT 0 COMMENT '已出库数量',
    version         INT             NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      VARCHAR(64)     NULL COMMENT '创建人',
    updated_by      VARCHAR(64)     NULL COMMENT '更新人',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_inventory_stock_unit (warehouse_id, location_code, sku_code, batch_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存表';
