# 后端新功能部署指南

## 📦 新增功能

本次更新包含以下后端功能:

### 1. 日报管理模块
- ✅ 日报CRUD接口
- ✅ 自动统计今日客户数据
- ✅ 日报提交功能
- ✅ 管理员审核功能

### 2. 营销方案模块
- ✅ 方案CRUD接口
- ✅ 按客户/销售查询方案
- ✅ 方案状态管理(草稿/待执行/执行中/已完成/已取消)
- ✅ 方案完成记录

### 3. Excel导出功能
- ✅ 客户明细导出为Excel文件
- ✅ 使用Apache POI库
- ✅ 支持中文文件名

### 4. Dashboard统计优化
- ✅ 接入日报总数统计
- ✅ 接入待执行方案数统计

---

## 🚀 服务器部署步骤

### 步骤1: SSH登录服务器

```bash
ssh ubuntu@82.156.165.194
```

### 步骤2: 拉取最新代码

```bash
cd ~/xinhuaproject
git pull origin master
```

### 步骤3: 执行数据库迁移

**重要**: 需要先更新数据库表结构!

```bash
# 进入MySQL容器
docker exec -it xinhuaproject-mysql-1 mysql -uroot -p123456

# 在MySQL中执行迁移脚本
USE sales_customer_db;

# 方式1: 直接执行迁移SQL(会删除旧表,请确认无重要数据)
source /docker-entrypoint-initdb.d/migrate-20260427.sql;

# 或者方式2: 手动执行(推荐)
DROP TABLE IF EXISTS daily_report;
DROP TABLE IF EXISTS marketing_plan;

-- 然后复制 backend/src/main/resources/schema.sql 中的新表定义执行

# 退出MySQL
exit;
```

**更简单的方式** - 直接在服务器上执行:

```bash
# 将迁移脚本复制到MySQL容器
docker cp backend/src/main/resources/migrate-20260427.sql xinhuaproject-mysql-1:/tmp/migrate.sql

# 执行迁移
docker exec -i xinhuaproject-mysql-1 mysql -uroot -p123456 < /tmp/migrate.sql
```

### 步骤4: 编译后端代码

```bash
cd ~/xinhuaproject/backend
mvn clean package -Dmaven.test.skip=true
```

预期输出:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
```

### 步骤5: 重新构建并启动服务

```bash
cd ~/xinhuaproject

# 停止旧服务
docker-compose down

# 重新构建后端镜像(强制不使用缓存)
docker-compose build --no-cache backend

# 启动所有服务
docker-compose up -d

# 等待30秒让服务完全启动
sleep 30
```

### 步骤6: 验证服务状态

```bash
# 检查容器状态
docker-compose ps

# 查看后端日志
docker logs xinhuaproject-backend-1 --tail 50
```

预期看到类似:
```
Started CustomerApplication in X.XXX seconds
```

### 步骤7: 测试新接口

#### 测试1: Dashboard统计接口

```bash
curl http://localhost:8080/api/admin/dashboard
```

预期返回:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalCustomers": 4,
    "todayCustomers": 4,
    "totalReports": 0,
    "pendingPlans": 0
  }
}
```

#### 测试2: 日报列表接口

```bash
curl http://localhost:8080/api/admin/reports
```

预期返回:
```json
{
  "code": 200,
  "message": "success",
  "data": []
}
```

#### 测试3: 营销方案列表接口

```bash
curl http://localhost:8080/api/admin/plans
```

预期返回:
```json
{
  "code": 200,
  "message": "success",
  "data": []
}
```

#### 测试4: Excel导出接口

在浏览器访问:
```
http://82.156.165.194:8080/api/admin/export/customers
```

应该下载一个Excel文件 `客户明细_XXXXXXXXXXXX.xlsx`

---

## 📋 API接口清单

### 日报管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/reports` | 获取所有日报列表 |
| GET | `/api/admin/reports/sales/{salesId}` | 查询销售的日报列表 |
| GET | `/api/admin/reports/date?salesId=1&reportDate=2026-04-27` | 获取指定日期的日报 |
| POST | `/api/admin/reports` | 创建或更新日报 |
| POST | `/api/admin/reports/{id}/submit` | 提交日报 |
| POST | `/api/admin/reports/{id}/review?reviewerId=1&reviewerName=管理员&comment=审核通过` | 审核日报 |
| DELETE | `/api/admin/reports/{id}` | 删除日报 |

### 营销方案

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/plans` | 获取所有营销方案列表 |
| GET | `/api/admin/plans/customer/{customerId}` | 查询客户的营销方案列表 |
| GET | `/api/admin/plans/sales/{salesId}` | 查询销售的营销方案列表 |
| POST | `/api/admin/plans` | 创建营销方案 |
| PUT | `/api/admin/plans/{id}` | 更新营销方案 |
| POST | `/api/admin/plans/{id}/status?status=pending` | 更新方案状态 |
| POST | `/api/admin/plans/{id}/complete?result=执行成功` | 完成方案 |
| DELETE | `/api/admin/plans/{id}` | 删除营销方案 |

### Excel导出

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/export/customers` | 导出客户明细到Excel |

---

## ⚠️ 注意事项

### 1. 数据库迁移风险

迁移脚本会**删除旧表**,如果已有数据请先备份:

```sql
-- 备份数据
CREATE TABLE daily_report_backup AS SELECT * FROM daily_report;
CREATE TABLE marketing_plan_backup AS SELECT * FROM marketing_plan;
```

### 2. Maven依赖下载

首次构建时,Maven需要下载Apache POI依赖,可能需要几分钟时间。

### 3. 端口配置

确保服务器防火墙开放了8080端口:

```bash
sudo ufw allow 8080/tcp
```

### 4. GitHub Actions自动部署

代码已推送到GitHub,会自动触发部署。但建议手动执行以确保数据库迁移正确执行。

---

## 🔍 故障排查

### 问题1: Maven编译失败

**症状**: `BUILD FAILURE`

**解决**:
```bash
# 清理Maven缓存
mvn clean

# 重新编译
mvn package -Dmaven.test.skip=true
```

### 问题2: 数据库表不存在

**症状**: API返回错误 "Table 'daily_report' doesn't exist"

**解决**: 重新执行迁移脚本

### 问题3: Docker容器启动失败

**症状**: `docker-compose ps` 显示容器状态为 `Exit`

**解决**:
```bash
# 查看日志
docker logs xinhuaproject-backend-1

# 重新启动
docker-compose restart backend
```

### 问题4: Excel导出乱码

**症状**: 下载的Excel文件中文显示乱码

**解决**: 确保响应头设置了正确的编码:
```java
response.setCharacterEncoding("utf-8");
```

---

## ✅ 部署完成检查清单

- [ ] 代码已拉取到服务器
- [ ] 数据库迁移脚本已执行
- [ ] Maven编译成功
- [ ] Docker容器正常运行
- [ ] Dashboard接口返回正确统计数据
- [ ] 日报列表接口正常
- [ ] 营销方案列表接口正常
- [ ] Excel导出功能正常
- [ ] 前端页面可以正常访问

---

## 📞 技术支持

如遇到问题,请检查:
1. Docker容器日志: `docker logs xinhuaproject-backend-1`
2. MySQL日志: `docker logs xinhuaproject-mysql-1`
3. Nginx日志: `docker logs xinhuaproject-nginx-1`

祝部署顺利! 🎉
