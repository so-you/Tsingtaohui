# 仓库端服务设计

代码目录：`../../../warehouse-client/`

仓库端面向保税仓操作人员，使用 Uni-app H5 适配 PDA、蓝牙指环扫描器和物理条码枪，覆盖登录、工作台、拣货、复核打包、出库交接和库存查询。

## 文档清单

| 文档 | 说明 |
|------|------|
| [requirements-specification.md](requirements-specification.md) | 仓库端详细需求规格说明书 |
| [technical-architecture.md](technical-architecture.md) | Uni-app Vue3 技术架构、扫码工具、Mock 数据和模块划分 |
| [ui-design-guidelines.md](ui-design-guidelines.md) | PDA 优先的蓝色主题、扫码组件和任务卡片规范 |
| [warehouse-client-redesign-design.md](warehouse-client-redesign-design.md) | 仓库端 H5 整体重写设计文档原文 |

## 上游基线

1. 整体需求：[../../01-product-design/software-requirements-specification.md](../../01-product-design/software-requirements-specification.md)
2. API 契约：[../../01-product-design/data-model-and-api-definition.md](../../01-product-design/data-model-and-api-definition.md)
3. 公共 UI：[../../01-product-design/ui-component-library-and-design-guidelines.md](../../01-product-design/ui-component-library-and-design-guidelines.md)

