-- V20260528_001: Create user, user_profile, user_ship tables

CREATE TABLE t_user (
    id              BIGINT          NOT NULL COMMENT '用户 ID',
    username        VARCHAR(64)     NOT NULL COMMENT '用户名，唯一',
    password_hash   VARCHAR(255)    NOT NULL COMMENT '密码哈希',
    user_type       VARCHAR(32)     NOT NULL COMMENT '用户类型: CUSTOMER, WAREHOUSE_OPERATOR, ADMIN, OPERATOR, DRONE_DISPATCHER, FINANCE',
    status          VARCHAR(32)     NOT NULL DEFAULT 'ENABLED' COMMENT '状态: ENABLED, DISABLED, LOCKED',
    preferred_language VARCHAR(16)  NOT NULL DEFAULT 'zh-CN' COMMENT '语言: zh-CN, en-US',
    last_login_at   DATETIME        NULL COMMENT '最近登录时间',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      VARCHAR(64)     NULL COMMENT '创建人',
    updated_by      VARCHAR(64)     NULL COMMENT '更新人',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_username (username),
    KEY idx_user_type_status (user_type, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

CREATE TABLE t_user_profile (
    id              BIGINT          NOT NULL COMMENT '资料 ID',
    user_id         BIGINT          NOT NULL COMMENT '用户 ID',
    display_name    VARCHAR(128)    NULL COMMENT '显示名称',
    contact_phone   VARCHAR(64)     NULL COMMENT '联系电话',
    email           VARCHAR(128)    NULL COMMENT '邮箱',
    nationality     VARCHAR(64)     NULL COMMENT '用户国籍',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      VARCHAR(64)     NULL COMMENT '创建人',
    updated_by      VARCHAR(64)     NULL COMMENT '更新人',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_profile_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户资料表';

CREATE TABLE t_user_ship (
    id                  BIGINT      NOT NULL COMMENT '用户船舶信息 ID',
    user_id             BIGINT      NOT NULL COMMENT '用户 ID',
    ship_no             VARCHAR(64) NOT NULL COMMENT '船号',
    ship_name           VARCHAR(128) NULL COMMENT '船名',
    ship_nationality    VARCHAR(64) NOT NULL COMMENT '船籍',
    imo                 VARCHAR(32) NULL COMMENT 'IMO 编码',
    mmsi                VARCHAR(32) NULL COMMENT 'MMSI 编码',
    is_default          TINYINT     NOT NULL DEFAULT 1 COMMENT '是否默认船舶',
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by          VARCHAR(64) NULL COMMENT '创建人',
    updated_by          VARCHAR(64) NULL COMMENT '更新人',
    deleted             TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    KEY idx_user_ship_user_id (user_id),
    KEY idx_user_ship_imo_mmsi (imo, mmsi)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户船舶信息表';
