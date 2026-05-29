# 演示账号与示例数据

## 演示账号

所有演示账号密码统一为 `demo1234`，通过 Flyway 迁移 `V20260529_001__seed_demo_data.sql` 写入数据库。

### 仓库端 (Warehouse Client)

| 账号 | 角色 | 姓名 | 说明 |
|------|------|------|------|
| operator01 | WAREHOUSE_OPERATOR | 仓管员 张伟 | 主演示账号，已预填在登录页 |
| operator02 | WAREHOUSE_OPERATOR | 仓管员 李明 | 备用账号 |

访问地址: `http://localhost:5176`

### 管理端 (Admin Console)

| 账号 | 密码 | 角色 | 姓名 | 说明 |
|------|------|------|------|------|
| admin01 | demo1234 | ADMIN | 管理员 王磊 | Seed 账号 |
| admin2 | admin123 | ADMIN | — | 早期手动创建，密码不同 |
| testadmin | (未知) | CUSTOMER | — | 早期手动创建 |

访问地址: `http://localhost:5178`

### H5 客户端 (Ship Crew)

| 账号 | 角色 | 姓名 | 关联船舶 |
|------|------|------|----------|
| customer01 | CUSTOMER | Captain Johnson | Maersk Elba (IMO9876544) |
| customer02 | CUSTOMER | 船长白帆 | 中远青岛号 (IMO9876543) |

访问地址: `http://localhost:5173`

## 示例数据概览

### 船舶 (3 艘)

| 船号 | 船名 | 国籍 | 泊位/锚地 |
|------|------|------|-----------|
| COSCO-QD-001 | 中远青岛号 | CN | BERTH-A3 |
| MAERSK-001 | Maersk Elba | DK | ANCHORAGE-B2 |
| EVER-002 | 长荣二号 | CN | BERTH-C1 |

### 商品 (6 个 SKU)

| SKU | 名称 | 分类 | 单价 | 库存 |
|-----|------|------|------|------|
| SKU-COKE-330 | 可口可乐 330ml | 饮料 | ¥2.50 | 128 可用 |
| SKU-WATER-550 | 矿泉水 550ml | 饮料 | ¥1.80 | 42 可用 |
| SKU-COFFEE-200 | 即饮咖啡 200ml | 饮料 | ¥5.90 | 7 可用 |
| SKU-BISCUIT-120 | 黄油饼干 120g | 食品 | ¥3.80 | 0 可用 |
| SKU-TEA-500 | 瓶装绿茶 500ml | 饮料 | ¥3.20 | 23 可用 |
| SKU-NOODLE-110 | 方便面 110g | 食品 | ¥2.00 | 5 可用 |

### 订单 (7 笔，覆盖三种仓库状态)

**拣货阶段** (order_status=WAREHOUSE_PROCESSING)

| 订单号 | 客户 | 金额 | 仓库状态 | 商品 |
|--------|------|------|----------|------|
| TH202605290001 | Captain Johnson | ¥20.20 | 待拣货 | 可乐×4、饼干×2、绿茶×1 |
| TH202605290002 | 白帆 | ¥23.60 | 待拣货 | 咖啡×4 |
| TH202605290003 | Captain Johnson | ¥16.00 | 拣货中 | 方便面×8 |

**验货阶段** (warehouse_status=PICKED)

| 订单号 | 客户 | 金额 | 商品 |
|--------|------|------|------|
| TH202605290004 | 白帆 | ¥9.00 | 矿泉水×5 |
| TH202605290005 | Captain Johnson | ¥18.40 | 绿茶×4、可乐×2、方便面×1 |

**待出库** (order_status=PENDING_OUTBOUND)

| 订单号 | 客户 | 金额 | 海关状态 | 包裹号 |
|--------|------|------|----------|--------|
| TH202605290006 | 白帆 | ¥12.80 | 同步成功 | PKG-QD-290006 |
| TH202605290007 | Captain Johnson | ¥32.40 | 同步失败 | PKG-QD-290007 |

### 无人机 (3 架)

| 编号 | 型号 | 载重 | 航程 | 状态 |
|------|------|------|------|------|
| DRONE-QD-001 | DJI FlyCart 30 | 30kg | 15km | 可用 |
| DRONE-QD-002 | DJI FlyCart 30 | 30kg | 15km | 可用 |
| DRONE-QD-003 | SkyPort S100 | 10kg | 10km | 可用 |
