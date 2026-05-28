# 系统架构与模块设计

版本：V1.2  
日期：2026-05-29  

## 1. 架构目标

1. 支持 H5 客户端、仓库端 H5、管理端 Web 和后端服务协同工作。
2. 以订单为主线打通用户、商品、库存、仓库、无人机、海关和签收流程。
3. 一期采用清晰的单体后端模块化架构，降低交付复杂度。
4. 为后续免注册一船一码、支付结算、多商户和更多配送方式预留扩展点。

## 2. 技术栈

| 层级 | 技术选型 | 说明 |
|------|----------|------|
| 客户端 H5 | Uni-app + Vue 3 + TypeScript | 面向船员用户，商城式购物体验 |
| 仓库端 H5 | Uni-app + Vue 3 + TypeScript | 面向仓库作业，适配扫码枪键盘回显 |
| 管理端 Web | Vue 3 + TypeScript + Element Plus | 面向平台运营和管理人员 |
| 后端服务 | Spring Boot + JDK 17 | 提供 REST API、业务规则、外部系统集成 |
| 数据库 | MySQL 8.x | 存储业务主数据、交易数据和审计数据 |
| 缓存 | Redis | 会话、验证码、热点配置、幂等键和短期状态缓存 |
| 文件存储 | 对象存储或本地文件服务 | 商品图片、导出文件和出库单文件 |
| API 文档 | OpenAPI 3 | 后端接口契约和联调基线 |
| 构建部署 | Maven、npm、Docker | 支持本地开发、测试环境和生产环境部署 |

## 3. 端到端架构

```text
用户 H5(Uni-app)       仓库 H5(Uni-app)       管理端(Vue3 + Element Plus)
      |                       |                         |
      +----------- HTTPS REST API / JWT 鉴权 ------------+
                              |
                    Spring Boot 后端服务
                              |
      +----------+------------+-------------+------------+
      |          |            |             |            |
    MySQL      Redis       文件存储       海关系统      无人机系统
                                            |            |
                                      同步请求/响应     状态查询/派发/回调
```

## 4. 应用划分

### 4.1 客户端 H5

面向船员和货轮用户，采用购物商城风格。

核心模块：

1. 注册登录模块。
2. 首页模块。
3. 商品模块。
4. 购物车模块。
5. 订单模块。
6. 我的模块。
7. 扫码收货模块。
8. 多语言模块。

底部导航：

1. 首页。
2. 商品。
3. 订单。
4. 我的。

### 4.2 仓库端 H5

面向保税仓操作员，强调高效率和扫码反馈。

核心模块：

1. 仓库登录。
2. 仓库工作台。
3. 拣货任务。
4. 复核打包。
5. 出库交接。
6. 库存查询。
7. 扫码输入适配。
8. 多语言模块。

### 4.3 管理端 Web

面向平台运营、无人机调度、财务和系统管理员。

核心模块：

1. 登录与权限。
2. 运营仪表盘。
3. 用户管理。
4. 船舶和船舶代理人管理。
5. 商品管理。
6. 库存管理。
7. 订单管理。
8. 匹配订单池。
9. 无人机管理。
10. 规则配置。
11. 海关同步管理。
12. 对账导出。
13. 审计日志。

## 5. 后端模块设计

一期后端采用一个 Spring Boot 应用，包结构和类命名必须与 `CLAUDE.md` 保持一致：根包为 `com.tsingtaohui`，按 `controller`、`service`、`mapper`、`model`、`config`、`common`、`integration` 分层组织；业务模块通过类名、子包或服务接口表达边界。模块之间通过 Service 接口调用，不直接跨模块操作数据库表。

| 模块 | 职责 |
|------|------|
| auth | 注册、登录、退出、JWT、密码哈希、角色权限 |
| user | 用户资料、用户状态、个人船舶信息 |
| ship | 船舶主数据、船号、船籍、IMO、MMSI、船舶代理人 |
| catalog | 商品分类、商品、SKU、上下架、图片 |
| inventory | 仓库、库位、批次、可用库存、锁定库存、库存流水 |
| order | 购物车结算、订单创建、订单状态机、交易模式判断 |
| warehouse | 拣货、复核、打包、包裹、出库交接 |
| drone | 无人机资料、匹配规则、配送任务、外部无人机 API |
| customs | 海关同步节点、红牌拦截、黄牌警告、重试和日志 |
| rules | 自动交易、匹配订单、无人机匹配和异常处理规则 |
| audit | 操作日志、状态变更日志、审计查询 |
| i18n | 字典、枚举显示名和后端错误码多语言 |
| file | 图片、出库单、导出文件 |

后端命名规则：

1. Controller 使用 `XxxController`，例如 `OrderController`。
2. Service 接口使用 `IXxxService`，实现类使用 `XxxServiceImpl`。
3. Mapper 使用 `XxxMapper`。
4. Entity 使用 `XxxEntity`，DTO 使用 `XxxDTO`，VO 使用 `XxxVO`。
5. REST API 路径使用 kebab-case 和复数形式，例如 `/api/v1/delivery-tasks`。
6. 数据库字段使用 snake_case。

后端类与包映射示例：

| 业务域 | Controller | Service | Mapper | Entity |
|--------|------------|---------|--------|--------|
| 认证 | `com.tsingtaohui.controller.AuthController` | `com.tsingtaohui.service.IAuthService` / `com.tsingtaohui.service.impl.AuthServiceImpl` | `com.tsingtaohui.mapper.UserMapper` | `com.tsingtaohui.model.entity.UserEntity` |
| 用户 | `com.tsingtaohui.controller.UserController` | `com.tsingtaohui.service.IUserService` / `com.tsingtaohui.service.impl.UserServiceImpl` | `com.tsingtaohui.mapper.UserMapper` | `com.tsingtaohui.model.entity.UserEntity` |
| 商品 | `com.tsingtaohui.controller.CatalogController` | `com.tsingtaohui.service.ICatalogService` / `com.tsingtaohui.service.impl.CatalogServiceImpl` | `com.tsingtaohui.mapper.ProductMapper` | `com.tsingtaohui.model.entity.ProductEntity` |
| 订单 | `com.tsingtaohui.controller.OrderController` | `com.tsingtaohui.service.IOrderService` / `com.tsingtaohui.service.impl.OrderServiceImpl` | `com.tsingtaohui.mapper.OrderMapper` | `com.tsingtaohui.model.entity.OrderEntity` |
| 管理 | `com.tsingtaohui.controller.AdminController` | `com.tsingtaohui.service.IAdminService` / `com.tsingtaohui.service.impl.AdminServiceImpl` | `com.tsingtaohui.mapper.UserMapper` 等 | 多个 |
| 仓库 | `com.tsingtaohui.controller.WarehouseController` | `com.tsingtaohui.service.IWarehouseService` / `com.tsingtaohui.service.impl.WarehouseServiceImpl` | `com.tsingtaohui.mapper.OrderMapper` 等 | 多个 |
| 无人机 | `com.tsingtaohui.controller.DroneController` | `com.tsingtaohui.service.IDroneService` / `com.tsingtaohui.service.impl.DroneServiceImpl` | `com.tsingtaohui.mapper.DroneMapper` | `com.tsingtaohui.model.entity.DroneEntity` |
| 海关同步 | `com.tsingtaohui.controller.CustomsController` | `com.tsingtaohui.service.ICustomsSyncService` / `com.tsingtaohui.service.impl.CustomsSyncServiceImpl` | `com.tsingtaohui.mapper.CustomsSyncRecordMapper` | `com.tsingtaohui.model.entity.CustomsSyncRecordEntity` |

## 6. 核心业务服务

### 6.1 订单服务

职责：

1. 订单创建。
2. 订单金额、重量和体积计算。
3. 订单状态流转。
4. 自动交易和匹配订单判断。
5. 库存锁定和释放。
6. 与海关同步、仓库作业、无人机配送任务的编排。

### 6.2 库存服务

职责：

1. 查询可用库存。
2. 锁定订单库存。
3. 仓库出库扣减库存。
4. 订单取消释放库存。
5. 写入库存流水。

库存并发控制：

1. 使用数据库事务保证锁定和扣减原子性。
2. 对 SKU、仓库、批次维度库存行使用乐观锁或行级锁。
3. 所有库存变更必须写入流水。

### 6.3 海关同步服务

职责：

1. 根据订单节点生成同步记录。
2. 调用海关接口。
3. 保存请求、响应、状态和失败原因。
4. 对红牌节点返回阻塞结果。
5. 对黄牌节点执行告警和自动重试。

红牌节点：

1. 订单创建。
2. 仓库出库。

黄牌节点：

1. 订单确认。
2. 配送任务创建。
3. 无人机装载。
4. 配送中。
5. 已送达。
6. 已签收。
7. 订单取消。
8. 订单异常。

### 6.4 无人机服务

职责：

1. 维护无人机型号和架次。
2. 查询外部无人机状态。
3. 根据重量、体积、航程、品类、状态和时间匹配无人机。
4. 创建配送任务。
5. 向无人机系统派发任务。
6. 接收并幂等处理无人机回调。

平台不直接控制飞控动作，飞行安全、航线、起降、避障和返航由无人机系统负责。

## 7. 权限模型

### 7.1 账号类型

| 类型 | 说明 |
|------|------|
| CUSTOMER | H5 客户端用户 |
| WAREHOUSE_OPERATOR | 仓库端操作员 |
| ADMIN | 管理端管理员 |
| OPERATOR | 平台运营人员 |
| DRONE_DISPATCHER | 无人机调度人员 |
| FINANCE | 对账和导出人员 |

### 7.2 权限原则

1. 客户端用户只能查看和操作自己的订单。
2. 仓库操作员只能处理所属仓库或被授权仓库的任务。
3. 管理端基于角色控制菜单和按钮权限。
4. 海关同步、规则变更、库存调整、订单取消和异常处理必须记录审计日志。

## 8. 多语言架构

### 8.1 前端多语言

1. 客户端和仓库端使用 Uni-app i18n 字典。
2. 管理端使用 Vue i18n。
3. 三端所有页面、菜单、按钮、表单、状态、校验和操作反馈都必须使用多语言 key。
4. 文案 key 以模块命名，例如 `order.status.pendingReceipt`。
5. 用户语言选择保存在本地缓存和用户资料中。

### 8.2 后端多语言

1. 后端错误码固定，错误消息支持中文和英文。
2. API 根据 `Accept-Language` 或用户资料语言返回展示消息。
3. 枚举编码不翻译，前端按字典渲染显示名。

## 9. 集成边界

### 9.1 海关系统

1. 后端统一封装海关接口客户端。
2. 所有请求和响应落库。
3. 接口地址、鉴权方式、超时时间和重试次数使用服务端配置。
4. 前端不直接调用海关接口。

### 9.2 无人机系统

1. 后端调用无人机状态查询和任务派发接口。
2. 后端提供无人机回调地址。
3. 回调必须使用任务编号和回调事件编号做幂等处理。
4. 回调失败可重放，不得造成订单状态倒退。

### 9.3 船位数据

一期以用户填写和管理端维护的船舶信息为主。后续接入 ShipXY 和 MarineTraffic 时，第三方船位数据仅作为候选位置，最终配送位置必须由用户或运营确认。

## 10. 推荐代码结构

### 10.1 后端

```text
backend/
  pom.xml
  Dockerfile
  src/main/java/com/tsingtaohui/
    TsingtaohuiApplication.java
    controller/
      AuthController.java
      UserController.java
      CatalogController.java
      CartController.java
      OrderController.java
      AdminController.java
      WarehouseController.java
      DroneController.java
      CustomsController.java
    service/
      IAuthService.java
      IUserService.java
      ICatalogService.java
      IOrderService.java
      IAdminService.java
      IWarehouseService.java
      IDroneService.java
      ICustomsSyncService.java
      impl/
        AuthServiceImpl.java
        UserServiceImpl.java
        CatalogServiceImpl.java
        OrderServiceImpl.java
        AdminServiceImpl.java
        WarehouseServiceImpl.java
        DroneServiceImpl.java
        CustomsSyncServiceImpl.java
    mapper/
      UserMapper.java
      UserProfileMapper.java
      UserShipMapper.java
      ProductMapper.java
      ProductCategoryMapper.java
      InventoryMapper.java
      OrderMapper.java
      OrderItemMapper.java
      PackageMapper.java
      DroneMapper.java
      DeliveryTaskMapper.java
      CustomsSyncRecordMapper.java
    model/
      entity/
        BaseEntity.java
        UserEntity.java
        UserProfileEntity.java
        UserShipEntity.java
        ProductEntity.java
        ProductCategoryEntity.java
        InventoryEntity.java
        OrderEntity.java
        OrderItemEntity.java
        PackageEntity.java
        DroneEntity.java
        DeliveryTaskEntity.java
        CustomsSyncRecordEntity.java
      dto/
        RegisterDTO.java
        LoginDTO.java
        UpdateProfileDTO.java
        UpdateShipDTO.java
        CartEstimateDTO.java
        CreateOrderDTO.java
        OrderItemDTO.java
        UpdateProductDTO.java
        UpdateStatusDTO.java
      vo/
        AuthVO.java
        UserVO.java
        UserProfileVO.java
        ShipVO.java
        CategoryVO.java
        ProductListVO.java
        ProductDetailVO.java
        OrderEstimateVO.java
        OrderVO.java
        OrderItemVO.java
        AdminProfileVO.java
        AdminUserVO.java
        AdminProductVO.java
        AdminInventoryVO.java
    config/
      SecurityConfig.java
      CorsConfig.java
      JwtProperties.java
      JwtSecretValidator.java
      MyBatisPlusConfig.java
      MyBatisPlusMetaObjectHandler.java
    common/
      context/UserContextHolder.java
      enums/ErrorCode.java
      enums/OrderStatus.java
      enums/TradeMode.java
      enums/UserType.java
      enums/CustomsSyncNode.java
      enums/CustomsSyncStatus.java
      exception/BusinessException.java
      exception/GlobalExceptionHandler.java
      model/ApiResponse.java
      model/PageResult.java
      model/UserContext.java
      util/JwtUtil.java
  src/main/resources/
    application.yml
    db/migration/
      V20260528_001__create_t_user.sql
      V20260528_002__create_t_ship_and_agent.sql
      V20260528_003__create_t_product.sql
      V20260528_004__create_t_inventory.sql
      V20260528_005__create_t_order.sql
      V20260528_006__create_t_package.sql
      V20260528_007__create_t_drone.sql
      V20260528_008__create_t_delivery_task.sql
      V20260528_009__create_t_customs_sync_record.sql
      V20260528_010__create_t_audit_log.sql
```

### 10.2 客户端 H5

```text
h5-client/
  package.json
  pages.json
  manifest.json
  src/
    pages/
      home/
      catalog/
      order/
      mine/
      auth/
    components/
    api/
    stores/
    i18n/
    utils/
    static/
```

### 10.3 仓库端 H5

```text
warehouse-client/
  package.json
  pages.json
  manifest.json
  vite.config.ts
  tsconfig.json
  index.html
  src/
    main.ts
    App.vue
    pages/
      dashboard/
      picking/
      review/
      outbound/
      inventory/
    api/
      warehouse.ts
    stores/
      warehouse.ts
    types/
      index.ts
    i18n/
      index.ts
      zh-CN.ts
      en-US.ts
    utils/
      request.ts
```

### 10.4 管理端

```text
admin-console/
  package.json
  vite.config.ts
  tsconfig.json
  src/
    views/
      dashboard/
      users/
      products/
      orders/
      drones/
      customs-sync/
      login/
    components/
    stores/
      auth.ts
      user.ts
      product.ts
      order.ts
    api/
      auth.ts
      user.ts
      product.ts
      order.ts
      drone.ts
      customs.ts
    router/
      index.ts
    layouts/
      AdminLayout.vue
    i18n/
      index.ts
      zh-CN.ts
      en-US.ts
    types/
      index.ts
    utils/
      request.ts
    styles/
    assets/
```

## 11. 部署拓扑

```text
Nginx
  |-- /client/       -> 客户端 H5 静态资源
  |-- /warehouse/    -> 仓库端 H5 静态资源
  |-- /admin/        -> 管理端静态资源
  |-- /api/          -> Spring Boot 后端服务

Spring Boot
  |-- MySQL
  |-- Redis
  |-- 文件存储
  |-- 海关系统
  |-- 无人机系统
```

## 12. 关键设计约束

1. 客户端、仓库端和管理端不得保存任何第三方接口密钥。
2. 用户密码不得明文存储或写入日志。
3. 海关同步红牌节点必须在业务服务层阻塞，不只依赖前端按钮禁用。
4. 库存锁定和扣减必须在事务中完成。
5. 无人机回调必须幂等。
6. 所有状态枚举以数据库和后端枚举为准，前端只负责显示翻译。
