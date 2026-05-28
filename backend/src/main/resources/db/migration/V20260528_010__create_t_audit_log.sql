-- V20260528_010: Create audit_log table

CREATE TABLE t_audit_log (
    id              BIGINT          NOT NULL COMMENT '日志 ID',
    actor_id        BIGINT          NULL COMMENT '操作人 ID',
    actor_name      VARCHAR(128)    NULL COMMENT '操作人',
    module          VARCHAR(64)     NOT NULL COMMENT '模块',
    action          VARCHAR(64)     NOT NULL COMMENT '操作',
    target_type     VARCHAR(64)     NOT NULL COMMENT '对象类型',
    target_id       VARCHAR(64)     NOT NULL COMMENT '对象 ID',
    before_value    MEDIUMTEXT      NULL COMMENT '变更前',
    after_value     MEDIUMTEXT      NULL COMMENT '变更后',
    client_ip       VARCHAR(64)     NULL COMMENT 'IP',
    user_agent      VARCHAR(512)    NULL COMMENT 'User-Agent',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_audit_log_module (module),
    KEY idx_audit_log_actor_id (actor_id),
    KEY idx_audit_log_target (target_type, target_id),
    KEY idx_audit_log_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志表';
