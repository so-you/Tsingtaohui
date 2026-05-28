-- V20260528_007: Create drone table

CREATE TABLE t_drone (
    id                      BIGINT          NOT NULL COMMENT '无人机 ID',
    drone_code              VARCHAR(64)     NOT NULL COMMENT '无人机编码',
    model                   VARCHAR(128)    NOT NULL COMMENT '型号',
    flight_no               VARCHAR(64)     NOT NULL COMMENT '架次编号',
    max_payload_kg          DECIMAL(10, 3)  NOT NULL COMMENT '核载质量(kg)',
    max_volume_m3           DECIMAL(10, 4)  NOT NULL COMMENT '可载体积(m³)',
    max_range_km            DECIMAL(10, 2)  NOT NULL COMMENT '可飞距离(km)',
    deliverable_categories  VARCHAR(512)    NULL COMMENT '可配送品类',
    status                  VARCHAR(32)     NOT NULL DEFAULT 'AVAILABLE' COMMENT '状态: AVAILABLE, IN_MISSION, MAINTENANCE, DISABLED',
    created_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by              VARCHAR(64)     NULL COMMENT '创建人',
    updated_by              VARCHAR(64)     NULL COMMENT '更新人',
    deleted                 TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_drone_drone_code (drone_code),
    KEY idx_drone_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='无人机表';
