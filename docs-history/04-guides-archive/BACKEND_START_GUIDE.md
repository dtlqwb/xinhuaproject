# 后端启动指南

## 前提条件

1. ✅ 已安装 JDK 8 或更高版本
2. ✅ 已安装 Maven 3.6+
3. ✅ 已安装 MySQL 8.0

## 详细步骤

### 1. 启动MySQL服务

确保MySQL服务正在运行：

**Windows:**
```powershell
# 检查MySQL服务状态
Get-Service MySQL80

# 如果未启动，执行：
Start-Service MySQL80
```

**或者通过服务管理器：**
- 按 `Win + R`，输入 `services.msc`
- 找到 `MySQL80` 服务
- 右键点击"启动"

### 2. 创建数据库并执行SQL脚本

#### 方法一：使用命令行

```bash
# 登录MySQL（会提示输入密码）
mysql -u root -p

# 在MySQL命令行中执行：
source d:/360MoveData/Users/wangbo/Desktop/客户信息收集/xinhuaproject/backend/src/main/resources/schema.sql
```

#### 方法二：使用MySQL客户端工具

1. 打开 MySQL Workbench、Navicat 或其他MySQL客户端
2. 连接到本地MySQL服务器
   - Host: localhost
   - Port: 3306
   - User: root
   - Password: 您的root密码
3. 打开并执行 SQL 文件：
   ```
   backend/src/main/resources/schema.sql
   ```

#### 方法三：手动执行

如果上述方法有问题，可以手动执行以下SQL：

```sql
-- 创建数据库
CREATE DATABASE IF NOT EXISTS sales_customer_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sales_customer_db;

-- 然后依次执行 schema.sql 中的所有 CREATE TABLE 语句
```

### 3. 修改数据库配置（如果需要）

如果您的MySQL密码不是 `root`，需要修改配置文件：

编辑文件：`backend/src/main/resources/application.yml`

找到这部分：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sales_customer_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root  # <-- 修改为您的MySQL密码
```

将 `password: root` 改为您的实际密码。

### 4. 启动后端服务

#### 方法一：使用Maven命令（推荐）

```bash
# 进入后端目录
cd "d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend"

# 清理并编译
mvn clean install

# 启动服务
mvn spring-boot:run
```

#### 方法二：使用IDEA启动

1. 用 IntelliJ IDEA 打开 `backend` 文件夹
2. 等待Maven依赖下载完成
3. 找到 `CustomerSystemApplication.java`
4. 右键点击 -> Run 'CustomerSystemApplication'

#### 方法三：打包后运行

```bash
# 打包
mvn clean package -DskipTests

# 运行JAR包
java -jar target/customer-system-1.0.0.jar
```

### 5. 验证后端是否启动成功

看到以下日志表示启动成功：

```
Started CustomerSystemApplication in X.XXX seconds
```

访问测试地址：
- http://localhost:8080

如果返回404是正常的，因为还没有配置根路径。

### 6. 测试API接口

可以使用Postman或浏览器测试：

**登录接口测试：**
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
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

## 🔧 常见问题

### 问题1：端口8080被占用

**错误信息：** `Port 8080 was already in use`

**解决方案：**
修改 `application.yml` 中的端口：
```yaml
server:
  port: 8081  # 改为其他端口
```

同时修改前端的 `vite.config.ts`：
```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8081',  // 改为相同端口
    changeOrigin: true
  }
}
```

### 问题2：数据库连接失败

**错误信息：** `Access denied for user 'root'@'localhost'`

**解决方案：**
1. 检查MySQL用户名和密码是否正确
2. 确认MySQL服务已启动
3. 检查防火墙设置

### 问题3：Maven依赖下载慢

**解决方案：**
配置国内镜像源，编辑 `~/.m2/settings.xml`：

```xml
<mirrors>
  <mirror>
    <id>aliyun</id>
    <mirrorOf>central</mirrorOf>
    <name>Aliyun Maven</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### 问题4：找不到Java命令

**解决方案：**
1. 确认已安装JDK
2. 配置JAVA_HOME环境变量
3. 将 `%JAVA_HOME%\bin` 添加到PATH

## 📝 数据库表说明

执行SQL脚本后会创建以下表：

1. **sales_person** - 销售人员表
   - 默认账号：13800138000 / 123456
   
2. **customer** - 客户信息表

3. **customer_attachment** - 客户附件表

4. **daily_report** - 日报表

5. **marketing_plan** - 营销方案表

6. **admin_user** - 管理员表
   - 默认账号：admin / admin123

## 🎯 启动顺序

正确的启动顺序：

1. ✅ 启动MySQL服务
2. ✅ 执行数据库脚本
3. ✅ 启动后端服务（端口8080）
4. ✅ 启动前端服务（端口3000/3001）
5. ✅ 浏览器访问前端地址

## 🚀 快速启动脚本

创建一个批处理文件 `start-backend.bat`：

```batch
@echo off
echo 正在启动后端服务...
cd /d "d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend"
mvn spring-boot:run
pause
```

双击即可启动！

---

祝您启动成功！如有问题请查看控制台错误日志。
