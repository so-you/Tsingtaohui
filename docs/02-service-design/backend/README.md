# 后端服务设计

代码目录：`../../../backend/`

后端服务提供统一 REST API、认证授权、订单履约、库存锁定、无人机集成、海关同步、审计日志和后台管理能力，是四端业务一致性的核心边界。

## 文档清单

| 文档 | 说明 |
|------|------|
| [requirements-specification.md](requirements-specification.md) | 后端服务详细需求规格说明书 |
| [technical-architecture.md](technical-architecture.md) | Spring Boot 技术架构、模块划分、数据和集成设计 |
| [ui-design-guidelines.md](ui-design-guidelines.md) | 后端无独立业务 UI，本文件定义 API 文档、错误响应和运维界面展示规范 |

## 上游基线

1. 整体需求：[../../01-product-design/software-requirements-specification.md](../../01-product-design/software-requirements-specification.md)
2. 总体架构：[../../01-product-design/system-architecture.md](../../01-product-design/system-architecture.md)
3. 数据和接口：[../../01-product-design/data-model-and-api-definition.md](../../01-product-design/data-model-and-api-definition.md)

