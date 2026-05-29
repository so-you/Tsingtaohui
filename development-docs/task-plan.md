# 青岛汇 MVP 开发任务计划

版本：V1.0
日期：2026-05-28
分支策略：每阶段新建分支 → 开发 → 测试 → 提交 → /codex review → 修复 → 测试 → 合并 main → push

## 阶段 0：工程基础设施（已完成 ✅）

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 0-1 | 后端 Spring Boot 脚手架搭建 | ✅ 完成 | dev/init-project | ba95e78 |
| 0-2 | 数据库建表 Flyway 迁移脚本（15 张表） | ✅ 完成 | dev/init-project | ba95e78 |
| 0-3 | Redis + JWT 鉴权配置 | ✅ 完成 | dev/init-project | ba95e78 |
| 0-4 | 后端多语言错误码支持 | ✅ 完成 | dev/init-project | ba95e78 |
| 0-fix | Codex review 修复（Critical/High） | ✅ 完成 | dev/phase0-fix-review | 42b16db |

### Codex Review 修复摘要
- Critical: access/refresh token 类型区分
- Critical: JWT secret 启动校验
- High: JWT 授予 ROLE_xxx 权限
- High: 启用 CORS
- High: invalid token 返回 401
- High: DB 凭证改用环境变量
- Medium: business exception 返回 400

## 阶段 1：核心用户 + 商品模块

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 1-1 | 注册/登录 API | ✅ 完成 | dev/phase1-auth-user | b9c4810 |
| 1-2 | 用户资料 + 船舶信息 API | ✅ 完成 | dev/phase1-auth-user | f9fcd3f |
| 1-3 | 商品分类/列表/详情 API | ✅ 完成 | dev/phase1-auth-user | (subagent) |
| 1-4 | H5 客户端 Uni-app 脚手架 + 注册登录页 | ✅ 完成 | dev/phase1-h5-client | ffb006e, 5a32a8b |
| 1-5 | H5 客户端首页 + 商品列表 + 商品详情 | ✅ 完成 | dev/phase1-h5-client | 5a32a8b |
| 1-6 | H5 客户端「我的」页面 + 船舶信息维护 | ✅ 完成 | dev/phase1-h5-client | 5a32a8b |
| 1-7 | Admin Console 脚手架 + 登录/用户管理 | ✅ 完成 | dev/phase1-admin | f3c5d09 |
| 1-8 | Admin 商品管理 + 库存管理 | ✅ 完成 | dev/phase1-admin | f3c5d09 |

## 阶段 2：订单 + 购物车（核心交易闭环）

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 2-1 | 购物车 + 订单试算 | ✅ 完成 | dev/phase2-order | cfad8a8 |
| 2-2 | 订单创建 + 自动交易/匹配判断 | ✅ 完成 | dev/phase2-order | cfad8a8 |
| 2-3 | 订单列表 + 订单详情 | ✅ 完成 | dev/phase2-order | cfad8a8 |
| 2-4 | Admin 订单管理 + 匹配订单池 | ✅ 完成 | dev/phase2-order | cfad8a8 |
| 2-fix | Admin 商品列表增加商品详情编辑 | ✅ 完成 | dev/phase2-order | cfad8a8 |

### 阶段 2 验证摘要
- 后端：订单试算、订单创建锁库、Admin 匹配池、商品详情编辑、目录库存汇总测试通过。
- H5：购物车、订单提交、订单列表、订单详情通过类型检查和 H5 构建。
- Admin：订单管理、匹配订单池、商品详情编辑通过生产构建。

## 阶段 3：仓库作业 + 无人机 + 海关

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 3-1 | 仓库拣货/复核/打包 API | ✅ 完成 | dev/phase3-warehouse-drone-customs | 4e4e39c |
| 3-2 | 仓库出库 + 库存查询 | ✅ 完成 | dev/phase3-warehouse-drone-customs | 4e4e39c |
| 3-3 | Warehouse Client 脚手架 + 工作台 + 拣货复核 | ✅ 完成 | dev/phase3-warehouse-drone-customs | (本次) |
| 3-4 | 无人机管理 + 配送任务 + 外部 API 集成 | ✅ 完成 | dev/phase3-warehouse-drone-customs | 4e4e39c |
| 3-5 | 海关同步服务 + Admin 海关管理 | ✅ 完成 | dev/phase3-warehouse-drone-customs | (本次) |
| 3-6 | 收货确认 API + H5 扫码收货 | ✅ 完成 | dev/phase3-warehouse-drone-customs | 4e4e39c |

## 阶段 4：运营完善 + 验收

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 4-1 | 审计日志 + Admin 审计查询 | ✅ 完成 | worktree-phase4-ops | 68c803f |
| 4-2 | 对账导出 | ✅ 完成 | worktree-phase4-ops | 68c803f |
| 4-3 | 规则配置 API + Admin 规则页 | ✅ 完成 | worktree-phase4-ops | 68c803f |
| 4-4 | 船舶/船代管理 API + Admin 页面 | ✅ 完成 | worktree-phase4-ops | 68c803f |
| 4-5 | 全端中英文文案补全 + 响应式适配 | ⏳ 待开发 | dev/phase4-polish | — |
| 4-6 | 端到端验收测试 | ⏳ 待开发 | dev/phase4-e2e | — |

### 阶段 4 验证摘要
- 后端：编译通过、单元测试通过
- 前端：Admin Console 生产构建通过
- 新增 8 个 Admin 端点：船舶 CRUD、船代列表、规则列表/更新、审计日志查询、对账 CSV 导出
- 新增 2 个 Flyway 迁移：t_rule_config 建表 + 种子数据（4 条默认规则）
- Admin 侧边栏新增 4 个菜单项，中英文 i18n 完整覆盖

## 阶段 5：仓库端 H5 整体重写

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 5-1 | 设计系统（SCSS 设计令牌 + 仓库端色彩主题 + 公共样式） | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-2 | 通用组件（ScanInput / StatusTag / TaskCard / EmptyState） | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-3 | Mock 数据模块（dashboard / picking / review / outbound / inventory） | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-4 | 登录页（WH-001） | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-5 | 工作台重写（WH-002）+ 底部导航 | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-6 | 拣货页重写（WH-003） | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-7 | 复核打包页重写（WH-004） | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-8 | 出库页重写（WH-005） | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-9 | 库存查询页重写（WH-007） | ⏳ 待开发 | feat/warehouse-client-optimization | — |
| 5-10 | 扫码设备兼容（WH-006）+ 中英文补全（WH-008） | ⏳ 待开发 | feat/warehouse-client-optimization | — |

设计文档：`docs/superpowers/specs/2026-05-29-warehouse-client-redesign.md`

## 每阶段执行流程

1. 从 main 新建分支 `dev/phaseN-xxx`
2. 按任务清单开发
3. `mvn compile` / `npm run build` 验证编译
4. `mvn test` / `npm run test` 运行测试
5. `git commit` 提交代码
6. `/codex review` 进行 PR 代码审查
7. 修复 review 发现的问题
8. 编写测试用例，并完成所有测试和修复
9. `git checkout main && git merge dev/phaseN-xxx --no-ff` 合并到 main
10. `git push` 推送到远程
11. 更新本文档任务状态
