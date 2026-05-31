# 后端服务技术架构设计

## 1. 技术栈

| 类型 | 选型 |
|------|------|
| 框架 | Spring Boot 3.x |
| JDK | 17 |
| ORM | MyBatis-Plus |
| 数据库 | MySQL 8.x |
| 缓存 | Redis |
| 安全 | Spring Security + JWT |
| 测试 | JUnit 5 + Mockito + Spring Boot Test |

## 2. 推荐目录

```text
backend/
├── src/
│   ├── main/
│   │   ├── java/com/tsingtaohui/
│   │   │   ├── common/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── integration/
│   │   │   ├── mapper/
│   │   │   ├── model/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.yml
│   │       └── mapper/
│   └── test/
├── pom.xml
└── Dockerfile
```

## 3. 业务模块

| 模块 | 职责 |
|------|------|
| 用户认证 | 注册、登录、JWT、密码加密、账号类型 |
| 商品库存 | 商品、分类、库存、锁定和释放 |
| 订单服务 | 订单创建、试算、状态流转、撮合判断 |
| 仓库服务 | 拣货、复核、包裹、出库 |
| 配送服务 | 无人机匹配、任务派发、回调处理 |
| 海关同步 | 红牌拦截、黄牌警告、重试、记录 |
| 管理服务 | 后台管理 API、规则配置、RBAC |
| 审计日志 | 敏感操作记录和查询 |

## 4. 数据设计

核心表以整体数据模型文档为准：

1. `t_user`
2. `t_user_profile`
3. `t_user_ship`
4. `t_ship`
5. `t_product`
6. `t_inventory`
7. `t_order`
8. `t_order_item`
9. `t_package`
10. `t_drone`
11. `t_delivery_task`
12. `t_customs_sync_record`
13. `t_audit_log`

## 5. 接口设计

1. REST API 路径使用 kebab-case 和复数资源名。
2. 返回结构统一为 `code`、`message`、`data`。
3. 分页返回统一包含 `records`、`total`、`pageNo` 和 `pageSize`。
4. 外部系统接口封装在 `integration/`，不在业务 Service 中直接拼接外部请求。

## 6. 事务和幂等

1. 库存锁定、订单创建和海关红牌同步需要明确事务边界。
2. 无人机 webhook 使用外部回调 ID 或业务幂等键去重。
3. 海关同步重试不得重复推进业务状态。
4. 审计日志失败不得吞掉主业务异常原因。

## 7. 测试重点

1. 订单试算和自动交易判断。
2. 库存锁定、释放和并发扣减。
3. 红牌海关同步阻断。
4. 黄牌海关同步重试。
5. 无人机回调幂等。
6. RBAC 权限校验。

