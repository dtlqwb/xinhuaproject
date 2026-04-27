# 管理端API修复 - 快速部署指南

## 🔍 问题诊断

### 当前状态
- ✅ `http://82.156.165.194` (销售端) - 正常访问
- ❌ `http://82.156.165.194/admin` - 空白(无Nginx配置)
- ⚠️ `http://82.156.165.194:81` (管理端) - 能打开但**没数据**

### 根本原因
管理前端在生产环境(Docker)中,API请求地址配置错误:
- **错误**: 请求 `http://82.156.165.194:81/api/...` (自己端口,找不到后端)
- **正确**: 应该请求 `http://82.156.165.194:8080/api/...` (后端端口)

---

## 🛠️ 修复方案

已修改文件: `admin-frontend/src/utils/request.ts`

```typescript
// 生产环境使用服务器IP,开发环境使用代理
const API_BASE_URL = import.meta.env.PROD 
  ? 'http://82.156.165.194:8080/api' 
  : '/api'
```

---

## 📋 部署步骤

### 方法1: 手动执行(推荐)

在您的本地终端执行:

```bash
# 1. SSH连接
ssh ubuntu@82.156.165.194

# 2. 进入项目目录
cd ~/xinhuaproject

# 3. 拉取最新代码
git pull origin master

# 4. 重新构建管理前端
docker-compose up -d --build admin-frontend

# 5. 重启后端
docker-compose restart backend

# 6. 等待15秒
sleep 15

# 7. 检查容器状态
docker-compose ps

# 8. 退出
exit
```

### 方法2: 使用PowerShell脚本

```powershell
.\fix-admin-api.ps1
```

---

## ✅ 验证测试

部署完成后,请按以下步骤测试:

### 测试1: 管理端登录
1. 访问: `http://82.156.165.194:81`
2. 输入账号: `admin` / `123456`
3. 点击登录

### 测试2: 客户列表
1. 点击左侧菜单"客户管理"
2. **预期结果**: 能看到销售端录入的客户数据
3. 尝试搜索功能(输入姓名/公司/手机)

### 测试3: 客户详情
1. 点击任意客户卡片
2. **预期结果**: 弹出详情弹窗,显示完整信息

---

## 🐛 如果仍然没数据

### 检查1: 浏览器控制台
按 `F12` 打开开发者工具,查看:
- **Network标签**: 看API请求是否成功(状态码200)
- **Console标签**: 看是否有错误信息

### 检查2: 后端日志
```bash
ssh ubuntu@82.156.165.194
docker logs sales-backend --tail 50
```

### 检查3: 数据库数据
```bash
ssh ubuntu@82.156.165.194
docker exec -it sales-mysql mysql -uroot -proot123 sales_customer_db -e "SELECT COUNT(*) FROM customer;"
```

### 常见问题

#### 问题1: CORS跨域错误
**症状**: Console显示 `Access-Control-Allow-Origin` 错误  
**解决**: 后端Controller已配置 `@CrossOrigin`,应该没问题

#### 问题2: 401 Unauthorized
**症状**: API返回401  
**解决**: 确认管理员登录成功,token已保存

#### 问题3: 网络超时
**症状**: 请求长时间pending  
**解决**: 检查后端容器是否正常运行 `docker-compose ps`

---

## 📊 预期结果

| 测试项 | 预期结果 |
|--------|---------|
| 管理端访问 | ✅ `http://82.156.165.194:81` 能打开 |
| 管理员登录 | ✅ 使用 admin/123456 成功登录 |
| 客户列表 | ✅ 能看到销售录入的所有客户 |
| 搜索功能 | ✅ 能按姓名/公司/手机搜索 |
| 客户详情 | ✅ 点击查看完整信息 |

---

## 💡 后续优化建议

### 短期(可选)
添加Nginx反向代理,让 `/admin` 路径也能访问:
```nginx
location /admin {
    proxy_pass http://admin-frontend:80;
}
```

### 长期(推荐)
统一使用域名 + Nginx路由:
```
https://sales.yourdomain.com        → 销售端
https://admin.yourdomain.com        → 管理端
https://api.yourdomain.com          → 后端API
```

---

**现在请执行部署命令,然后告诉我测试结果!** 😊
