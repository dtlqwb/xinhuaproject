# Maven安装详细指南 - 10分钟快速完成

## 📥 步骤1：下载Maven（2分钟）

### 方式A：直接下载（推荐）

点击下载链接：
- **主下载地址**: https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip
- **备用地址**: https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip

### 方式B：官网下载

1. 访问：https://maven.apache.org/download.cgi
2. 找到 "Binary zip archive" 
3. 点击链接下载 `apache-maven-3.9.x-bin.zip`

---

## 📂 步骤2：解压文件（1分钟）

1. **创建目录**
   ```
   C:\Program Files\Apache
   ```

2. **解压ZIP文件**
   - 右键下载的zip文件
   - 选择"解压到当前文件夹"或"全部提取"
   - 目标位置：`C:\Program Files\Apache`

3. **验证解压结果**
   
   应该有这个路径：
   ```
   C:\Program Files\Apache\apache-maven-3.9.6\bin\mvn.cmd
   ```

---

## ⚙️ 步骤3：配置环境变量（3分钟）

### 方式A：使用PowerShell（快速，需要管理员权限）

1. **以管理员身份运行PowerShell**
   - 在开始菜单搜索"PowerShell"
   - 右键 -> "以管理员身份运行"

2. **执行以下命令**（复制粘贴后回车）：

```powershell
# 设置MAVEN_HOME
[Environment]::SetEnvironmentVariable("MAVEN_HOME", "C:\Program Files\Apache\apache-maven-3.9.6", "Machine")

# 添加到PATH
$oldPath = [Environment]::GetEnvironmentVariable("Path", "Machine")
$newPath = "$oldPath;%MAVEN_HOME%\bin"
[Environment]::SetEnvironmentVariable("Path", $newPath, "Machine")

# 验证
echo "环境变量配置完成！"
```

3. **关闭所有终端窗口**（包括当前的PowerShell和CMD）

### 方式B：手动配置（图形界面）

1. **打开环境变量设置**
   - 右键"此电脑" -> "属性"
   - 点击"高级系统设置"
   - 点击"环境变量"按钮

2. **新建系统变量**
   - 点击"系统变量"区域的"新建"
   - 变量名：`MAVEN_HOME`
   - 变量值：`C:\Program Files\Apache\apache-maven-3.9.6`
   - 点击"确定"

3. **编辑Path变量**
   - 在"系统变量"中找到`Path`
   - 双击或点击"编辑"
   - 点击"新建"
   - 添加：`%MAVEN_HOME%\bin`
   - 点击"确定"保存所有窗口

---

## ✅ 步骤4：验证安装（1分钟）

1. **打开新的CMD或PowerShell窗口**（必须新开）

2. **执行命令**：
   ```bash
   mvn -version
   ```

3. **看到类似输出表示成功**：
   ```
   Apache Maven 3.9.6
   Maven home: C:\Program Files\Apache\apache-maven-3.9.6
   Java version: 1.8.0_xxx, vendor: Oracle Corporation
   ...
   ```

---

## 🚀 步骤5：启动后端（3分钟）

1. **进入项目目录**
   ```bash
   cd "d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend"
   ```

2. **首次启动（下载依赖，需要5-10分钟）**
   ```bash
   mvn spring-boot:run
   ```

   **提示**：首次运行会下载大量依赖，请耐心等待。可以看到下载进度。

3. **看到以下日志表示启动成功**：
   ```
   Started CustomerSystemApplication in X.XXX seconds
   ```

4. **保持这个窗口运行**，不要关闭

---

## 🧪 步骤6：测试后端API

打开浏览器或Postman测试：

### 测试登录接口

**URL**: `http://localhost:8080/api/sales/login`  
**方法**: POST  
**Headers**: `Content-Type: application/json`  
**Body**:
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

**预期返回**:
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

## 🎉 完成！

现在您可以：
1. ✅ 后端运行在 http://localhost:8080
2. ✅ 前端运行在 http://localhost:3001
3. ✅ 刷新前端页面，使用真实后端API
4. ✅ 完整测试所有功能

---

## ❓ 常见问题

### Q1: 下载速度慢怎么办？

**配置阿里云镜像加速**

编辑文件：`%USERPROFILE%\.m2\settings.xml`（如果不存在就创建）

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
```

### Q2: mvn命令找不到？

1. 确认已正确配置环境变量
2. **必须重新打开终端窗口**
3. 检查路径是否正确：`C:\Program Files\Apache\apache-maven-3.9.6\bin\mvn.cmd`

### Q3: 端口8080被占用？

修改 `backend/src/main/resources/application.yml`：
```yaml
server:
  port: 8081
```

同时修改前端 `sales-frontend/vite.config.ts`：
```typescript
proxy: {
  '/api': {
    target: 'http://localhost:8081',  // 改为相同端口
    changeOrigin: true
  }
}
```

### Q4: Java版本不对？

确保使用JDK 8或更高版本：
```bash
java -version
```

应该显示 1.8.x 或更高版本。

---

## 💡 快速脚本

我已经为您创建了自动化脚本：

**运行**: `backend/install-maven.bat`

这个脚本会：
1. 检查是否已安装
2. 提供下载链接
3. 指导配置环境变量
4. 验证安装

---

## 📞 需要帮助？

如果在安装过程中遇到问题：
1. 截图错误信息
2. 告诉我具体的问题
3. 我会帮您解决

祝您安装顺利！🎊
