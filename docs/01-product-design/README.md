# 整体功能设计

本目录是项目当前开发基线，覆盖青岛世天智汇保税仓货物上船无人机配送平台的产品需求、总体架构、数据模型、接口契约、统一 UI 和研发规范。

## 文档清单

| 文档 | 说明 |
|------|------|
| [software-requirements-specification.md](software-requirements-specification.md) | 项目整体软件需求规格说明书 |
| [system-architecture.md](system-architecture.md) | 系统架构、应用划分、后端模块、集成边界和部署拓扑 |
| [data-model-and-api-definition.md](data-model-and-api-definition.md) | 核心数据模型、枚举、REST API 和外部系统契约 |
| [ui-component-library-and-design-guidelines.md](ui-component-library-and-design-guidelines.md) | H5 客户端、仓库端和管理端统一 UI 规范 |
| [development-testing-deployment-guidelines.md](development-testing-deployment-guidelines.md) | 研发、测试、部署、运维和质量门禁规范 |

## 与服务文档的关系

1. 本目录定义跨端公共规则，是四端文档的上位基线。
2. `../02-service-design/` 下的服务文档只细化本端职责，不重复维护跨端完整方案。
3. 当整体需求、状态机、接口契约或安全规则变更时，必须同步检查四端服务文档。

