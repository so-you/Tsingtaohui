# 数据模型与接口定义

版本：V1.1  
日期：2026-05-28  

## 1. 设计原则

1. 数据模型以 MySQL 为主存储。
2. 主键建议使用雪花 ID、UUID 或数据库统一 ID 生成策略，具体实现需在后端工程内保持一致。
3. 所有业务表包含 `created_at`、`updated_at`、`created_by`、`updated_by` 字段。
4. 核心交易表使用逻辑删除字段 `deleted`，审计和流水表不做物理删除。
5. 金额字段使用 `DECIMAL(12,2)`。
6. 重量、体积、距离使用 `DECIMAL`，避免浮点误差。
7. 枚举字段存储稳定英文编码，前端根据多语言字典渲染。

## 2. 通用字段和响应格式

### 2.1 通用审计字段

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT 或 VARCHAR(64) | 主键 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| created_by | VARCHAR(64) | 创建人 |
| updated_by | VARCHAR(64) | 更新人 |
| deleted | TINYINT | 逻辑删除标记，0 表示未删除，1 表示已删除 |

### 2.2 API 响应格式

```json
{
  "code": "0",
  "message": "OK",
  "data": {},
  "request_id": "202605281200000001"
}
```

### 2.3 分页响应格式

```json
{
  "code": "0",
  "message": "OK",
  "data": {
    "items": [],
    "page": 1,
    "page_size": 20,
    "total": 0
  },
  "request_id": "202605281200000002"
}
```

## 3. 核心枚举

### 3.1 语言

| 编码 | 说明 |
|------|------|
| zh-CN | 简体中文 |
| en-US | 英文 |

### 3.2 用户类型

| 编码 | 说明 |
|------|------|
| CUSTOMER | 客户端用户 |
| WAREHOUSE_OPERATOR | 仓库操作员 |
| ADMIN | 系统管理员 |
| OPERATOR | 平台运营人员 |
| DRONE_DISPATCHER | 无人机调度人员 |
| FINANCE | 财务人员 |

### 3.3 订单状态

| 编码 | 说明 |
|------|------|
| PENDING_CONFIRM | 待确认 |
| CONFIRMED | 已确认 |
| WAREHOUSE_PROCESSING | 仓库处理中 |
| PENDING_OUTBOUND | 待出库 |
| OUTBOUND | 已出库 |
| PENDING_LOADING | 待装载 |
| IN_DELIVERY | 配送中 |
| PENDING_RECEIPT | 待签收 |
| COMPLETED | 已完成 |
| CANCELLED | 已取消 |
| EXCEPTION | 异常 |

### 3.4 交易模式

| 编码 | 说明 |
|------|------|
| AUTO_TRADE | 自动交易 |
| MATCHING_ORDER | 匹配订单 |

### 3.5 海关同步节点

| 编码 | 级别 | 说明 |
|------|------|------|
| ORDER_CREATED | RED | 订单创建 |
| ORDER_CONFIRMED | YELLOW | 订单确认 |
| WAREHOUSE_OUTBOUND | RED | 仓库出库 |
| DELIVERY_TASK_CREATED | YELLOW | 配送任务创建 |
| DRONE_LOADED | YELLOW | 无人机装载 |
| IN_DELIVERY | YELLOW | 配送中 |
| DELIVERED | YELLOW | 已送达 |
| RECEIPT_CONFIRMED | YELLOW | 已签收 |
| ORDER_CANCELLED | YELLOW | 订单取消 |
| ORDER_EXCEPTION | YELLOW | 订单异常 |

## 4. 数据表定义

### 4.1 用户表 `sys_user`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 用户 ID |
| username | VARCHAR(64) | 是 | 用户名，唯一 |
| password_hash | VARCHAR(255) | 是 | 密码哈希 |
| user_type | VARCHAR(32) | 是 | 用户类型 |
| status | VARCHAR(32) | 是 | ENABLED、DISABLED、LOCKED |
| preferred_language | VARCHAR(16) | 是 | zh-CN 或 en-US |
| last_login_at | DATETIME | 否 | 最近登录时间 |

索引：

1. `uk_sys_user_username` 唯一索引：`username`。
2. `idx_sys_user_type_status` 普通索引：`user_type, status`。

### 4.2 用户资料表 `user_profile`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 资料 ID |
| user_id | BIGINT | 是 | 用户 ID |
| display_name | VARCHAR(128) | 否 | 显示名称 |
| contact_phone | VARCHAR(64) | 否 | 联系电话 |
| email | VARCHAR(128) | 否 | 邮箱 |
| nationality | VARCHAR(64) | 否 | 用户国籍 |

索引：

1. `uk_user_profile_user_id` 唯一索引：`user_id`。

### 4.3 用户船舶信息表 `user_ship`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 用户船舶信息 ID |
| user_id | BIGINT | 是 | 用户 ID |
| ship_no | VARCHAR(64) | 是 | 船号 |
| ship_name | VARCHAR(128) | 否 | 船名 |
| ship_nationality | VARCHAR(64) | 是 | 船籍 |
| imo | VARCHAR(32) | 否 | IMO 编码 |
| mmsi | VARCHAR(32) | 否 | MMSI 编码 |
| is_default | TINYINT | 是 | 是否默认船舶 |

索引：

1. `idx_user_ship_user_id` 普通索引：`user_id`。
2. `idx_user_ship_imo_mmsi` 普通索引：`imo, mmsi`。

### 4.4 船舶主数据表 `ship`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 船舶 ID |
| ship_no | VARCHAR(64) | 是 | 船号 |
| ship_name | VARCHAR(128) | 否 | 船名 |
| ship_nationality | VARCHAR(64) | 是 | 船籍 |
| imo | VARCHAR(32) | 否 | IMO 编码 |
| mmsi | VARCHAR(32) | 否 | MMSI 编码 |
| current_berth | VARCHAR(128) | 否 | 当前泊位 |
| current_anchorage | VARCHAR(128) | 否 | 当前锚地 |
| target_gps | VARCHAR(128) | 否 | 目标 GPS |
| location_source | VARCHAR(32) | 否 | USER_INPUT、ADMIN、SHIPXY、MARINE_TRAFFIC、QR_CODE |

### 4.5 船舶代理人表 `shipping_agent`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 船舶代理人 ID |
| agent_name_zh | VARCHAR(128) | 是 | 中文名称 |
| agent_name_en | VARCHAR(128) | 否 | 英文名称 |
| contact_name | VARCHAR(128) | 否 | 联系人 |
| contact_phone | VARCHAR(64) | 否 | 联系电话 |
| status | VARCHAR(32) | 是 | ENABLED、DISABLED |

### 4.6 商品分类表 `product_category`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 分类 ID |
| parent_id | BIGINT | 否 | 父分类 ID |
| name_zh | VARCHAR(128) | 是 | 中文名称 |
| name_en | VARCHAR(128) | 是 | 英文名称 |
| sort_order | INT | 是 | 排序 |
| status | VARCHAR(32) | 是 | ENABLED、DISABLED |

### 4.7 商品表 `product`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 商品 ID |
| sku_code | VARCHAR(64) | 是 | SKU 编码，唯一 |
| category_id | BIGINT | 是 | 分类 ID |
| name_zh | VARCHAR(128) | 是 | 中文名称 |
| name_en | VARCHAR(128) | 是 | 英文名称 |
| description_zh | TEXT | 否 | 中文描述 |
| description_en | TEXT | 否 | 英文描述 |
| main_image_url | VARCHAR(512) | 否 | 主图 |
| specification | VARCHAR(255) | 否 | 规格 |
| price | DECIMAL(12,2) | 是 | 单价 |
| weight_kg | DECIMAL(10,3) | 是 | 单位重量 |
| volume_m3 | DECIMAL(10,4) | 是 | 单位体积 |
| source | VARCHAR(32) | 是 | BONDED_WAREHOUSE、PORT_SHOP |
| merchant_id | BIGINT | 否 | 商户 ID，预留 |
| drone_deliverable | TINYINT | 是 | 是否可无人机配送 |
| status | VARCHAR(32) | 是 | ON_SALE、OFF_SALE |

### 4.8 库存表 `inventory`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 库存 ID |
| warehouse_id | BIGINT | 是 | 仓库 ID |
| location_code | VARCHAR(64) | 否 | 库位编码 |
| sku_code | VARCHAR(64) | 是 | SKU 编码 |
| batch_no | VARCHAR(64) | 否 | 批次号 |
| available_qty | INT | 是 | 可用库存 |
| locked_qty | INT | 是 | 锁定库存 |
| outbound_qty | INT | 是 | 已出库数量 |
| version | INT | 是 | 乐观锁版本 |

索引：

1. `uk_inventory_stock_unit` 唯一索引：`warehouse_id, location_code, sku_code, batch_no`。

### 4.9 订单表 `order_header`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 订单 ID |
| order_no | VARCHAR(64) | 是 | 订单编号，唯一 |
| user_id | BIGINT | 是 | 下单用户 ID |
| total_price | DECIMAL(12,2) | 是 | 订单总价 |
| total_weight_kg | DECIMAL(10,3) | 是 | 总重量 |
| total_volume_m3 | DECIMAL(10,4) | 是 | 总体积 |
| trade_mode | VARCHAR(32) | 是 | AUTO_TRADE、MATCHING_ORDER |
| order_status | VARCHAR(32) | 是 | 订单状态 |
| warehouse_status | VARCHAR(32) | 否 | 仓库状态 |
| delivery_status | VARCHAR(32) | 否 | 配送状态 |
| customs_sync_status | VARCHAR(32) | 否 | 海关同步状态 |
| consignee_name | VARCHAR(128) | 是 | 收货人 |
| cabin_no | VARCHAR(64) | 是 | Cabin No. |
| contact_info | VARCHAR(128) | 否 | 联系方式 |
| expected_delivery_time | DATETIME | 否 | 期望送达时间 |
| remark | VARCHAR(512) | 否 | 备注 |
| ship_no | VARCHAR(64) | 是 | 船号 |
| ship_name | VARCHAR(128) | 否 | 船名 |
| ship_nationality | VARCHAR(64) | 是 | 船籍 |
| imo | VARCHAR(32) | 否 | IMO 编码 |
| mmsi | VARCHAR(32) | 否 | MMSI 编码 |
| berth_or_anchorage | VARCHAR(128) | 否 | 泊位或锚地 |
| target_gps | VARCHAR(128) | 否 | 目标 GPS |
| shipping_agent_id | BIGINT | 是 | 船舶代理人 ID |
| shipping_agent_name | VARCHAR(128) | 是 | 船舶代理人名称 |
| completed_at | DATETIME | 否 | 完成时间 |

### 4.10 订单明细表 `order_item`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 明细 ID |
| order_id | BIGINT | 是 | 订单 ID |
| order_no | VARCHAR(64) | 是 | 订单编号 |
| product_id | BIGINT | 是 | 商品 ID |
| sku_code | VARCHAR(64) | 是 | SKU 编码 |
| product_name_zh | VARCHAR(128) | 是 | 下单时中文名称快照 |
| product_name_en | VARCHAR(128) | 是 | 下单时英文名称快照 |
| unit_price | DECIMAL(12,2) | 是 | 下单时单价 |
| quantity | INT | 是 | 数量 |
| unit_weight_kg | DECIMAL(10,3) | 是 | 单位重量 |
| unit_volume_m3 | DECIMAL(10,4) | 是 | 单位体积 |
| line_amount | DECIMAL(12,2) | 是 | 行金额 |

### 4.11 包裹表 `package`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 包裹 ID |
| package_no | VARCHAR(64) | 是 | 包裹编号，唯一 |
| order_id | BIGINT | 是 | 订单 ID |
| order_no | VARCHAR(64) | 是 | 订单编号 |
| actual_weight_kg | DECIMAL(10,3) | 否 | 实际重量 |
| actual_volume_m3 | DECIMAL(10,4) | 否 | 实际体积 |
| package_status | VARCHAR(32) | 是 | CREATED、REVIEWED、OUTBOUND、DELIVERED |

### 4.12 无人机表 `drone`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 无人机 ID |
| drone_code | VARCHAR(64) | 是 | 无人机编码 |
| model | VARCHAR(128) | 是 | 型号 |
| flight_no | VARCHAR(64) | 是 | 架次编号 |
| max_payload_kg | DECIMAL(10,3) | 是 | 核载质量 |
| max_volume_m3 | DECIMAL(10,4) | 是 | 可载体积 |
| max_range_km | DECIMAL(10,2) | 是 | 可飞距离 |
| deliverable_categories | VARCHAR(512) | 否 | 可配送品类 |
| status | VARCHAR(32) | 是 | AVAILABLE、IN_MISSION、MAINTENANCE、DISABLED |

### 4.13 配送任务表 `delivery_task`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 配送任务 ID |
| task_no | VARCHAR(64) | 是 | 任务编号，唯一 |
| order_id | BIGINT | 是 | 订单 ID |
| order_no | VARCHAR(64) | 是 | 订单编号 |
| package_no | VARCHAR(64) | 是 | 包裹编号 |
| warehouse_id | BIGINT | 是 | 起点仓库 |
| target_ship_no | VARCHAR(64) | 是 | 目标船号 |
| target_location | VARCHAR(255) | 是 | 目标位置 |
| drone_id | BIGINT | 否 | 无人机 ID |
| drone_code | VARCHAR(64) | 否 | 无人机编码 |
| task_status | VARCHAR(32) | 是 | CREATED、DISPATCHED、IN_DELIVERY、DELIVERED、FAILED、CANCELLED |
| estimated_arrival | DATETIME | 否 | 预计送达 |
| actual_arrival | DATETIME | 否 | 实际送达 |

### 4.14 海关同步记录表 `customs_sync_record`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 同步记录 ID |
| sync_no | VARCHAR(64) | 是 | 同步编号，唯一 |
| order_id | BIGINT | 是 | 订单 ID |
| order_no | VARCHAR(64) | 是 | 订单编号 |
| sync_node | VARCHAR(64) | 是 | 同步节点 |
| sync_level | VARCHAR(16) | 是 | RED、YELLOW |
| request_payload | MEDIUMTEXT | 否 | 请求报文 |
| response_payload | MEDIUMTEXT | 否 | 响应报文 |
| sync_status | VARCHAR(32) | 是 | SYNC_NONE、SYNCING、SYNC_SUCCESS、SYNC_FAILED、RETRYING、MANUAL_RESOLVED |
| failure_reason | VARCHAR(1024) | 否 | 失败原因 |
| retry_count | INT | 是 | 重试次数 |
| next_retry_at | DATETIME | 否 | 下次重试时间 |

### 4.15 审计日志表 `audit_log`

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| id | BIGINT | 是 | 日志 ID |
| actor_id | BIGINT | 否 | 操作人 ID |
| actor_name | VARCHAR(128) | 否 | 操作人 |
| module | VARCHAR(64) | 是 | 模块 |
| action | VARCHAR(64) | 是 | 操作 |
| target_type | VARCHAR(64) | 是 | 对象类型 |
| target_id | VARCHAR(64) | 是 | 对象 ID |
| before_value | MEDIUMTEXT | 否 | 变更前 |
| after_value | MEDIUMTEXT | 否 | 变更后 |
| client_ip | VARCHAR(64) | 否 | IP |
| user_agent | VARCHAR(512) | 否 | User-Agent |

## 5. API 约定

### 5.1 请求约定

1. API 前缀：`/api/v1`。
2. 鉴权方式：`Authorization: Bearer <token>`。
3. 多语言请求头：`Accept-Language: zh-CN` 或 `Accept-Language: en-US`。
4. 写操作必须记录审计日志。
5. 幂等写操作使用 `Idempotency-Key` 请求头。

### 5.2 错误码

| 错误码 | 说明 |
|--------|------|
| 0 | 成功 |
| AUTH_001 | 用户名或密码错误 |
| AUTH_002 | 登录状态失效 |
| USER_001 | 用户名已存在 |
| ORDER_001 | 购物车为空 |
| ORDER_002 | 库存不足 |
| ORDER_003 | 订单状态不允许当前操作 |
| CUSTOMS_001 | 海关红牌节点同步失败 |
| DRONE_001 | 无可用无人机 |
| WAREHOUSE_001 | 扫码结果不匹配 |

## 6. 客户端 API

### 6.1 注册

`POST /api/v1/auth/register`

请求：

```json
{
  "username": "crew001",
  "password": "Passw0rd123",
  "preferred_language": "en-US"
}
```

响应：

```json
{
  "code": "0",
  "message": "OK",
  "data": {
    "token": "jwt-token",
    "user": {
      "id": "10001",
      "username": "crew001",
      "preferred_language": "en-US"
    }
  },
  "request_id": "202605281201000001"
}
```

### 6.2 登录

`POST /api/v1/auth/login`

请求：

```json
{
  "username": "crew001",
  "password": "Passw0rd123"
}
```

### 6.3 获取个人资料

`GET /api/v1/users/me`

响应数据：

```json
{
  "id": "10001",
  "username": "crew001",
  "display_name": "Crew 001",
  "preferred_language": "en-US",
  "ship": {
    "ship_no": "QDH-001",
    "ship_name": "Qingdao Star",
    "ship_nationality": "Panama",
    "imo": "9876543",
    "mmsi": "412345678"
  }
}
```

### 6.4 更新个人船舶信息

`PUT /api/v1/users/me/ship`

请求：

```json
{
  "ship_no": "QDH-001",
  "ship_name": "Qingdao Star",
  "ship_nationality": "Panama",
  "imo": "9876543",
  "mmsi": "412345678"
}
```

### 6.5 商品分类

`GET /api/v1/catalog/categories`

### 6.6 商品列表

`GET /api/v1/catalog/products?category_id=1&keyword=water&page=1&page_size=20`

响应数据项：

```json
{
  "id": "20001",
  "sku_code": "SKU-WATER-001",
  "name_zh": "矿泉水",
  "name_en": "Mineral Water",
  "price": "12.00",
  "main_image_url": "/files/products/water.png",
  "available_qty": 120,
  "drone_deliverable": true,
  "weight_kg": "1.000",
  "volume_m3": "0.0020"
}
```

### 6.7 商品详情

`GET /api/v1/catalog/products/{product_id}`

### 6.8 订单试算

`POST /api/v1/orders/quote`

请求：

```json
{
  "items": [
    {
      "sku_code": "SKU-WATER-001",
      "quantity": 2
    }
  ]
}
```

响应数据：

```json
{
  "total_price": "24.00",
  "total_weight_kg": "2.000",
  "total_volume_m3": "0.0040",
  "validation_results": []
}
```

### 6.9 创建订单

`POST /api/v1/orders`

请求：

```json
{
  "items": [
    {
      "sku_code": "SKU-WATER-001",
      "quantity": 2
    }
  ],
  "consignee_name": "John",
  "cabin_no": "A-102",
  "contact_info": "+123456789",
  "expected_delivery_time": "2026-05-28T15:00:00+08:00",
  "remark": "Call before delivery",
  "ship": {
    "ship_no": "QDH-001",
    "ship_name": "Qingdao Star",
    "ship_nationality": "Panama",
    "imo": "9876543",
    "mmsi": "412345678"
  },
  "shipping_agent_id": "30001"
}
```

响应数据：

```json
{
  "order_no": "OD202605280001",
  "trade_mode": "AUTO_TRADE",
  "order_status": "CONFIRMED"
}
```

### 6.10 订单列表

`GET /api/v1/orders?status=PENDING_RECEIPT&page=1&page_size=20`

### 6.11 订单详情

`GET /api/v1/orders/{order_no}`

### 6.12 验证码收货

`POST /api/v1/orders/{order_no}/receipt/verify-code`

请求：

```json
{
  "verify_code": "836214"
}
```

### 6.13 扫码收货

`POST /api/v1/orders/receipt/package-scan`

请求：

```json
{
  "package_no": "PK202605280001"
}
```

## 7. 仓库端 API

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/v1/warehouse/dashboard` | 仓库工作台统计 |
| GET | `/api/v1/warehouse/picking-tasks` | 待拣货任务 |
| POST | `/api/v1/warehouse/picking-tasks/{task_id}/scan` | 拣货扫码确认 |
| GET | `/api/v1/warehouse/review-tasks` | 待复核任务 |
| POST | `/api/v1/warehouse/review-tasks/{task_id}/scan-product` | 商品复核扫码 |
| POST | `/api/v1/warehouse/review-tasks/{task_id}/pack` | 打包并生成包裹 |
| GET | `/api/v1/warehouse/outbound-tasks` | 待出库任务 |
| POST | `/api/v1/warehouse/outbound-tasks/{task_id}/confirm` | 出库交接确认 |
| GET | `/api/v1/warehouse/inventory` | 库存查询 |

出库确认响应中必须明确是否被海关红牌拦截：

```json
{
  "outbound_allowed": false,
  "blocked_by": "CUSTOMS_RED_CARD",
  "sync_record_no": "CS202605280001",
  "message": "Customs outbound sync failed"
}
```

## 8. 管理端 API

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户 | GET | `/api/v1/admin/users` | 用户列表 |
| 用户 | PUT | `/api/v1/admin/users/{user_id}/status` | 启停用户 |
| 船舶 | GET | `/api/v1/admin/ships` | 船舶列表 |
| 船舶 | POST | `/api/v1/admin/ships` | 新增船舶 |
| 船代 | GET | `/api/v1/admin/shipping-agents` | 船舶代理人列表 |
| 商品 | GET | `/api/v1/admin/products` | 商品列表 |
| 商品 | POST | `/api/v1/admin/products` | 新增商品 |
| 商品 | PUT | `/api/v1/admin/products/{product_id}` | 更新商品 |
| 库存 | GET | `/api/v1/admin/inventory` | 库存列表 |
| 库存 | POST | `/api/v1/admin/inventory/adjustments` | 库存调整 |
| 订单 | GET | `/api/v1/admin/orders` | 订单列表 |
| 订单 | GET | `/api/v1/admin/orders/{order_no}` | 订单详情 |
| 订单 | POST | `/api/v1/admin/orders/{order_no}/cancel` | 取消订单 |
| 匹配池 | GET | `/api/v1/admin/matching-orders` | 匹配订单池 |
| 匹配池 | POST | `/api/v1/admin/matching-orders/{order_no}/confirm` | 人工确认 |
| 无人机 | GET | `/api/v1/admin/drones` | 无人机列表 |
| 无人机 | POST | `/api/v1/admin/drones` | 新增无人机 |
| 规则 | GET | `/api/v1/admin/rules` | 规则列表 |
| 规则 | PUT | `/api/v1/admin/rules/{rule_id}` | 更新规则 |
| 海关 | GET | `/api/v1/admin/customs-sync-records` | 海关同步记录 |
| 海关 | POST | `/api/v1/admin/customs-sync-records/{sync_no}/retry` | 手动重试 |
| 对账 | GET | `/api/v1/admin/reconciliation/export` | 对账导出 |
| 审计 | GET | `/api/v1/admin/audit-logs` | 审计日志 |

## 9. 外部系统接口契约

### 9.1 无人机状态查询

`GET /drones/status`

平台期望字段：

```json
{
  "drones": [
    {
      "drone_code": "DR-001",
      "model": "M-20",
      "flight_no": "F-001",
      "battery_percent": 92,
      "idle": true,
      "max_payload_kg": "20.000",
      "remaining_payload_kg": "20.000",
      "max_volume_m3": "0.1000",
      "max_range_km": "15.00",
      "status": "AVAILABLE"
    }
  ]
}
```

### 9.2 无人机任务派发

`POST /deliveries/dispatch`

请求：

```json
{
  "task_no": "DT202605280001",
  "order_no": "OD202605280001",
  "package_no": "PK202605280001",
  "actual_weight_kg": "2.000",
  "actual_volume_m3": "0.0040",
  "origin_warehouse": "QDW-001",
  "target_ship_no": "QDH-001",
  "target_gps": "36.0671,120.3826",
  "expected_delivery_time": "2026-05-28T15:00:00+08:00"
}
```

### 9.3 无人机回调

`POST /api/v1/integrations/drone/callback`

请求：

```json
{
  "event_id": "EVT-202605280001",
  "task_no": "DT202605280001",
  "status": "DELIVERED",
  "event_time": "2026-05-28T15:10:00+08:00",
  "message": "Delivered successfully"
}
```

幂等规则：

1. `event_id` 唯一。
2. 已处理事件再次回调时直接返回成功。
3. 不允许回调导致订单状态从后置状态回退到前置状态。

### 9.4 海关同步

海关接口由后端 `customs` 模块封装。内部同步请求使用统一结构：

```json
{
  "sync_node": "ORDER_CREATED",
  "order_no": "OD202605280001",
  "occurred_at": "2026-05-28T14:00:00+08:00",
  "payload": {
    "ship_no": "QDH-001",
    "ship_nationality": "Panama",
    "imo": "9876543",
    "mmsi": "412345678",
    "shipping_agent_name": "Qingdao Agent",
    "items": [
      {
        "sku_code": "SKU-WATER-001",
        "quantity": 2,
        "weight_kg": "2.000",
        "volume_m3": "0.0040"
      }
    ]
  }
}
```

红牌节点同步失败时，调用方必须得到明确阻塞结果，且业务状态不可继续流转。

