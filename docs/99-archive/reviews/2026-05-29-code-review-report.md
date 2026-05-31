# 青岛世天智汇四端代码审查报告

审查日期：2026-05-29

审查范围：

- `admin-console`
- `backend`
- `h5-client`
- `warehouse-client`

审查目标：检查四端实现是否符合青岛世天智汇保税仓货物上船无人机配送业务逻辑，识别明显 Bug、安全风险、状态流转问题、接口契约不一致和测试缺口。

## 执行摘要

本次审查发现多项会影响核心履约链路的问题。最高优先级集中在后端鉴权、海关红牌阻断、配送任务落库、库存并发锁定、无人机回调幂等、签收校验，以及仓库端编译失败和 H5 扫码收货未真正落后端。

建议修复顺序：

1. 后端敏感接口鉴权、签收校验、默认密钥防护。
2. 海关红牌阻断、订单状态机、库存原子锁定。
3. 配送任务 `warehouse_id` / `target_location` 落库约束和无人机 webhook 幂等。
4. `warehouse-client` 类型错误和扫码任务匹配。
5. H5 注册船舶资料、扫码确认收货、订单列表分页过滤。
6. 管理端 RBAC、仪表盘假数据、撮合池业务动作。

## 后端 Backend

### Critical

#### 敏感接口缺少角色授权

位置：

- `backend/src/main/java/com/tsingtaohui/config/SecurityConfig.java:54`
- `backend/src/main/java/com/tsingtaohui/controller/WarehouseController.java:16`
- `backend/src/main/java/com/tsingtaohui/controller/CustomsController.java:15`
- `backend/src/main/java/com/tsingtaohui/controller/DroneController.java:16`

问题：除 `AdminController` 外，仓库、海关同步、无人机管理和无人机回调接口只有“已登录”限制，没有角色级授权。

影响：普通船员 `CUSTOMER` token 可执行仓库扫码、出库、查询库存、查询或重试海关记录、添加无人机、伪造无人机回调。

建议：仓库接口限制为仓库操作员、管理员或运营角色；海关接口限制为管理员或运营角色；无人机管理接口限制为调度或管理员角色；外部 webhook 使用独立签名、HMAC 或 IP allowlist，不使用普通用户 JWT。

#### 订单创建红牌海关同步缺失

位置：

- `backend/src/main/java/com/tsingtaohui/service/impl/OrderServiceImpl.java:125`
- `backend/src/main/java/com/tsingtaohui/service/impl/CustomsSyncServiceImpl.java:47`

问题：订单创建时没有执行 `ORDER_CREATED` 红牌海关同步；海关同步服务当前始终模拟成功。

影响：红牌节点无法真正阻断订单创建，审计记录不完整，生产环境可能绕过合规链路。

建议：订单创建事务中接入真实海关客户端；同步失败时阻断并回滚，或进入明确的人工处理状态；完整记录请求体、响应体、失败原因，并同步更新订单海关状态。

#### 配送任务创建可能违反数据库约束

位置：

- `backend/src/main/java/com/tsingtaohui/service/impl/DroneServiceImpl.java:113`
- `backend/src/main/resources/db/migration/V20260528_008__create_t_delivery_task.sql:9`

问题：创建配送任务时未设置 `warehouseId`，而数据库字段 `warehouse_id` 为 `NOT NULL`；`target_location` 也可能为空。

影响：出库后匹配到无人机时，配送任务插入可能失败，导致事务回滚，订单无法进入无人机配送。

建议：明确配送任务仓库来源，并设置 `warehouseId`；对目标位置做业务必填校验，或调整 schema 允许空值并定义补录流程。

### High

#### 库存锁定存在并发超卖风险

位置：

- `backend/src/main/java/com/tsingtaohui/service/impl/OrderServiceImpl.java:296`
- `backend/src/main/java/com/tsingtaohui/config/MyBatisPlusConfig.java:14`
- `backend/src/main/java/com/tsingtaohui/model/entity/InventoryEntity.java:15`

问题：库存锁定为先查询后 `updateById`，没有条件更新、行锁或乐观锁；`version` 字段未加 `@Version`，也未配置乐观锁插件。

影响：并发下单会造成超卖或覆盖锁定数量，`available_qty` 和 `locked_qty` 不可信。

建议：使用原子 SQL，在 `available_qty >= qty` 条件下递减可用库存并递增锁定库存，检查 affected rows；或启用 MyBatis-Plus 乐观锁并对冲突进行重试。

#### 无人机 webhook 幂等不足

位置：

- `backend/src/main/java/com/tsingtaohui/service/impl/DroneServiceImpl.java:137`
- `backend/src/main/java/com/tsingtaohui/controller/DroneController.java:41`

问题：`eventId` 未使用，仅用“状态相同则返回”作为幂等处理；没有事件去重、顺序校验或终态保护。

影响：重复但状态不同、乱序回调可能把已送达任务回退为配送中，或改成异常状态。

建议：保存回调事件表并对 `eventId` 建唯一键；定义任务状态机，终态不可回退；按事件时间或版本号丢弃旧事件。

#### 后台订单状态允许任意跳转

位置：

- `backend/src/main/java/com/tsingtaohui/service/impl/OrderServiceImpl.java:192`

问题：后台订单状态更新只校验目标状态是否在枚举中，不校验合法前置状态。

影响：管理员操作可跳过海关红牌、仓库处理、无人机配送；取消、异常、完成也不会释放库存或触发对应同步副作用。

建议：集中实现订单状态机，限制合法迁移路径，并把库存释放、海关同步、完成时间等副作用放入事务化状态迁移服务。

#### 签收校验可被伪造

位置：

- `backend/src/main/java/com/tsingtaohui/controller/OrderController.java:69`
- `backend/src/main/java/com/tsingtaohui/controller/OrderController.java:103`

问题：验证码签收接受任意 6 位数字；包裹扫码签收没有校验当前用户是否属于该订单。

影响：用户可伪造签收，或完成他人订单。

建议：生成并存储带 TTL 的签收码或二维码 token，校验哈希、订单归属、订单状态和一次性使用；包裹扫码也必须校验 `order.userId == currentUserId` 或明确的授权代理关系。

### Medium

#### 出库扣减锁定库存后未检查剩余数量

位置：

- `backend/src/main/java/com/tsingtaohui/service/impl/WarehouseServiceImpl.java:263`

问题：出库扣减 `locked_qty` 后没有检查 `remaining` 是否归零。

影响：锁定库存不足时，订单仍可能标记为 `OUTBOUND`，造成库存账实不一致。

建议：扣减后若 `remaining > 0`，直接抛错并回滚；更稳妥的做法是按订单锁定明细精确释放，不按 SKU 全局扣减。

#### 默认 JWT 密钥未被拦截

位置：

- `backend/src/main/java/com/tsingtaohui/config/JwtSecretValidator.java:21`
- `backend/src/main/resources/application.yml:42`

问题：配置里的默认 JWT secret 是 `tsingtaohui-dev-only-secret-key-do-not-use-in-production-env`，但校验器拦截的是另一个默认字符串。

影响：生产环境若未配置 `JWT_SECRET`，服务会用公开默认密钥启动。

建议：精确禁止当前默认值；生产 profile 要求必须从环境变量提供 secret，并增加启动校验测试。

## 仓库端 Warehouse Client

### High

#### 类型定义与分页响应字段不一致

位置：

- `warehouse-client/src/stores/warehouse.ts:32`
- `warehouse-client/src/types/index.ts:40`

问题：Store 读取 `res.items`，但 `IPageResult` 定义的是 `list`。`fetchPickingTasks`、`fetchReviewTasks`、`fetchOutboundTasks`、`fetchInventory` 都有同类问题。

影响：`npm run type-check` 失败；运行时列表也可能变为 `undefined`。

建议：统一分页响应字段。后端当前 `PageResult` 是 `items`，仓库端类型应改为 `items`，或在 API 层做响应归一化。

### Medium

#### 扫码永远提交第一个任务

位置：

- `warehouse-client/src/pages/picking/index.vue:43`
- `warehouse-client/src/pages/review/index.vue:34`

问题：拣货和复核扫码都使用 `store.*Tasks[0].taskId`。

影响：多任务场景下扫码会落到第一个任务；多个订单有同 SKU 时，可能确认错订单或错任务。

建议：引入明确的当前任务选择，或从扫码内容解析出任务、订单或包裹编号后再提交。

#### 出库缺少包裹扫码校验

位置：

- `warehouse-client/src/pages/outbound/index.vue:16`
- `warehouse-client/src/pages/outbound/index.vue:32`

问题：出库仅靠点击按钮确认，没有扫描包裹号或交接码。

影响：PDA 或条码枪工作流中，操作员可能误点任意未拦截任务完成出库。

建议：出库页增加包裹扫码输入，扫描 `packageNo` 匹配当前任务后再调用确认接口。

#### tabBar 页面刷新时机不足

位置：

- `warehouse-client/src/pages/picking/index.vue:50`
- `warehouse-client/src/pages/review/index.vue:50`
- `warehouse-client/src/pages/outbound/index.vue:41`

问题：页面只在 `onMounted` 拉取数据。

影响：Uni-app tab 页面缓存后，拣货、复核、打包、出库之间切换可能展示旧任务。

建议：改用 Uni-app `onShow` 刷新当前任务，必要时增加节流。

#### 库存搜索没有生效

位置：

- `warehouse-client/src/pages/inventory/index.vue:26`
- `warehouse-client/src/api/warehouse.ts:36`

问题：库存搜索忽略输入值，API 也未传 SKU、库位或批次查询参数。

影响：PDA 输入或扫描 SKU 后仍返回默认第一页，查询不可用。

建议：读取输入值并传给 `getInventory`，API 支持 `skuCode`、`location`、`batch` 等查询参数。

## H5 客户端 H5 Client

### High

#### 注册流程未采集船舶信息

位置：

- `h5-client/src/pages/auth/register.vue:62`
- `h5-client/src/stores/user.ts:27`

问题：注册只收集用户名、密码和确认密码，提交参数也只有 `username`、`password`、`preferredLanguage`。

影响：不满足船员注册时填写船号、国籍、IMO/MMSI 的核心流程，新用户注册后缺少下单必需资料。

建议：注册表单、API 类型、Store 同步扩展船舶字段；或注册成功后强制进入船舶信息补全页，未补全前限制下单。

#### 扫码收货未调用后端确认接口

位置：

- `h5-client/src/pages/mine/index.vue:164`
- `h5-client/src/api/order.ts:16`

问题：“扫码收货”只调用 `uni.scanCode` 并展示成功提示，没有解析二维码、没有调用确认收货接口，也没有刷新订单状态。

影响：用户看到成功提示，但后端订单仍可能停留在 `PENDING_RECEIPT`，核心收货闭环不可用。

建议：补充确认收货 API；扫码后解析订单号、包裹号或签收 token，调用后端确认接口，并处理非法码、非待收货订单和重复签收。

#### 船舶 IMO/MMSI 校验不足

位置：

- `h5-client/src/pages/mine/ship.vue:112`
- `h5-client/src/pages/cart/index.vue:198`

问题：船舶信息校验和下单前校验只要求 `shipNo`、`shipNationality`，`imo` 和 `mmsi` 可全部为空。

影响：订单可能在缺少船舶识别字段时提交，后续自动交易、海关同步或船舶识别可能失败。

建议：明确 IMO/MMSI 是二选一还是都必填，并在船舶表单、购物车提交前和后端 DTO 中保持一致。

### Medium

#### “进行中”订单列表前端分页过滤会漏单

位置：

- `h5-client/src/pages/order/index.vue:87`
- `h5-client/src/pages/order/index.vue:117`

问题：“进行中”tab 没有向后端传状态组，只拉第一页全部订单后在前端过滤。

影响：如果第一页主要是已完成或取消订单，进行中的订单可能在后续页，页面会误显示为空或漏单。

建议：后端支持状态组查询，或传多个 active 状态；同时补齐分页或加载更多逻辑。

#### i18n 覆盖不完整

位置：

- `h5-client/src/pages/home/index.vue:8`
- `h5-client/src/pages/catalog/index.vue:66`
- `h5-client/src/pages.json:74`

问题：首页、商品列表、tabBar 和 navigation 中仍有大量硬编码中文。

影响：切换英文后，关键商城文案仍显示中文，不满足 H5 必须支持中英文的要求。

建议：页面文案移入 i18n；tabBar 和导航标题在语言切换后通过 `uni.setTabBarItem` 和 `uni.setNavigationBarTitle` 同步更新。

#### 购物车库存限制不足

位置：

- `h5-client/src/pages/catalog/detail.vue:91`
- `h5-client/src/stores/cart.ts:17`

问题：无库存商品仍可加入购物车；购物车不保存库存，数量没有上限。

影响：用户可提交明显超库存订单，只能在试算或下单时失败，购物体验差且容易制造无效订单。

建议：详情页禁用无库存购买；购物车 item 保存 `availableQty`，步进器按库存上限限制，并在提交前再次校验。

## 管理端 Admin Console

### High

#### 管理端缺少前端 RBAC 控制

位置：

- `admin-console/src/router/index.ts:60`
- `admin-console/src/layouts/AdminLayout.vue:74`

问题：路由守卫只检查 `localStorage.token`，菜单和操作按钮没有基于角色或权限过滤。

影响：不符合管理端 RBAC 要求；低权限账号可进入用户、商品、订单、无人机、海关同步等页面并尝试操作。

建议：登录后拉取角色和权限；路由 `meta.permission` 配合全局守卫校验；侧边栏和页面动作按权限渲染。后端仍作为最终鉴权。

#### 仪表盘使用假数据兜底

位置：

- `admin-console/src/views/dashboard/index.vue:54`

问题：接口返回 `0` 时会被 `|| 1284/3847` 替换为假数据；请求失败时也直接展示 demo 数据。

影响：生产运营数据会被误报，接口故障被掩盖，误导订单、配送和撮合监控。

建议：移除 demo fallback；用 `?? 0` 处理空值；请求失败展示错误或空状态。

#### 撮合池缺少专用业务动作

位置：

- `admin-console/src/views/orders/index.vue:129`
- `admin-console/src/api/order.ts:8`

问题：撮合池只是普通订单 tab 和列表接口，页面动作仍是通用“确认、异常、取消”，没有撮合原因、候选库存、候选无人机、人工确认匹配、改派或拆包流程。

影响：操作员无法正确处理超大件、无人机匹配失败、库存调配等撮合订单。

建议：增加撮合详情和专用 API，例如确认匹配、拒绝、改派、选择无人机、选择仓库、拆包方案，并记录处理原因。

### Medium

#### 缺少规则配置模块

位置：

- `admin-console/src/router/index.ts:14`
- `admin-console/src/layouts/AdminLayout.vue:74`

问题：管理端核心模块缺少“规则配置”入口、API 和页面。

影响：服务时间、重量限制、体积限制、区域限制等自动交易规则无法维护。

建议：补齐规则配置模块，并纳入 RBAC 和审计。

#### 海关同步页面审计信息不足

位置：

- `admin-console/src/types/index.ts:143`
- `admin-console/src/views/customs-sync/index.vue:149`

问题：类型里有 `requestBody`、`responseBody`，但页面只展示摘要和重试按钮，没有请求响应详情、失败原因、人工处理入口。

影响：红牌阻断和黄牌告警失败时，运营无法在管理端完成审计追溯和问题定位。

建议：增加详情抽屉或弹窗，展示请求体、响应体、失败原因、重试历史，并提供人工解决流程。

#### 无人机容量校验不足

位置：

- `admin-console/src/views/drones/index.vue:25`
- `admin-console/src/views/drones/index.vue:235`

问题：新增无人机默认载重、体积、航程为 `0`，校验只做 required，输入也允许 `min=0`。

影响：可创建不可配送的无人机，后续调度和匹配逻辑可能出错。

建议：对 `maxPayloadKg`、`maxVolumeM3`、`maxRangeKm` 加 `> 0` 校验，并按业务设置合理范围。

### Low

#### 商品编辑缺少 i18n key

位置：

- `admin-console/src/views/products/index.vue:462`
- `admin-console/src/i18n/zh-CN.ts:18`

问题：商品编辑按钮使用 `t('common.save')`，但中英文 `common` 都没有 `save` key。

影响：按钮可能显示 `common.save`，影响可用性。

建议：补齐 `common.save` 的中英文翻译。

#### 部分图标未导入或未全局注册

位置：

- `admin-console/src/layouts/AdminLayout.vue:129`
- `admin-console/src/views/dashboard/index.vue:123`
- `admin-console/src/views/login/index.vue:148`

问题：模板使用了未导入或未全局注册的图标组件。

影响：运行时图标缺失并产生 Vue warning。

建议：逐个导入对应图标，或在 `main.ts` 全局注册 `@element-plus/icons-vue`。

## 验证结果

执行过的验证命令：

```bash
cd admin-console && npm run build
cd h5-client && npm run type-check
cd warehouse-client && npm run type-check
cd backend && ./mvnw test
```

结果：

- `admin-console`：构建通过；存在 chunk 体积和第三方 annotation 警告。
- `h5-client`：类型检查通过。
- `warehouse-client`：类型检查失败，错误集中在 `IPageResult` 不存在 `items` 字段。
- `backend`：测试通过，9 个测试全部通过。

## 测试缺口

- 后端缺少 MockMvc 鉴权和角色测试，尤其是普通 `CUSTOMER` 访问仓库、海关、无人机接口的拒绝用例。
- 后端缺少真实 schema 集成测试，覆盖出库、配送任务插入和 `NOT NULL` 约束。
- 后端缺少并发库存锁定测试。
- 后端缺少订单状态机测试，覆盖非法跳转、取消释放库存、完成和异常触发同步。
- 后端缺少海关红牌失败阻断、黄牌失败重试测试。
- 后端缺少无人机 webhook 重复、乱序、终态回退测试。
- 后端缺少签收码和包裹扫码的订单归属测试。
- `admin-console`、`h5-client`、`warehouse-client` 均未发现有效业务单测；需要补充前端状态管理、API 契约、关键页面交互和业务流程测试。

## 需确认问题

- 海关同步 MVP stub 是否允许保留；若允许，需要限制环境，避免生产误用。
- 无人机 webhook 合同路径是 `/webhook/drone-callback` 还是当前 `/api/v1/integrations/drone/callback`。
- 无人机 webhook 认证方式应为平台 JWT、签名、HMAC 还是 IP allowlist。
- 配送任务 `warehouse_id` 应从订单、库存批次、包裹还是仓库操作员上下文派生。
- 注册是否必须一次性采集船号、国籍、IMO/MMSI，或允许注册后强制补全。
- 确认收货二维码 payload 格式需要产品和后端统一。
- 仓库条码内容应明确为 SKU、订单号、任务号、包裹号或组合码。
