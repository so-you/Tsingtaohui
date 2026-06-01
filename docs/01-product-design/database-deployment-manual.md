# 数据库部署手册

版本：V1.0
日期：2026-06-01
适用范围：预生产 / 生产环境

---

## 1. 部署架构

```
┌─────────────────────────────────────────────────┐
│                  云服务器 (Docker)                 │
│                                                   │
│  ┌──────────┐  ┌──────────┐  ┌────────────────┐ │
│  │  MySQL   │  │  Redis   │  │  Spring Boot   │ │
│  │  8.0     │  │  7       │  │  (Flyway 迁移)  │ │
│  │  :3306   │  │  :6379   │  │  :8080         │ │
│  └────┬─────┘  └────┬─────┘  └───────┬────────┘ │
│       │              │               │           │
│       └──────────────┴───────────────┘           │
│              数据卷持久化                          │
│         mysql_data / redis_data                   │
└─────────────────────────────────────────────────┘
```

**关键设计决策**：
- MySQL 和 Redis 端口仅绑定 `127.0.0.1`，不暴露到公网
- 数据库初始化由 Flyway 在应用启动时自动执行，无需手动跑 SQL
- 数据文件通过 Docker 命名卷持久化，容器销毁不会丢数据
- 所有连接密码通过 `.env` 文件注入，不入代码仓库

---

## 2. 部署前准备

### 2.1 服务器最低配置

| 资源 | 预生产 | 生产 |
|------|--------|------|
| CPU | 2 核 | 4 核 |
| 内存 | 4 GB | 8 GB |
| 磁盘 | 40 GB SSD | 100 GB SSD |
| 操作系统 | CentOS 7+ / Ubuntu 20.04+ | 同左 |

### 2.2 依赖清单

| 软件 | 版本 | 用途 |
|------|------|------|
| Docker | 24.x+ | 容器运行时 |
| Docker Compose | 2.x+ | 服务编排 |
| Git | 2.x+ | 拉取代码 |

### 2.3 网络要求

服务器需开放以下出站端口用于拉取镜像：

| 端口 | 目标 | 用途 |
|------|------|------|
| 443 | Docker Hub (registry-1.docker.io) | 拉取 MySQL/Redis 镜像 |
| 443 | GitHub / Git 仓库 | 拉取代码 |

**入站端口仅需开放应用端口（通常 80/443 由 Nginx 转发），数据库端口不对外。**

---

## 3. 安装 Docker 环境

### CentOS / RHEL

```bash
# 卸载旧版本（如有）
sudo yum remove docker docker-client docker-client-latest docker-common \
  docker-latest docker-latest-logrotate docker-logrotate docker-engine

# 安装 Docker
curl -fsSL https://get.docker.com | sudo sh

# 启动并设置开机自启
sudo systemctl enable docker
sudo systemctl start docker

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
  -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证
docker --version
docker-compose --version
```

### Ubuntu / Debian

```bash
curl -fsSL https://get.docker.com | sudo sh
sudo systemctl enable docker && sudo systemctl start docker

sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
  -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

---

## 4. 项目部署

### 4.1 拉取代码

```bash
# 创建部署目录
sudo mkdir -p /opt/tsingtaohui
sudo chown $USER:$USER /opt/tsingtaohui

# 拉取代码
cd /opt
git clone <your-repo-url> tsingtaohui
cd tsingtaohui
```

### 4.2 生成密钥

```bash
# 生成三个独立密钥，分别记录
echo "JWT_SECRET:   $(openssl rand -base64 64)"
echo "DB_PASSWORD:  $(openssl rand -base64 32 | tr -d '/+=')"
echo "REDIS_PASS:   $(openssl rand -base64 32 | tr -d '/+=')"
```

**将输出保存到安全位置（密码管理器），后续步骤需要用到。**

### 4.3 配置环境变量

```bash
cd /opt/tsingtaohui
cp .env.example .env
vim .env
```

填写以下必填项：

| 变量 | 说明 | 示例值 |
|------|------|--------|
| `DB_PASSWORD` | MySQL root 密码 | 第 4.2 步生成的 |
| `REDIS_PASSWORD` | Redis 密码 | 第 4.2 步生成的 |
| `JWT_SECRET` | JWT 签名密钥 | 第 4.2 步生成的 |
| `CORS_ALLOWED_ORIGINS` | 前端域名白名单 | `https://h5.example.com,https://admin.example.com` |

### 4.4 调整 MySQL 配置（按需）

`mysql/conf.d/custom.cnf` 文件预置了 2C4G 服务器的推荐配置。根据实际服务器规格调整：

```ini
# 4C8G 服务器建议调整：
innodb_buffer_pool_size = 5G    # 内存的 60-70%
max_connections = 400
```

**`innodb_buffer_pool_size` 是最关键的参数** — 设太小查询慢，设太大会导致 OOM。公式：`可用内存 × 0.7 - 其他容器占用`。

---

## 5. 启动服务

### 5.1 首次启动

```bash
cd /opt/tsingtaohui
docker-compose up -d
```

首次启动耗时约 3-8 分钟，包含：
1. 拉取 `mysql:8.0` 和 `redis:7-alpine` 镜像（约 2 分钟，仅首次）
2. 构建后端镜像 — Maven 下载依赖 + 打包（约 3-5 分钟，仅首次）
3. MySQL 初始化数据库
4. Flyway 执行 13 个迁移脚本建表
5. 后端服务启动

### 5.2 观察启动过程

```bash
# 查看所有容器状态（等待 healthy）
docker-compose ps

# 实时查看后端日志（确认 Flyway 迁移和启动成功）
docker-compose logs -f backend
```

### 5.3 启动成功标志

后端日志中应看到：

```
Flyway ... Successfully applied 13 migrations
...
Started TsingtaohuiApplication in X.XXX seconds
```

确认容器全部健康：

```bash
$ docker-compose ps
NAME                  STATUS
tsingtaohui-mysql     Up (healthy)
tsingtaohui-redis     Up (healthy)
tsingtaohui-backend   Up
```

---

## 6. 数据库验证

### 6.1 连接检查

```bash
# 进入 MySQL 容器
docker exec -it tsingtaohui-mysql mysql -u root -p"${DB_PASSWORD}" tsingtaohui

# 查看已创建的表
SHOW TABLES;

# 预期输出（13 张表）：
# t_audit_log
# t_customs_sync_record
# t_delivery_task
# t_drone
# t_inventory
# t_order
# t_order_item
# t_package
# t_product
# t_product_category
# t_rule_config
# t_ship
# t_ship_agent
# t_user
# flyway_schema_history   ← Flyway 版本记录表
```

### 6.2 验证 Flyway 迁移记录

```sql
SELECT version, description, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;
```

应显示 13 条记录，全部 `success=1`。

### 6.3 验证种子数据

```sql
-- 确认商品分类有数据
SELECT COUNT(*) FROM t_product_category;

-- 确认规则配置有数据
SELECT COUNT(*) FROM t_rule_config;
```

---

## 7. 数据库备份

### 7.1 全量备份脚本

创建 `/opt/tsingtaohui/scripts/backup.sh`：

```bash
#!/bin/bash
set -e

BACKUP_DIR="/opt/backups/mysql"
RETENTION_DAYS=30
DB_NAME="tsingtaohui"
SOURCE="$(dirname "$(dirname "$(realpath "$0")")")/.env"

# 读取密码
source "$SOURCE"
BACKUP_FILE="${BACKUP_DIR}/${DB_NAME}-$(date +%Y%m%d-%H%M%S).sql.gz"

mkdir -p "$BACKUP_DIR"

docker exec tsingtaohui-mysql mysqldump \
  -u root -p"${DB_PASSWORD}" \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  "$DB_NAME" | gzip > "$BACKUP_FILE"

# 清理过期备份
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +$RETENTION_DAYS -delete

echo "Backup completed: ${BACKUP_FILE} ($(du -h "$BACKUP_FILE" | cut -f1))"
```

### 7.2 配置定时任务

```bash
chmod +x /opt/tsingtaohui/scripts/backup.sh

# 每天凌晨 3:00 执行备份
(crontab -l 2>/dev/null; echo "0 3 * * * /opt/tsingtaohui/scripts/backup.sh >> /var/log/mysql-backup.log 2>&1") | crontab -
```

### 7.3 异地备份（推荐）

```bash
# 方案 A：rsync 到另一台服务器
0 4 * * * rsync -avz /opt/backups/mysql/ user@backup-server:/backup/tsingtaohui/

# 方案 B：上传到对象存储（阿里云 OSS 示例）
0 4 * * * /opt/tsingtaohui/scripts/upload-to-oss.sh
```

---

## 8. 数据库恢复

### 8.1 从备份恢复

```bash
# 1. 停止后端（避免写入冲突）
docker-compose stop backend

# 2. 解压备份文件
gunzip /opt/backups/mysql/tsingtaohui-20260601-030000.sql.gz

# 3. 恢复到数据库
docker exec -i tsingtaohui-mysql mysql -u root -p"${DB_PASSWORD}" tsingtaohui \
  < /opt/backups/mysql/tsingtaohui-20260601-030000.sql

# 4. 重启后端
docker-compose start backend
```

### 8.2 恢复后验证

```bash
# 检查容器状态
docker-compose ps

# 确认业务表数据量正常
docker exec tsingtaohui-mysql mysql -u root -p"${DB_PASSWORD}" tsingtaohui \
  -e "SELECT TABLE_NAME, TABLE_ROWS FROM information_schema.TABLES WHERE TABLE_SCHEMA='tsingtaohui' AND TABLE_NAME LIKE 't_%';"
```

---

## 9. 常见问题

### 9.1 端口被占用

```
Error: bind: address already in use
```

```bash
# 排查占用进程
sudo lsof -i :3306
sudo lsof -i :6379

# 如果宿主机已有 MySQL/Redis 实例，修改 docker-compose.yml 端口映射
# 例如：ports: "3307:3306"
```

### 9.2 Flyway 迁移失败

```
Flyway ... Migration V20260528_001 failed
```

可能原因：数据库不是空库、字符集不匹配、权限不足。排查步骤：

```bash
# 查看 Flyway 失败详情
docker-compose logs backend | grep -A 20 "Flyway"

# 如果是数据库残留旧表，清空重建（⚠️ 仅限无数据的新部署）
docker-compose down -v   # 删除容器和数据卷
docker-compose up -d     # 重新启动
```

### 9.3 MySQL 容器无法启动

```bash
# 查看 MySQL 日志
docker-compose logs mysql

# 常见：文件权限问题 → 清理数据卷重建
docker-compose down -v
docker volume rm tsingtaohui_mysql_data
docker-compose up -d
```

### 9.4 后端无法连接数据库

```
CommunicationsException: Communications link failure
```

```bash
# 确认 MySQL 已 healthy
docker-compose ps mysql

# 确认网络互通
docker exec tsingtaohui-backend ping -c 3 mysql

# 确认密码正确
docker exec tsingtaohui-backend env | grep DB_
```

### 9.5 磁盘空间不足

MySQL 镜像和数据卷会占用约 2-5 GB。监控磁盘：

```bash
df -h /var/lib/docker
docker system df
```

---

## 10. 日常运维命令

```bash
# 查看各容器运行状态
docker-compose ps

# 查看 MySQL 慢查询日志
docker exec tsingtaohui-mysql tail -100 /var/lib/mysql/slow.log

# 查看数据库连接数
docker exec tsingtaohui-mysql mysql -u root -p"${DB_PASSWORD}" \
  -e "SHOW PROCESSLIST;"

# 查看数据卷磁盘占用
docker system df -v | grep tsingtaohui

# 平滑重启后端（不中断数据库）
docker-compose up -d --no-deps --build backend

# 全部停止
docker-compose down

# 停止并删除数据卷（⚠️ 数据会丢失）
docker-compose down -v
```

---

## 11. 安全清单

部署完成后逐项确认：

- [ ] MySQL root 密码已从 `.env.example` 的默认值更换为强密码
- [ ] Redis 密码已设置且为强密码
- [ ] JWT 密钥已更换为随机生成的 64 位以上字符串
- [ ] MySQL 端口 (3306) 未暴露到公网（`docker-compose.yml` 绑定 `127.0.0.1`）
- [ ] Redis 端口 (6379) 未暴露到公网
- [ ] `.env` 文件权限为 600：`chmod 600 /opt/tsingtaohui/.env`
- [ ] 备份脚本正常运行且备份文件可恢复
- [ ] 生产日志级别为 INFO（`application-prod.yml` 已生效）

---

## 附录 A：文件清单

| 文件 | 路径 | 用途 |
|------|------|------|
| Docker Compose | `/opt/tsingtaohui/docker-compose.yml` | 服务编排 |
| 环境变量模板 | `/opt/tsingtaohui/.env.example` | 配置参考 |
| 环境变量（实际） | `/opt/tsingtaohui/.env` | 密钥和配置 |
| MySQL 配置 | `/opt/tsingtaohui/mysql/conf.d/custom.cnf` | 字符集、缓冲池等 |
| Flyway 迁移 | `backend/src/main/resources/db/migration/` | 13 个建表脚本 |
| 生产配置 | `backend/src/main/resources/application-prod.yml` | 日志级别等 |

## 附录 B：环境变量速查

| 变量 | 用途 | 有默认值 |
|------|------|----------|
| `DB_HOST` | MySQL 主机地址 | `localhost` |
| `DB_PORT` | MySQL 端口 | `3306` |
| `DB_NAME` | 数据库名 | `tsingtaohui` |
| `DB_USER` | 数据库用户 | `root` |
| `DB_PASSWORD` | 数据库密码 | 无（必填） |
| `REDIS_HOST` | Redis 主机地址 | `localhost` |
| `REDIS_PORT` | Redis 端口 | `6379` |
| `REDIS_PASSWORD` | Redis 密码 | 无（必填） |
| `JWT_SECRET` | JWT 签名密钥 | 有开发默认值（不安全） |
| `CORS_ALLOWED_ORIGINS` | 前端跨域白名单 | `http://localhost:*` |
