-- V20260528_011: Create rule_config table

CREATE TABLE t_rule_config (
    id              BIGINT          NOT NULL COMMENT '规则 ID',
    rule_key        VARCHAR(128)    NOT NULL COMMENT '规则键（唯一业务标识）',
    rule_name_zh    VARCHAR(128)    NOT NULL COMMENT '规则中文名称',
    rule_name_en    VARCHAR(128)    NULL COMMENT '规则英文名称',
    rule_value      VARCHAR(1024)   NOT NULL COMMENT '规则值（JSON 或纯文本）',
    description     VARCHAR(512)    NULL COMMENT '规则说明',
    status          VARCHAR(32)     NOT NULL DEFAULT 'ENABLED' COMMENT '状态: ENABLED, DISABLED',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      VARCHAR(64)     NULL COMMENT '创建人',
    updated_by      VARCHAR(64)     NULL COMMENT '更新人',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_rule_config_rule_key (rule_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='规则配置表';
