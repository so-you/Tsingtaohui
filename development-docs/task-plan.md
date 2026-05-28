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
| 1-7 | Admin Console 脚手架 + 登录/用户管理 | ⏳ 待开发 | dev/phase1-admin | — |
| 1-8 | Admin 商品管理 + 库存管理 | ⏳ 待开发 | dev/phase1-admin | — |

## 阶段 2：订单 + 购物车（核心交易闭环）

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 2-1 | 购物车 + 订单试算 | ⏳ 待开发 | dev/phase2-order | — |
| 2-2 | 订单创建 + 自动交易/匹配判断 | ⏳ 待开发 | dev/phase2-order | — |
| 2-3 | 订单列表 + 订单详情 | ⏳ 待开发 | dev/phase2-order | — |
| 2-4 | Admin 订单管理 + 匹配订单池 | ⏳ 待开发 | dev/phase2-order | — |

## 阶段 3：仓库作业 + 无人机 + 海关

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 3-1 | 仓库拣货/复核/打包 API | ⏳ 待开发 | dev/phase3-warehouse | — |
| 3-2 | 仓库出库 + 库存查询 | ⏳ 待开发 | dev/phase3-warehouse | — |
| 3-3 | Warehouse Client 脚手架 + 工作台 + 拣货复核 | ⏳ 待开发 | dev/phase3-warehouse | — |
| 3-4 | 无人机管理 + 配送任务 + 外部 API 集成 | ⏳ 待开发 | dev/phase3-drone | — |
| 3-5 | 海关同步服务 + Admin 海关管理 | ⏳ 待开发 | dev/phase3-customs | — |
| 3-6 | 收货确认 API + H5 扫码收货 | ⏳ 待开发 | dev/phase3-receipt | — |

## 阶段 4：运营完善 + 验收

| # | 任务 | 状态 | 分支 | 提交 |
|---|------|------|------|------|
| 4-1 | 审计日志 + Admin 审计查询 | ⏳ 待开发 | dev/phase4-ops | — |
| 4-2 | 对账导出 | ⏳ 待开发 | dev/phase4-ops | — |
| 4-3 | 规则配置 API + Admin 规则页 | ⏳ 待开发 | dev/phase4-ops | — |
| 4-4 | 船舶/船代管理 API + Admin 页面 | ⏳ 待开发 | dev/phase4-ops | — |
| 4-5 | 全端中英文文案补全 + 响应式适配 | ⏳ 待开发 | dev/phase4-polish | — |
| 4-6 | 端到端验收测试 | ⏳ 待开发 | dev/phase4-e2e | — |

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
