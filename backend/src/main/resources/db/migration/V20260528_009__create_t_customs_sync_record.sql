-- V20260528_009: Create customs_sync_record table

CREATE TABLE t_customs_sync_record (
    id                  BIGINT          NOT NULL COMMENT '同步记录 ID',
    sync_no             VARCHAR(64)     NOT NULL COMMENT '同步编号，唯一',
    order_id            BIGINT          NOT NULL COMMENT '订单 ID',
    order_no            VARCHAR(64)     NOT NULL COMMENT '订单编号',
    sync_node           VARCHAR(64)     NOT NULL COMMENT '同步节点',
    sync_level          VARCHAR(16)     NOT NULL COMMENT '同步级别: RED, YELLOW',
    request_payload     MEDIUMTEXT      NULL COMMENT '请求报文',
    response_payload    MEDIUMTEXT      NULL COMMENT '响应报文',
    sync_status         VARCHAR(32)     NOT NULL DEFAULT 'SYNC_NONE' COMMENT '状态: SYNC_NONE, SYNCING, SYNC_SUCCESS, SYNC_FAILED, RETRYING, MANUAL_RESOLVED',
    failure_reason      VARCHAR(1024)   NULL COMMENT '失败原因',
    retry_count         INT             NOT NULL DEFAULT 0 COMMENT '重试次数',
    next_retry_at       DATETIME        NULL COMMENT '下次重试时间',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by          VARCHAR(64)     NULL COMMENT '创建人',
    updated_by          VARCHAR(64)     NULL COMMENT '更新人',
    deleted             TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_customs_sync_sync_no (sync_no),
    KEY idx_customs_sync_order_id (order_id),
    KEY idx_customs_sync_status (sync_status),
    KEY idx_customs_sync_node (sync_node)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='海关同步记录表';
