# 服务器首次部署 - 快速指南（3分钟）

## 🎯 目标

在服务器上完成首次项目部署。

---

## 📋 步骤（复制粘贴即可）

### 第1步：连接服务器

```bash
ssh ubuntu@82.156.165.194
```

输入密码：`H6~4]+9wFt8pSd`

### 第2步：克隆项目

```bash
cd /opt
sudo git clone https://github.com/dtlqwb/xinhuaproject.git
cd xinhuaproject
```

### 第3步：配置环境变量

```bash
sudo cp .env.example .env
sudo vi .env
```

修改以下内容：
```env
DB_PASSWORD=your_secure_password
JWT_SECRET=your_jwt_secret_minimum_32_characters
```

按 `Esc`，输入 `:wq` 保存退出。

### 第4步：启动服务

```bash
sudo docker compose up -d
```

等待5-10分钟，Docker会：
- 下载基础镜像
- 编译前端代码
- 构建后端镜像
- 启动所有服务

### 第5步：验证部署

```bash
# 查看服务状态
sudo docker compose ps

# 应该看到4个running状态的容器
```

访问网站：
- 销售端：http://82.156.165.194:80
- 管理端：http://82.156.165.194:81

---

## ✅ 完成后

首次部署完成后，以后更新就简单了：

### 方式1：使用自动脚本

在本地电脑执行：
```bash
python auto-deploy.py
```

### 方式2：手动更新

```bash
# 本地推送
git push

# 服务器更新
ssh ubuntu@82.156.165.194 "cd /opt/xinhuaproject && sudo git pull && sudo docker compose up -d --build"
```

---

## ❓ 遇到问题？

### 问题1：git clone失败

**原因**：服务器网络无法访问GitHub

**解决**：配置Git代理或使用SSH密钥

```bash
# 方法1：配置HTTP代理
git config --global http.proxy http://proxy-server:port

# 方法2：配置SSH密钥（推荐）
ssh-keygen -t ed25519
cat ~/.ssh/id_ed25519.pub
# 复制公钥到 GitHub: https://github.com/settings/keys
git clone git@github.com:dtlqwb/xinhuaproject.git
```

### 问题2：docker compose命令找不到

**解决**：安装Docker Compose

```bash
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 问题3：端口被占用

**解决**：检查并停止占用的服务

```bash
sudo ss -tlnp | grep :80
sudo systemctl stop nginx  # 如果nginx占用了80端口
```

---

## 🚀 开始吧！

现在就连接到服务器，按照上面的步骤执行即可。

预计时间：**3-10分钟**（取决于网络速度）
