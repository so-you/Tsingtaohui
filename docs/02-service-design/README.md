# 各端服务设计

本目录按服务拆分文档，使 H5 客户端、仓库端、管理端和后端服务可以独立阅读、独立开发、独立验收。

## 服务目录

| 服务 | 代码目录 | 文档目录 | 说明 |
|------|----------|----------|------|
| H5 客户端 | `../../h5-client/` | [h5-client](h5-client/README.md) | 船员端商城、下单、订单和扫码收货 |
| 仓库端 | `../../warehouse-client/` | [warehouse-client](warehouse-client/README.md) | 保税仓拣货、复核、打包、出库和库存查询 |
| 管理端 | `../../admin-console/` | [admin-console](admin-console/README.md) | 运营后台、订单、库存、无人机、海关同步和 RBAC |
| 后端服务 | `../../backend/` | [backend](backend/README.md) | REST API、业务服务、数据库、认证授权和外部系统集成 |

## 服务文档模板

每个服务目录固定包含：

1. `README.md`
2. `requirements-specification.md`
3. `technical-architecture.md`
4. `ui-design-guidelines.md`

## 并行开发规则

1. 各端需求变更先更新本服务目录，再评估是否影响整体文档。
2. API、状态机、数据模型和权限变更必须同步后端服务文档与整体接口文档。
3. UI 规范以各端 `ui-design-guidelines.md` 为开发入口，公共设计令牌以 `../01-product-design/ui-component-library-and-design-guidelines.md` 为准。

