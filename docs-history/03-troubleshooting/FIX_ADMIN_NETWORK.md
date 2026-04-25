# 管理后台"网络连接失败"问题解决方案

## 问题原因
管理后台显示"网络连接失败"是因为**后端服务(端口8080)没有启动**。

## 快速诊断
当前状态检查:
- ✗ 后端服务 (端口 8080): **未运行**
- ✗ 管理前端 (端口 3002): **未运行**

## 解决方案

### 方案一: 使用 Docker 启动(推荐)

如果您已安装 Docker:

```powershell
# 在项目根目录执行
cd "d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject"

# 启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps
```

访问地址:
- 销售前端: http://localhost
- 管理后台: http://localhost:81

### 方案二: 本地开发模式启动

#### 步骤1: 启动后端服务

```powershell
# 打开第一个终端窗口
cd "d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend"

# 确保 MySQL 数据库已启动并运行
# 然后启动后端
mvn spring-boot:run
```

等待看到类似以下输出表示启动成功:
```
Started CustomerSystemApplication in X.XXX seconds
```

#### 步骤2: 启动管理前端

```powershell
# 打开第二个终端窗口
cd "d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\admin-frontend"

# 安装依赖(首次运行需要)
npm install

# 启动开发服务器
npm run dev
```

等待看到类似以下输出:
```
VITE v5.x.x  ready in xxx ms

➜  Local:   http://localhost:3002/
```

#### 步骤3: 访问管理后台

在浏览器中打开: **http://localhost:3002**

## 常见问题排查

### 1. 后端启动失败

**问题**: Maven 编译错误或依赖缺失

**解决**:
```powershell
cd backend
mvn clean install
mvn spring-boot:run
```

### 2. 数据库连接失败

**问题**: MySQL 未启动或配置错误

**检查**:
```powershell
# 检查 MySQL 是否运行
netstat -ano | findstr ":3306"

# 如果没有输出,说明 MySQL 未启动
# 需要启动 MySQL 服务或使用 Docker
```

**Docker 方式启动 MySQL**:
```powershell
docker run -d --name sales-mysql -e MYSQL_ROOT_PASSWORD=root123 -p 3306:3306 mysql:8.0
```

### 3. 端口被占用

**问题**: 端口 8080 或 3002 已被其他程序占用

**解决**:
```powershell
# 查找占用端口的进程
netstat -ano | findstr ":8080"

# 终止进程(替换 PID 为实际进程ID)
taskkill /F /PID <PID>
```

### 4. 查看详细错误信息

我已经改进了错误提示,现在当出现网络错误时:

1. 打开浏览器开发者工具 (按 F12)
2. 切换到 Console 标签
3. 查看红色错误信息,会显示详细的诊断信息:
   - 网络连接失败详情
   - 请求的 URL
   - 错误代码

## 改进的错误处理

我已更新 [admin-frontend/src/utils/request.ts](file://d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\admin-frontend\src\utils\request.ts),现在会显示:

- ✓ 详细的网络错误诊断信息
- ✓ 控制台输出完整的错误详情
- ✓ 友好的用户提示信息
- ✓ 区分不同类型的错误(网络错误、超时、HTTP错误等)

## 验证修复

启动服务后,在浏览器控制台应该能看到:

```javascript
// 如果后端未启动,会显示:
网络连接失败详情: {
  message: "ERR_NETWORK",
  config: {...},
  code: "ERR_NETWORK"
}

// 错误提示:
"网络连接失败,请检查:
1. 后端服务是否启动(端口8080)
2. 网络连接是否正常
3. 防火墙设置"
```

## 一键启动脚本

创建 `start-admin.ps1`:

```powershell
# 启动后端(新窗口)
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'd:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend'; mvn spring-boot:run"

# 等待3秒让后端启动
Start-Sleep -Seconds 3

# 启动前端(新窗口)
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd 'd:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\admin-frontend'; npm run dev"

Write-Host "正在启动管理后台..." -ForegroundColor Green
Write-Host "后端: http://localhost:8080" -ForegroundColor Cyan
Write-Host "前端: http://localhost:3002" -ForegroundColor Cyan
```

---

**下一步**: 请先启动后端服务,然后再启动前端,最后访问 http://localhost:3002
