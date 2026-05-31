# H5 客户端技术架构设计

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
h5-client/
├── src/
│   ├── api/
│   ├── components/
│   ├── i18n/
│   ├── pages/
│   ├── static/
│   ├── stores/
│   ├── types/
│   └── utils/
├── pages.json
└── manifest.json
```

## 3. 模块划分

| 模块 | 职责 |
|------|------|
| `api/authApi.ts` | 注册、登录、个人资料和船舶信息 |
| `api/productApi.ts` | 分类、列表和详情 |
| `api/cartApi.ts` | 购物车本地或服务端同步 |
| `api/orderApi.ts` | 试算、创建、列表、详情和收货 |
| `stores/user.ts` | 登录态、用户资料、语言偏好 |
| `stores/cart.ts` | 购物车商品、数量、金额和重量统计 |
| `stores/order.ts` | 订单查询条件和当前订单详情 |

## 4. API 依赖

1. 客户端 API 以整体接口文档第 6 章为准。
2. 所有业务请求统一走 API 层，不在页面中直接拼接请求。
3. API 错误需要映射为用户可理解的提示文案。
4. 认证失败时清理本地登录态并跳转登录页。

## 5. 国际化

1. 所有页面可见文案必须进入 `src/i18n/`。
2. key 使用点分层命名，例如 `product.detail.title`。
3. 商品名称和商品描述如由后端返回多语言字段，前端按当前语言选择展示。

## 6. 测试重点

1. 购物车金额、重量、体积计算。
2. 订单试算参数组装。
3. 船舶信息表单校验。
4. 订单状态展示映射。
5. 中英文切换后的关键页面渲染。

