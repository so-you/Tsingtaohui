# 青岛汇软件开发文档索引

版本：V1.1  
日期：2026-05-28  
适用阶段：一期 MVP 研发落地  

## 1. 文档目的

本目录用于承接 `docs/superpowers/specs/` 中的产品规格，并按最新功能调整整理为软件研发可执行文档。文档覆盖需求、架构、数据模型、接口、UI 规范、研发流程、测试验收、部署运维和安全合规约束。

## 2. 最新功能调整

1. 一期优先实现用户注册和登录。客户端采用用户名和密码注册登录，免注册一船一码下单暂不开发，后续由客户确认后再纳入版本计划。
2. 个人资料增加船舶信息，包含船号、船籍、IMO 编码和 MMSI 编码。
3. 客户端为 H5，使用 Uni-app 开发，界面采用购物商城风格，底部导航为：首页、商品、订单、我的。
4. 收货扫码确认入口放在“我的”页面，不再作为底部独立菜单。
5. 客户端、仓库端和管理端的所有页面和功能支持中文和英文切换。
6. 管理端技术栈为 Vue 3 + Element Plus。
7. 后端服务技术栈为 Spring Boot + JDK 17。
8. 数据库使用 MySQL。
9. 仓库端技术栈同客户端，使用 Uni-app H5 开发。

## 3. 文档清单

| 文档 | 说明 |
|------|------|
| [01-software-requirements-specification.md](01-software-requirements-specification.md) | 软件需求规格说明书，定义角色、范围、业务流程、功能需求和验收标准 |
| [02-system-architecture.md](02-system-architecture.md) | 系统架构与模块设计，定义技术栈、端到端架构、服务模块和集成边界 |
| [03-data-model-and-api-definition.md](03-data-model-and-api-definition.md) | 数据模型与接口定义，定义核心表、枚举、REST API 和外部系统契约 |
| [04-ui-component-library-and-design-guidelines.md](04-ui-component-library-and-design-guidelines.md) | UI 组件库与设计规范，覆盖客户端、仓库端和管理端 |
| [05-development-testing-deployment-guidelines.md](05-development-testing-deployment-guidelines.md) | 研发、测试、部署和运维规范，定义工程结构、质量门禁和发布要求 |

## 4. 需求基线

本文档集基于以下输入整理：

1. `docs/superpowers/specs/2026-05-22-bonded-warehouse-ship-drone-delivery-product-spec.md`
2. `docs/superpowers/specs/2026-05-22-bonded-warehouse-ship-drone-delivery-product-spec-glm.md`
3. `docs/superpowers/specs/2026-05-23-h5-client-mvp-design.md`
4. 2026-05-28 用户补充的功能和技术栈调整

## 5. 一期不开发范围

1. 免注册一船一码下单。
2. 在线支付、线上结算和发票。
3. 送海关查验流程。
4. 港口周边商店和复杂商家入驻体系。
5. 多仓调拨和多无人机拆单配送。
6. 无人机飞控、航线审批、飞行安全决策和实时轨迹可视化。
7. 真实 ShipXY、MarineTraffic、海关和无人机硬件联调，除非项目进入接口联调阶段。

## 6. 研发使用方式

1. 产品、研发和测试以 `01` 文档作为需求验收基线。
2. 后端和数据库设计以 `03` 文档作为接口和表结构基线。
3. 前端、交互和视觉以 `04` 文档作为 UI 组件和页面规范基线。
4. 项目排期、分支、质量门禁、部署和运维以 `05` 文档作为工程执行基线。
