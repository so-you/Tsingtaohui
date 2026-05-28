-- V20260528_003: Create product_category and product tables

CREATE TABLE t_product_category (
    id              BIGINT          NOT NULL COMMENT '分类 ID',
    parent_id       BIGINT          NULL COMMENT '父分类 ID',
    name_zh         VARCHAR(128)    NOT NULL COMMENT '中文名称',
    name_en         VARCHAR(128)    NOT NULL COMMENT '英文名称',
    sort_order      INT             NOT NULL DEFAULT 0 COMMENT '排序',
    status          VARCHAR(32)     NOT NULL DEFAULT 'ENABLED' COMMENT '状态: ENABLED, DISABLED',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      VARCHAR(64)     NULL COMMENT '创建人',
    updated_by      VARCHAR(64)     NULL COMMENT '更新人',
    deleted         TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

CREATE TABLE t_product (
    id                  BIGINT          NOT NULL COMMENT '商品 ID',
    sku_code            VARCHAR(64)     NOT NULL COMMENT 'SKU 编码，唯一',
    category_id         BIGINT          NOT NULL COMMENT '分类 ID',
    name_zh             VARCHAR(128)    NOT NULL COMMENT '中文名称',
    name_en             VARCHAR(128)    NOT NULL COMMENT '英文名称',
    description_zh      TEXT            NULL COMMENT '中文描述',
    description_en      TEXT            NULL COMMENT '英文描述',
    main_image_url      VARCHAR(512)    NULL COMMENT '主图',
    specification       VARCHAR(255)    NULL COMMENT '规格',
    price               DECIMAL(12, 2)  NOT NULL COMMENT '单价',
    weight_kg           DECIMAL(10, 3)  NOT NULL COMMENT '单位重量(kg)',
    volume_m3           DECIMAL(10, 4)  NOT NULL COMMENT '单位体积(m³)',
    source              VARCHAR(32)     NOT NULL DEFAULT 'BONDED_WAREHOUSE' COMMENT '来源: BONDED_WAREHOUSE, PORT_SHOP',
    merchant_id         BIGINT          NULL COMMENT '商户 ID，预留',
    drone_deliverable   TINYINT         NOT NULL DEFAULT 1 COMMENT '是否可无人机配送',
    status              VARCHAR(32)     NOT NULL DEFAULT 'ON_SALE' COMMENT '状态: ON_SALE, OFF_SALE',
    created_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at          DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by          VARCHAR(64)     NULL COMMENT '创建人',
    updated_by          VARCHAR(64)     NULL COMMENT '更新人',
    deleted             TINYINT         NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_sku_code (sku_code),
    KEY idx_product_category_id (category_id),
    KEY idx_product_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';
