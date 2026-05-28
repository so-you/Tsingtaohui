-- V20260528_002: Create ship and shipping_agent tables

CREATE TABLE t_ship (
    id                  BIGINT      NOT NULL COMMENT '船舶 ID',
    ship_no             VARCHAR(64) NOT NULL COMMENT '船号',
    ship_name           VARCHAR(128) NULL COMMENT '船名',
    ship_nationality    VARCHAR(64) NOT NULL COMMENT '船籍',
    imo                 VARCHAR(32) NULL COMMENT 'IMO 编码',
    mmsi                VARCHAR(32) NULL COMMENT 'MMSI 编码',
    current_berth       VARCHAR(128) NULL COMMENT '当前泊位',
    current_anchorage   VARCHAR(128) NULL COMMENT '当前锚地',
    target_gps          VARCHAR(128) NULL COMMENT '目标 GPS',
    location_source     VARCHAR(32) NULL COMMENT '位置来源: USER_INPUT, ADMIN, SHIPXY, MARINE_TRAFFIC, QR_CODE',
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by          VARCHAR(64) NULL COMMENT '创建人',
    updated_by          VARCHAR(64) NULL COMMENT '更新人',
    deleted             TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    KEY idx_ship_imo (imo),
    KEY idx_ship_mmsi (mmsi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='船舶主数据表';

CREATE TABLE t_shipping_agent (
    id              BIGINT          NOT NULL COMMENT '船舶代理人 ID',
    agent_name_zh   VARCHAR(128)    NOT NULL COMMENT '中文名称',
    agent_name_en   VARCHAR(128)    NULL COMMENT '英文名称',
    contact_name    VARCHAR(128)    NULL COMMENT '联系人',
    contact_phone   VARCHAR(64)     NULL COMMENT '联系电话',
    status          VARCHAR(32)     NOT NULL DEFAULT 'ENABLED' COMMENT '状态: ENABLED, DISABLED',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      VARCHAR(64)     NULL COMMENT '创建人',
    updated_by      VARCHAR(64)     NULL COMMENT '更新人',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='船舶代理人表';
