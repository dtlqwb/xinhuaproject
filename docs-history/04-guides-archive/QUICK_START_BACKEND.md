# 快速启动后端 - 最简单的方法

## 🎯 推荐方案：使用Navicat或MySQL Workbench

### 第一步：初始化数据库（5分钟）

#### 方法A：使用Navicat（推荐）

1. **打开Navicat**
2. **连接到MySQL**
   - 连接名：随便填
   - 主机：localhost
   - 端口：3306
   - 用户名：root
   - 密码：您的MySQL密码（如果不知道，尝试：root、123456、空）

3. **执行SQL脚本**
   - 右键点击连接 -> "新建数据库"
   - 数据库名：`sales_customer_db`
   - 字符集：`utf8mb4`
   - 排序规则：`utf8mb4_unicode_ci`
   - 点击确定
   
   - 双击新建的数据库使其激活
   - 点击菜单栏 "查询" -> "新建查询"
   - 打开文件：`backend/src/main/resources/schema.sql`
   - 复制所有内容粘贴到查询窗口
   - 点击"运行"按钮

4. **验证**
   - 刷新左侧数据库列表
   - 应该看到以下表：
     - sales_person
     - customer
     - customer_attachment
     - daily_report
     - marketing_plan
     - admin_user

#### 方法B：使用MySQL Workbench

1. 打开MySQL Workbench
2. 点击本地连接
3. 点击菜单 File -> Open SQL Script
4. 选择 `schema.sql` 文件
5. 点击执行按钮（⚡图标）

#### 方法C：使用命令行（如果您知道密码）

```bash
# 在backend目录下执行
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p < src\main\resources\schema.sql
# 输入密码后回车
```

### 第二步：启动后端

由于您没有安装Maven，有以下选择：

#### 选项1：使用IntelliJ IDEA（最简单⭐）

1. **下载IDEA**（如果还没有）
   - 社区版免费：https://www.jetbrains.com/idea/download/
   
2. **打开项目**
   - File -> Open
   - 选择 `backend` 文件夹
   
3. **等待依赖下载**
   - 右下角会显示Maven导入进度
   - 首次需要5-10分钟
   
4. **运行应用**
   - 找到 `CustomerSystemApplication.java`
   - 右键 -> Run

#### 选项2：安装Maven（10分钟）

1. **下载Maven**
   - https://maven.apache.org/download.cgi
   - 下载 `apache-maven-3.9.x-bin.zip`

2. **解压**
   - 解压到：`C:\Program Files\Apache\maven`

3. **配置环境变量**
   
   以**管理员身份**运行PowerShell，执行：
   ```powershell
   # 设置MAVEN_HOME
   [Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\Apache\maven", "Machine")
   
   # 添加到PATH
   $oldPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
   $newPath = "$oldPath;%MAVEN_HOME%\bin"
   [Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
   ```

4. **重启终端并验证**
   ```bash
   mvn -version
   ```

5. **启动后端**
   ```bash
   cd backend
   mvn spring-boot:run
   ```

#### 选项3：双击启动脚本（需要先安装Maven）

我已经为您创建了启动脚本：
- `backend/start-backend.bat`

但需要先安装Maven才能使用。

### 第三步：验证后端启动

看到以下日志表示成功：
```
Started CustomerSystemApplication in X.XXX seconds
```

访问：http://localhost:8080

### 第四步：测试API

使用浏览器或Postman测试：

**登录接口：**
```
POST http://localhost:8080/api/sales/login
Content-Type: application/json

{
  "phone": "13800138000",
  "password": "123456"
}
```

预期返回：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "张三",
    "phone": "13800138000",
    "department": "销售部",
    "token": "eyJhbG..."
  }
}
```

## 📝 完整启动流程总结

```
1. MySQL服务运行中 ✅ (已确认)
2. 执行schema.sql创建数据库和表 ⏳ (需要您的MySQL密码)
3. 安装Maven或使用IDEA ⏳
4. 启动后端服务 ⏳
5. 前端已经运行在 http://localhost:3001 ✅
6. 浏览器访问前端，使用真实后端API 🎉
```

## 🔑 默认账号

**销售人员：**
- 手机号：13800138000
- 密码：123456

**管理员：**
- 用户名：admin
- 密码：admin123

## ❓ 常见问题

### Q: 我不知道MySQL密码怎么办？

A: 可以尝试重置MySQL密码：
1. 停止MySQL服务
2. 以安全模式启动
3. 重置密码
4. 或者重装MySQL（如果数据不重要）

### Q: Maven下载依赖很慢？

A: 配置阿里云镜像：
编辑 `%USERPROFILE%\.m2\settings.xml`，添加：
```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### Q: 端口8080被占用？

A: 修改 `application.yml`：
```yaml
server:
  port: 8081
```

同时修改前端 `vite.config.ts` 的proxy配置。

---

## 💡 当前建议

由于您需要先解决MySQL密码和Maven安装的问题，我建议：

1. **先用演示模式体验前端** 
   - 前端已经可以登录（模拟数据）
   - 可以查看UI设计和交互效果

2. **准备好后再启动后端**
   - 找到MySQL密码
   - 安装Maven或使用IDEA
   - 然后完整测试

需要我帮您做其他事情吗？
