# 青岛世天智汇项目文档目录规范

本目录按“需求来源 -> 整体设计 -> 各端服务设计 -> 归档资料”组织文档，目标是让 H5 客户端、仓库端、管理端和后端服务可以并行开发，减少跨端文档耦合。

## 目录结构

```text
docs/
├── 00-original-requirements/        # 原始需求、讨论记录、阶段任务和示例数据
├── 01-product-design/               # 项目整体功能设计与通用研发规范
├── 02-service-design/               # 四端独立设计文档
│   ├── h5-client/
│   ├── warehouse-client/
│   ├── admin-console/
│   └── backend/
└── 99-archive/                      # 历史索引、代码审查报告和工具归档
```

## 文档分层

| 层级 | 目录 | 维护内容 |
|------|------|----------|
| 原始需求文档 | `00-original-requirements/` | 最原始的需求、讨论修改记录、开发阶段计划、演示账号和样例数据 |
| 整体功能设计 | `01-product-design/` | 产品级需求规格说明书、总体架构、数据模型、接口契约、统一 UI 和研发规范 |
| 各端服务设计 | `02-service-design/<service>/` | 每个服务独立维护需求规格说明书、技术架构设计文档、UI 设计规范 |
| 历史归档 | `99-archive/` | 不直接作为当前开发基线的历史文档、审查报告和工具产物 |

## 四端服务目录规范

每个服务目录固定包含以下文件：

| 文件 | 用途 |
|------|------|
| `README.md` | 服务文档入口、代码目录、依赖文档和边界说明 |
| `requirements-specification.md` | 面向该服务的详细需求规格说明书 |
| `technical-architecture.md` | 该服务内部技术架构、模块划分、接口依赖和工程约束 |
| `ui-design-guidelines.md` | 该服务 UI 设计规范；后端服务无独立 UI 时记录 API 文档和运维界面约束 |

## 使用原则

1. 需求变更先进入 `00-original-requirements/`，确认后同步更新 `01-product-design/` 和受影响的服务文档。
2. 跨端公共规则以 `01-product-design/` 为准，各端只保留自身实现所需的细化规则。
3. 四端并行开发时优先阅读对应 `02-service-design/<service>/README.md`。
4. 历史审查和工具产物进入 `99-archive/`，不作为当前需求基线。

