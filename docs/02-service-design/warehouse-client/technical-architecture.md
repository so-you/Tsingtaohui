# 仓库端技术架构设计

## 1. 技术栈

| 类型 | 选型 |
|------|------|
| 框架 | Uni-app + Vue3 |
| 语言 | TypeScript |
| 状态管理 | Pinia |
| 路由 | Uni-app `pages.json` |
| HTTP | Axios |
| 国际化 | vue-i18n |
| 测试 | Vitest + @vue/test-utils |

## 2. 推荐目录

```text
warehouse-client/
├── src/
│   ├── api/
│   ├── components/
│   ├── i18n/
│   ├── mock/
│   ├── pages/
│   ├── static/
│   ├── stores/
│   ├── styles/
│   ├── types/
│   └── utils/
├── pages.json
└── manifest.json
```

## 3. 页面模块

| 页面 | 职责 |
|------|------|
| `login` | 仓库人员登录 |
| `dashboard` | 待办概览和作业入口 |
| `picking` | 拣货任务、扫码校验和完成确认 |
| `review` | 复核打包、商品校验和包裹确认 |
| `outbound` | 出库交接和海关红牌校验 |
| `inventory` | 库存查询 |

## 4. 通用组件

| 组件 | 职责 |
|------|------|
| `ScanInput` | 统一处理扫码枪、PDA 和手动输入 |
| `StatusTag` | 展示订单、仓库和异常状态 |
| `TaskCard` | 展示作业任务摘要和主操作 |
| `EmptyState` | 空任务、无结果和异常兜底 |

## 5. 扫码处理

1. 扫码工具集中放在 `src/utils/scanner.ts`。
2. 扫码结果进入页面业务校验前先做去空格、去控制字符和格式识别。
3. 对连续扫码输入设置节流或提交边界，避免重复提交。
4. 扫码失败要保留当前任务上下文。

## 6. Mock 数据

1. 开发环境使用 `VITE_USE_MOCK=true` 启用内置 mock。
2. Mock 覆盖登录、任务列表、任务详情、扫码校验、完成作业和库存查询。
3. Mock 数据结构必须与后端 API 响应保持一致。

## 7. 测试重点

1. 扫码输入解析和回车提交。
2. 拣货、复核、出库状态流转。
3. 红牌海关同步拦截。
4. Mock API 与真实 API 响应结构兼容。

