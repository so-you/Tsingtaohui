# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 及所有 AI 编码助手在本仓库工作时的主要指导文档。

---

## 项目概述

**青岛世天智汇 (Shitian Zhihui)** 是面向青岛港的保税仓货物上船无人机配送平台。提供从在线下单、保税仓履约、无人机配送、船舶收货到海关数据同步的闭环业务流程。

目标用户：靠港船舶的船员（通过 H5 商城下单购买保税仓商品，由无人机配送至船上）。

## 技术栈

| 子项目 | 技术选型 | 说明 |
|--------|---------|------|
| **H5 客户端（船员端）** | Uni-app (Vue3) | 商城风格 UI（参考有赞），支持中英文 i18n |
| **仓库端** | Uni-app (Vue3) | 与 H5 客户端同技术栈，适配 PDA 设备 |
| **管理端 (Admin Console)** | Vue3 + Element Plus | 后台管理系统，RBAC 权限控制 |
| **后端服务** | Spring Boot, JDK 17 | RESTful API 服务 |
| **数据库** | MySQL | 主数据库 |
| **无人机系统** | 外部黑盒系统 | 仅通过 API 集成，不直接控制飞行 |

### 前端公共依赖

- **Vue3** — 所有前端项目的基础框架
- **TypeScript** — 所有前端项目强制使用
- **Pinia** — 状态管理
- **Vue Router** — 路由管理
- **Axios** — HTTP 请求
- **vue-i18n** — 国际化（H5 客户端必须支持中英文）

### 后端公共依赖

- **Spring Boot 3.x** — 主框架
- **JDK 17** — 运行时
- **MyBatis-Plus** — ORM 框架
- **MySQL 8.x** — 数据库
- **Redis** — 缓存与会话管理
- **Spring Security** — 认证授权

## 项目结构

本仓库采用 Monorepo 结构：

```
Tsingtaohui/
├── h5-client/                  # Uni-app H5 客户端（船员端）
│   ├── src/
│   │   ├── pages/              # 页面
│   │   ├── components/         # 组件
│   │   ├── stores/             # Pinia stores
│   │   ├── api/                # API 请求
│   │   ├── i18n/               # 国际化资源
│   │   ├── utils/              # 工具函数
│   │   └── static/             # 静态资源
│   ├── pages.json              # 页面路由配置
│   └── manifest.json           # Uni-app 配置
│
├── warehouse-client/           # Uni-app 仓库端
│   ├── src/
│   │   ├── pages/              # 页面（login / dashboard / picking / review / outbound / inventory）
│   │   ├── components/         # 通用组件（ScanInput / StatusTag / TaskCard / EmptyState）
│   │   ├── styles/             # 设计令牌 + 仓库端色彩主题 + 公共样式
│   │   ├── mock/               # Mock 数据模块（开发环境自动加载）
│   │   ├── stores/             # Pinia stores
│   │   ├── api/                # API 请求
│   │   ├── i18n/               # 国际化资源
│   │   ├── utils/              # 工具函数（含 scanner.ts 扫码设备处理）
│   │   └── types/              # TypeScript 类型定义
│   ├── pages.json
│   └── manifest.json
│
├── admin-console/              # Vue3 + Element Plus 管理端
│   ├── src/
│   │   ├── views/              # 页面视图
│   │   ├── components/         # 组件
│   │   ├── stores/             # Pinia stores
│   │   ├── api/                # API 请求
│   │   ├── router/             # 路由配置
│   │   ├── layouts/            # 布局组件
│   │   └── utils/              # 工具函数
│   ├── vite.config.ts
│   └── package.json
│
├── backend/                    # Spring Boot 后端服务
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tsingtaohui/
│   │   │   │   ├── controller/     # REST 控制器
│   │   │   │   ├── service/        # 业务逻辑
│   │   │   │   ├── mapper/         # MyBatis Mapper
│   │   │   │   ├── model/          # 数据模型（entity / DTO / VO）
│   │   │   │   ├── config/         # 配置类
│   │   │   │   ├── common/         # 公共工具与常量
│   │   │   │   └── integration/    # 外部系统集成（无人机、海关）
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── mapper/         # MyBatis XML
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── docs/                       # 项目文档
│   ├── 00-original-requirements/ # 原始需求、讨论记录、阶段计划和样例数据
│   ├── 01-product-design/        # 整体功能设计、系统架构、数据模型、接口契约、统一 UI 和研发规范
│   ├── 02-service-design/        # H5 客户端、仓库端、管理端和后端服务独立设计文档
│   └── 99-archive/               # 历史索引、审查报告和工具归档
│
├── CLAUDE.md                   # 本文件 — AI 编码指导
├── AGENTS.md                   # Codex 编码指导
└── README.md                   # 项目说明
```

## 构建、测试与开发命令

### 通用命令

```bash
rg --files                      # 快速列出项目文件
git status --short              # 检查本地变更
git diff                        # 查看待提交的修改
```

### H5 客户端（船员端）

```bash
cd h5-client
npm install                     # 安装依赖
npm run dev:h5                  # 启动 H5 开发服务器
npm run build:h5                # 构建 H5 生产包
npm run lint                    # 代码检查
npm run test                    # 运行单元测试
```

### 仓库端

```bash
cd warehouse-client
npm install                     # 安装依赖
npm run dev:h5                  # 启动开发服务器
npm run build:h5                # 构建生产包
npm run lint                    # 代码检查
npm run test                    # 运行单元测试
```

### 管理端

```bash
cd admin-console
npm install                     # 安装依赖
npm run dev                     # 启动 Vite 开发服务器
npm run build                   # 构建生产包
npm run lint                    # 代码检查
npm run test                    # 运行单元测试（Vitest）
```

### 后端服务

```bash
cd backend
./mvnw spring-boot:run          # 启动开发服务器
./mvnw test                     # 运行单元测试
./mvnw package                  # 打包
./mvnw clean install            # 清理并构建
```

---

## 架构与关键设计决策

### 三端架构

#### 1. H5 客户端（船员端）

- **框架**: Uni-app (Vue3)
- **用户认证**: 用户名/密码注册登录（当前阶段不支持匿名/免登录）
- **UI 风格**: 商城风格，参考有赞 (Youzan) 的交互与视觉设计
- **底部导航**: 首页 (Home) / 商品 (Products) / 订单 (Orders) / 我的 (Mine)
- **扫码功能**: 在「我的」页面提供扫码确认收货功能
- **国际化**: 必须支持中文和英文 (vue-i18n)
- **用户注册信息**: 用户名、密码、船舶信息（船号、国籍、IMO/MMSI）

#### 2. 仓库端（保税仓操作端）

- **框架**: Uni-app (Vue3)，与 H5 客户端同技术栈
- **设备适配**: 必须支持 PDA 手持终端、蓝牙指环扫描器、物理条码枪（键盘模拟输入）
- **核心功能**: 拣货、验货、打包、出库
- **交互设计**: 以扫码驱动工作流，最小化手动输入

#### 3. 管理端 (Admin Console)

- **框架**: Vue3 + Element Plus
- **功能模块**:
  - 订单管理（全状态查询、操作）
  - 撮合池管理（人工确认匹配）
  - 商品与库存管理
  - 无人机管理（状态监控、调度记录）
  - 规则配置（服务时间、重量限制、区域限制等）
  - 海关同步监控（同步状态、失败重试、日志查询）
  - RBAC 权限管理（角色、用户、菜单权限）

### 核心业务流程

1. 用户通过用户名/密码注册，填写船舶信息（船号、国籍、IMO/MMSI）
2. 用户浏览商品、加入购物车，填写收货人姓名和舱位号 (Cabin No.)
3. 系统校验库存、重量、体积、配送规则
4. 订单进入 **自动交易 (Auto-Trade)** 流程或 **撮合订单 (Matching Order)** 池
5. 仓库接收订单，锁定库存，执行拣货/验货/打包
6. 系统根据重量、体积、距离、可用性自动匹配无人机机型与航班
7. 无人机配送至船舶；用户在「我的」页面通过扫码确认收货
8. 关键节点数据同步至海关服务器

### 订单状态流转

```
待提交 → 待确认 → 已确认 → 仓库处理中 → 待出库 → 已出库 → 待装载 → 配送中 → 待收货 → 已完成
                                                                                              ↘ 已取消
                                                                                              ↘ 异常
```

### 交易模式

- **自动交易 (Auto-Trade)**: 标准保税仓订单，通过所有自动校验（库存、重量、体积、无人机可用性、船舶位置、服务时间、海关数据完整性）后自动履约。
- **撮合订单 (Matching Order)**: 需要人工介入的订单（超大件、多包裹、船舶位置不明确、特殊配送时间、库存不足但可调配、无人机匹配失败等）。

### 海关同步与合规

#### 红牌拦截 (Red-card Interception) — 阻断型

- **触发节点**: 订单创建、仓库出库
- **行为**: 海关同步必须成功，否则不允许仓库操作继续或无人机任务激活
- **失败处理**: 阻断流程，等待同步成功或人工处理

#### 黄牌警告 (Yellow-card Warning) — 非阻断型

- **触发节点**: 配送任务创建、无人机装载、配送中、已送达、已签收、订单异常
- **行为**: 同步失败触发告警和自动重试，不阻断履约流程
- **失败处理**: 记录日志，定时重试，通知运营人员

#### 通用规则

- 所有海关同步的请求/响应必须完整记录，用于审计追溯
- 同步记录模型: 订单 ID、节点类型、请求体、响应体、状态、重试次数、时间戳

### 无人机集成

无人机系统视为外部黑盒硬件系统，平台不直接控制飞行动作。

#### 最小 API 契约

```
GET  /drones/status              # 查询无人机状态列表
POST /deliveries/dispatch        # 派发配送任务
POST /webhook/drone-callback     # 无人机状态回调（由无人机系统调用）
```

#### 集成规则

- Webhook 回调必须幂等处理（相同 callback 多次调用结果一致）
- 所有 API 调用必须记录日志
- 无人机状态变更通过回调推送，平台不主动轮询
- 需处理无人机系统不可用的降级场景

### 关键数据模型

| 模型 | 关键字段 | 说明 |
|------|---------|------|
| **Product (商品)** | SKU, weight, volume, drone_deliverable, merchant_id | merchant_id 预留多商户扩展 |
| **Order (订单)** | ship_info, IMO/MMSI, shipping_agent, consignee, cabin_no, trade_mode, order_status, warehouse_status, delivery_status, customs_sync_status | 多维状态独立管理 |
| **User (用户)** | username, password_hash, ship_no, nationality, imo, mmsi | 船员用户信息 |
| **Inventory (库存)** | warehouse, location, batch, available_qty, locked_qty | 可用库存与锁定库存分离 |
| **Drone (无人机)** | model, flight_no, max_payload, volume_capacity, range, deliverable_categories, status | 可配送品类限制 |
| **DeliveryTask (配送任务)** | order_id, package_id, warehouse, target_ship, drone_id, status | 关联订单与无人机 |
| **CustomsSyncRecord (海关同步记录)** | order_id, node_type, request_body, response_body, status, retry_count | 完整的请求/响应日志 |

---

## 编码规范与命名约定

### Vue / TypeScript（前端）

- **文件命名**: 组件使用 PascalCase (`ProductCard.vue`)，工具函数和 API 使用 camelCase (`useCart.ts`, `orderApi.ts`)
- **组件命名**: 使用 PascalCase，且至少两个单词 (`ProductList`, `OrderDetail`)
- **变量命名**: camelCase (`orderList`, `isLoading`)
- **常量命名**: UPPER_SNAKE_CASE (`MAX_WEIGHT`, `API_BASE_URL`)
- **CSS 类名**: kebab-case (`product-card`, `order-list`)
- **Composition API**: 强制使用 `<script setup lang="ts">`，不使用 Options API
- **类型定义**: 接口使用 `I` 前缀 (`IProduct`, `IOrder`)，类型使用 `T` 前缀 (`TOrderStatus`)
- **API 请求**: 统一通过 `api/` 目录管理，按业务模块分文件 (`productApi.ts`, `orderApi.ts`)
- **国际化 key**: 使用点分层命名 (`product.detail.title`, `order.status.pending`)

### Java / Spring Boot（后端）

- **包命名**: `com.tsingtaohui.模块名`，全小写
- **类命名**: PascalCase
  - Controller: `XxxController`
  - Service 接口: `IXxxService`，实现: `XxxServiceImpl`
  - Mapper: `XxxMapper`
  - Entity: `XxxEntity`
  - DTO: `XxxDTO`
  - VO: `XxxVO`
- **方法命名**: camelCase，动词开头 (`createOrder`, `findByShipNo`)
- **REST API 路径**: kebab-case，复数形式 (`/api/v1/orders`, `/api/v1/delivery-tasks`)
- **配置项**: kebab-case (`spring.datasource.url`, `tsingtaohui.drone.api-base-url`)
- **数据库字段**: snake_case (`order_id`, `created_at`, `ship_no`)

### SQL / 数据库

- **表名**: snake_case，单数形式 (`t_order`, `t_product`, `t_delivery_task`)
- **表前缀**: 使用 `t_` 前缀
- **字段名**: snake_case (`order_id`, `created_at`)
- **索引命名**: `idx_表名_字段名` (`idx_order_ship_no`)
- **外键命名**: `fk_表名_关联表名` (`fk_order_user`)

### Markdown 文档

- 使用清晰的标题和简短段落
- 有序流程使用编号列表，分组事实使用无序列表
- 文件名使用 kebab-case (`2026-05-22-bonded-warehouse-ship-drone-delivery-product-spec.md`)
- 保持术语一致性：使用 "H5"、"保税仓"、"船舶代理人"、"无人机"、"海关同步" 等产品规格书中确立的术语

---

## 提交规范 (Commit Style)

使用简洁的祈使句式中文摘要：

```
feat: add product list page for h5-client
fix: resolve order status sync issue in warehouse-client
docs: update customs integration spec
refactor: extract drone dispatch service
chore: upgrade Element Plus to 2.x
```
以上为示例，请使用中文

### Commit 类型

| 类型 | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | 修复 Bug |
| `docs` | 文档变更 |
| `refactor` | 代码重构（不改变功能） |
| `chore` | 构建、依赖等杂项变更 |
| `test` | 测试相关 |
| `style` | 代码格式化（不影响逻辑） |

格式：`类型(可选作用域): 描述`，例如 `feat(h5-client): add shopping cart page`

---

## 测试规范

### 前端测试

- **单元测试**: Vitest + @vue/test-utils
- **测试文件**: 与源文件同目录，命名为 `*.spec.ts` 或 `*.test.ts`
- **覆盖率**: 核心业务逻辑（购物车计算、订单状态流转、表单校验）必须有单元测试

### 后端测试

- **单元测试**: JUnit 5 + Mockito
- **集成测试**: Spring Boot Test + H2 内存数据库
- **测试文件**: 在 `src/test/java/` 下保持与 `main` 相同的包结构
- **覆盖率**: Service 层核心逻辑必须有单元测试

### 文档变更验证

- 链接和文件路径正确
- 不包含 `TODO`、`TBD`、`待定` 等占位符
- 不与现有产品规格书产生矛盾

---

## 安全注意事项

- **禁止提交**: credentials、API keys、私有 token、真实客户数据
- **密码存储**: 使用 BCrypt 加密，禁止明文存储
- **API 认证**: 使用 JWT token，设置合理的过期时间
- **敏感接口**: 海关同步接口、无人机 API 等必须配置访问控制
- **产品文档**: 可以描述敏感集成（如海关接口、token 行为），但不得暴露真实端点、密钥或生产标识
- **数据库**: 生产环境敏感字段需加密存储
- **CORS**: 严格配置跨域策略，仅允许已知域名

---

## AI 编码行为准则

以下准则偏向谨慎而非速度。对于简单任务，可自行判断。

### 1. 先思考再编码 (Think Before Coding)

**不要假设。不要隐藏困惑。暴露权衡。**

在开始实现之前：
- 明确陈述你的假设。如果不确定，直接问。
- 如果存在多种解读，全部列出 — 不要悄悄选一个。
- 如果存在更简单的方案，说出来。必要时提出反对意见。
- 如果有不清楚的地方，停下来。指出困惑点。提问。

### 2. 简洁优先 (Simplicity First)

**解决问题的最少代码。不做投机性开发。**

- 不添加未被要求的功能。
- 不为只用一次的代码创建抽象。
- 不添加未被要求的"灵活性"或"可配置性"。
- 不为不可能的场景添加错误处理。
- 如果你写了 200 行代码但其实 50 行就够了，重写。

自问："资深工程师会说这太复杂了吗？" 如果会，简化。

### 3. 精准修改 (Surgical Changes)

**只修改必须改的。只清理自己造成的混乱。**

编辑已有代码时：
- 不要"顺便改进"相邻的代码、注释或格式。
- 不要重构没有问题的代码。
- 匹配已有风格，即使你会用不同方式实现。
- 如果发现无关的死代码，提一下 — 但不要删除。

当你的修改导致孤立代码时：
- 删除因**你的修改**而变得无用的 import/变量/函数。
- 不要删除之前就存在的死代码，除非被要求。

检验标准：每一行修改都应能直接追溯到用户的需求。

### 4. 目标驱动执行 (Goal-Driven Execution)

**定义成功标准。循环直到验证通过。**

将任务转化为可验证的目标：
- "添加校验" → "为无效输入编写测试，然后让测试通过"
- "修复 Bug" → "编写复现 Bug 的测试，然后让测试通过"
- "重构 X" → "确保重构前后测试都通过"

对于多步骤任务，给出简要计划：
```
1. [步骤] → 验证: [检查项]
2. [步骤] → 验证: [检查项]
3. [步骤] → 验证: [检查项]
```

强的成功标准让你可以独立循环。弱的标准（"让它能用"）需要不断澄清。

---

**这些准则在以下情况下是有效的：** diff 中不必要的修改更少，因过度复杂化导致的重写更少，澄清性问题在实现之前而非犯错之后提出。
