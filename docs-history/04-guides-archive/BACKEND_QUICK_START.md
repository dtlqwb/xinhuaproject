# 智销助手 - 后端快速启动指南

## 🎯 最简单的启动方式

### 前提条件检查

- ✅ Java已安装 (JDK 1.8)
- ✅ MySQL正在运行 (MySQL84)
- ⏳ 需要初始化数据库
- ⏳ 需要Maven或IDEA

---

## 📝 步骤一：初始化数据库（必须）

### 方法1：使用Navicat（推荐）

1. 打开Navicat
2. 连接到MySQL
   - 主机: localhost
   - 端口: 3306
   - 用户: root
   - 密码: 您的MySQL密码
3. 新建查询
4. 打开并执行文件: `backend/src/main/resources/schema.sql`
5. 刷新查看表是否创建成功

### 方法2：使用MySQL Workbench

1. 打开MySQL Workbench
2. 点击本地连接
3. File -> Open SQL Script
4. 选择 `schema.sql`
5. 点击执行按钮

### 方法3：命令行（需要知道密码）

```bash
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p < backend\src\main\resources\schema.sql
```

输入密码后回车执行。

---

## 🚀 步骤二：启动后端

### 方式A：使用IntelliJ IDEA（最简单⭐）

1. **下载安装IDEA社区版**（如果还没有）
   - 官网: https://www.jetbrains.com/idea/download/
   - 社区版免费

2. **打开项目**
   - File -> Open
   - 选择: `d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend`

3. **等待Maven导入**
   - 右下角显示进度
   - 首次需要5-10分钟下载依赖

4. **运行应用**
   - 找到: `src/main/java/com/sales/customer/CustomerSystemApplication.java`
   - 右键 -> Run 'CustomerSystemApplication'

5. **看到以下日志表示成功**
   ```
   Started CustomerSystemApplication in X.XXX seconds
   ```

### 方式B：安装Maven后使用命令行

#### 1. 下载Maven

访问: https://maven.apache.org/download.cgi
下载: `apache-maven-3.9.x-bin.zip`

#### 2. 解压

解压到: `C:\Program Files\Apache\maven`

#### 3. 配置环境变量

**以管理员身份运行PowerShell**，执行：

```powershell
# 设置MAVEN_HOME
[Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\Apache\maven", "Machine")

# 添加到PATH
$oldPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
$newPath = "$oldPath;%MAVEN_HOME%\bin"
[Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")
```

#### 4. 重启终端验证

```bash
mvn -version
```

应该看到M版本信息。

#### 5. 启动后端

```bash
cd backend
mvn spring-boot:run
```

首次启动会下载依赖，需要耐心等待。

---

## ✅ 验证后端启动成功

### 1. 检查日志

看到以下内容表示成功：
```
Started CustomerSystemApplication in X.XXX seconds
```

### 2. 测试API

使用浏览器或Postman访问：

**登录接口测试：**
```
POST http://localhost:8080/api/sales/login
Content-Type: application/json

{
  "phone": "13800138000",
  "password": "123456"
}
```

**预期返回：**
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

---

## 🔑 默认账号

**销售人员：**
- 手机号: 13800138000
- 密码: 123456

**管理员：**
- 用户名: admin
- 密码: admin123

---

## 📊 完整启动流程

```
1. MySQL服务运行中 ✅
2. 执行schema.sql ⏳ ← 当前需要做的
3. 启动后端（IDEA或Maven）⏳
4. 前端已运行在3001端口 ✅
5. 浏览器访问前端，完整测试 🎉
```

---

## ❓ 常见问题

### Q: MySQL密码忘记了怎么办？

A: 可以尝试：
- root
- 123456
- 空密码
- 或者重置MySQL密码

### Q: IDEA下载依赖很慢？

A: 配置阿里云镜像：
- File -> Settings -> Build -> Maven
- User settings file 指向自定义settings.xml
- 添加阿里云镜像配置

### Q: 端口8080被占用？

A: 修改 `application.yml`:
```yaml
server:
  port: 8081
```

同时修改前端 `vite.config.ts` 的proxy配置。

---

## 💡 建议操作顺序

1. **现在**: 用Navicat执行SQL脚本初始化数据库
2. **然后**: 用IDEA打开backend文件夹并运行
3. **最后**: 刷新前端页面，使用真实后端API

准备好了吗？需要我帮您做什么？
