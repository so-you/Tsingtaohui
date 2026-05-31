# 仓库端 H5 整体重写设计文档

版本：V1.0
日期：2026-05-29
分支：feat/warehouse-client-optimization

## 1. 目标

对 warehouse-client 进行整体重写，实现三个目标：

1. **需求对齐** — 覆盖 WH-001 到 WH-008 全部功能点
2. **UI 统一** — 与 h5-client 共享设计令牌，仓库端使用独立色彩主题
3. **Demo 可展示** — 内置 mock 数据，无需后端即可展示完整页面内容

## 2. 设计系统

### 2.1 共享设计令牌

从 h5-client 提取，两端共用：

| 类别 | 令牌 | 值 |
|------|------|-----|
| 间距 | xs / sm / md / lg / xl | 8 / 16 / 24 / 32 / 48rpx |
| 圆角 | sm / md / lg / pill | 8 / 16 / 24 / 9999rpx |
| 阴影 | sm / md / lg / card | 四级递增 |
| 字号 | xs / sm / base / md / lg / xl / 2xl | 22 / 24 / 28 / 32 / 36 / 40 / 48rpx |
| 字重 | normal / medium / semibold / bold | 400 / 500 / 600 / 700 |
| 背景 | page / card / surface / input | #f5f5f5 / #ffffff / #fafafa / #f0f0f0 |
| 文字 | primary / secondary / tertiary / placeholder | #222 / #666 / #999 / #bbb |

### 2.2 仓库端色彩主题

独立于 h5-client 的商城红色，仓库端使用工具型蓝色主题：

| 用途 | 色值 | 说明 |
|------|------|------|
| 主色 | #2563EB | 专业工具感 |
| 主色浅 | #3B82F6 | 悬停/次要 |
| 主色深 | #1D4ED8 | 按压状态 |
| 渐变 | linear-gradient(135deg, #2563EB 0%, #3B82F6 100%) | 主按钮 |
| 成功色 | #16A34A | 完成、同步成功 |
| 警告色 | #F59E0B | 需注意、黄牌 |
| 错误色 | #DC2626 | 异常、红牌拦截 |
| 信息色 | #0891B2 | 处理中状态 |

## 3. 项目结构

```
warehouse-client/src/
├── styles/
│   ├── variables.scss          # 设计令牌（间距、圆角、阴影、字体）
│   ├── theme-warehouse.scss    # 仓库端色彩主题
│   └── common.scss             # 公共样式（卡片、按钮、表单、状态标签）
├── mock/
│   ├── index.ts                # mock 开关与注册
│   ├── dashboard.ts            # 工作台统计数据
│   ├── picking.ts              # 拣货任务数据
│   ├── review.ts               # 复核打包数据
│   ├── outbound.ts             # 出库任务数据
│   └── inventory.ts            # 库存查询数据
├── components/
│   ├── ScanInput.vue           # 扫码输入组件（兼容 PDA/蓝牙/物理扫码枪）
│   ├── StatusTag.vue           # 通用状态标签
│   ├── TaskCard.vue            # 任务卡片组件
│   └── EmptyState.vue          # 空状态组件
├── pages/
│   ├── login/index.vue         # 仓库登录页
│   ├── dashboard/index.vue     # 工作台
│   ├── picking/index.vue       # 拣货任务
│   ├── review/index.vue        # 复核打包
│   ├── outbound/index.vue      # 出库交接
│   └── inventory/index.vue     # 库存查询
├── api/warehouse.ts            # API 请求层
├── stores/warehouse.ts         # Pinia 状态管理
├── i18n/                       # 国际化（zh-CN / en-US）
├── types/index.ts              # TypeScript 类型定义
└── utils/
    ├── request.ts              # HTTP 请求工具
    └── scanner.ts              # 扫码设备统一处理
```

## 4. 页面功能设计

### 4.1 登录页（WH-001 新增）

- 账号密码输入框
- 登录按钮（主色渐变）
- 登录成功后跳转工作台
- 支持中英文切换入口
- 表单校验：账号必填、密码必填

### 4.2 工作台（WH-002）

- 页面标题 + 语言切换按钮
- 统计卡片 2x2 网格：待拣货 / 待复核 / 待出库 / 异常订单
- 统计卡片可点击跳转对应页面
- 扫码输入框（自动聚焦）
- 底部导航：工作台 / 拣货 / 复核 / 出库

### 4.3 拣货页（WH-003）

- 顶部扫码输入框（绿色边框，自动聚焦）
- 拣货任务卡片列表：订单号、商品、数量、库位、批次
- 扫码确认后自动刷新任务列表
- 扫码成功/失败/重复/超量反馈

### 4.4 复核打包页（WH-004）

- 顶部扫码输入框（黄色边框，自动聚焦）
- 复核任务卡片：订单号、已扫/应扫数量
- 数量满足后显示打包按钮
- 打包后生成包裹码

### 4.5 出库交接页（WH-005）

- 出库任务卡片：订单号、包裹号、海关同步状态
- 海关红牌拦截时显示红色阻塞提示，禁用出库按钮
- 海关同步通过时显示绿色状态和出库确认按钮
- 出库确认后激活无人机配送任务

### 4.6 库存查询页（WH-007）

- 从工作台入口跳转（非底部导航）
- 搜索框（按 SKU 编码搜索）
- 库存卡片：SKU 编码、库位、批次、可用库存、锁定库存
- 颜色区分库存状态（充足/不足/为零）

### 4.7 扫码设备兼容（WH-006）

`utils/scanner.ts` 统一处理：
- PDA 手持终端键盘回显
- 蓝牙指环扫码器输入
- 物理条码枪模拟键盘输入
- 手动输入兜底
- 按 Enter 自动提交校验
- 页面进入自动聚焦

### 4.8 中英文切换（WH-008）

- 扩展 i18n 翻译，补充登录页和新增组件文案
- 所有页面标题、按钮、状态、提示、错误信息均使用 i18n key
- 禁止硬编码中文或英文

## 5. 通用组件

### 5.1 ScanInput

- Props: borderColor, placeholder, autoFocus
- 事件: @scan(code)
- 自动聚焦，Enter 提交，显示最近 5 条扫描记录
- 成功绿色、失败红色、重复黄色反馈

### 5.2 StatusTag

- Props: status, size
- 根据 status 映射颜色和文案（复用 04 文档状态标签规范）
- 支持中英文自动切换

### 5.3 TaskCard

- Props: task, type (picking/review/outbound)
- 可点击跳转详情
- 展示订单号、优先级、商品数量、状态标签

### 5.4 EmptyState

- Props: title, description, icon
- 居中展示空状态图标、标题、描述

## 6. Mock 数据

### 6.1 机制

- `mock/index.ts` 导出 `setupMock()` 函数
- 拦截 API 请求返回 mock 数据
- 环境变量 `VITE_USE_MOCK=true` 控制
- 接口就绪后设置 `VITE_USE_MOCK=false` 切换为真实 API

### 6.2 数据设计

**工作台统计：**
```typescript
{ pendingPicking: 12, pendingReview: 5, pendingOutbound: 3, exceptionOrders: 2 }
```

**拣货任务（3 条）：** 订单号、商品名/SKU、数量、库位、批次号、期望送达时间、状态（待拣货/拣货中）

**复核任务（2 条）：** 订单号、已扫/应扫数量、商品列表、包裹码、状态（待复核/复核中）

**出库任务（2 条）：** 订单号、包裹号、海关同步状态（含 SYNC_SUCCESS 和 SYNC_FAILED 场景）、无人机分配状态

**库存数据（6 条）：** SKU 编码、商品名、库位、批次、可用库存、锁定库存（涵盖充足/不足/为零）

## 7. 底部导航

| 菜单 | 图标 | 页面 |
|------|------|------|
| 工作台 | home | 仓库工作台 |
| 拣货 | clipboard | 拣货任务 |
| 复核 | check-circle | 复核打包 |
| 出库 | truck | 出库交接 |

库存查询通过工作台入口跳转，不做底部导航项。

## 8. 需求对齐检查

| 编号 | 功能 | 改动 |
|------|------|------|
| WH-001 | 仓库登录 | 新增 login 页面 |
| WH-002 | 工作台 | 重写 UI + 底部导航 |
| WH-003 | 拣货任务 | 重写 UI + 扫码组件 |
| WH-004 | 复核打包 | 重写 UI + 进度展示 |
| WH-005 | 出库交接 | 重写 UI + 海关状态 |
| WH-006 | 扫码设备兼容 | 新增 scanner.ts + ScanInput 组件 |
| WH-007 | 库存查询 | 重写 UI + 搜索 |
| WH-008 | 中英文切换 | 扩展 i18n 翻译 |
