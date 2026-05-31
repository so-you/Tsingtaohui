# 管理端技术架构设计

## 1. 技术栈

| 类型 | 选型 |
|------|------|
| 框架 | Vue3 |
| 语言 | TypeScript |
| UI 组件 | Element Plus |
| 构建工具 | Vite |
| 状态管理 | Pinia |
| 路由 | Vue Router |
| HTTP | Axios |
| 国际化 | vue-i18n |
| 测试 | Vitest |

## 2. 推荐目录

```text
admin-console/
├── src/
│   ├── api/
│   ├── assets/
│   ├── components/
│   ├── composables/
│   ├── i18n/
│   ├── layouts/
│   ├── router/
│   ├── stores/
│   ├── styles/
│   ├── types/
│   ├── utils/
│   └── views/
├── vite.config.ts
└── package.json
```

## 3. 模块划分

| 模块 | 职责 |
|------|------|
| `views/orders` | 订单列表、详情、状态操作 |
| `views/matching` | 撮合池和人工匹配 |
| `views/products` | 商品、分类和库存 |
| `views/drones` | 无人机状态和配送任务 |
| `views/rules` | 服务时间、重量、区域等规则 |
| `views/customs` | 海关同步监控和重试日志 |
| `views/rbac` | 用户、角色、菜单和权限 |

## 4. 路由和权限

1. 路由按业务域拆分，使用懒加载。
2. 登录态和权限信息放入 Pinia。
3. 路由守卫校验 token、菜单权限和按钮权限。
4. 无权限页面展示明确提示，不暴露敏感数据。

## 5. API 约束

1. 管理端 API 以整体接口文档第 8 章为准。
2. 所有列表 API 使用统一分页结构。
3. 运营操作必须携带操作原因或备注字段。
4. 审计类接口只读展示，不在前端伪造审计记录。

## 6. 测试重点

1. 路由权限和菜单渲染。
2. 订单状态和交易模式筛选。
3. 表格分页、查询参数和重置逻辑。
4. 敏感操作的二次确认和权限拦截。

