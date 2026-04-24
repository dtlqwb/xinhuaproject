# 智销助手 - 服务器首次部署指南

## 🔑 问题诊断

当前部署失败的原因：**服务器无法访问GitHub**

可能原因：
1. 服务器上没有配置SSH密钥
2. 服务器网络无法访问GitHub
3. GitHub需要认证

---

## ✅ 解决方案（3选1）

### 方案A：在服务器上配置SSH密钥（推荐⭐）

#### 第1步：连接到服务器

```bash
ssh ubuntu@82.156.165.194
# 密码: H6~4]+9wFt8pSd
```

#### 第2步：生成SSH密钥

```bash
# 生成密钥
ssh-keygen -t ed25519 -C "deploy@xinhuaproject"

# 按回车使用默认路径
# 按回车不设置密码
```

#### 第3步：查看公钥

```bash
cat ~/.ssh/id_ed25519.pub
```

会输出类似：
```
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI... deploy@xinhuaproject
```

**复制整个公钥内容**

#### 第4步：添加到GitHub

1. 访问：https://github.com/settings/keys
2. 点击 "New SSH key"
3. Title: `Server Deploy Key`
4. Key: 粘贴刚才复制的公钥
5. 点击 "Add SSH key"

#### 第5步：测试连接

```bash
ssh -T git@github.com
```

应该看到：
```
Hi dtlqwb! You've successfully authenticated...
```

#### 第6步：克隆项目

```bash
cd /opt
sudo git clone git@github.com:dtlqwb/xinhuaproject.git
cd xinhuaproject
```

#### 第7步：配置并启动

```bash
# 配置环境变量
sudo cp .env.example .env
sudo vi .env  # 修改密码

# 启动服务
sudo docker compose up -d
```

---

### 方案B：使用Personal Access Token（简单）

#### 第1步：生成GitHub Token

1. 访问：https://github.com/settings/tokens
2. 点击 "Generate new token (classic)"
3. Note: `Server Deploy`
4. 勾选权限：`repo` (全部)
5. 点击 "Generate token"
6. **复制token**（只显示一次！）

#### 第2步：在服务器上克隆

```bash
ssh ubuntu@82.156.165.194
# 密码: H6~4]+9wFt8pSd

cd /opt
sudo git clone https://YOUR_TOKEN@github.com/dtlqwb/xinhuaproject.git
# 将YOUR_TOKEN替换为实际的token
```

---

### 方案C：本地上传代码（最快）

如果上述方法都困难，可以直接从本地上传：

#### 第1步：在本地打包

```bash
# 在项目根目录
tar -czf xinhuaproject.tar.gz \
  --exclude='node_modules' \
  --exclude='.git' \
  --exclude='dist' \
  --exclude='target' \
  .
```

#### 第2步：上传到服务器

```bash
scp xinhuaproject.tar.gz ubuntu@82.156.165.194:/opt/
```

#### 第3步：在服务器上解压

```bash
ssh ubuntu@82.156.165.194
# 密码: H6~4]+9wFt8pSd

cd /opt
sudo mkdir -p xinhuaproject
sudo tar -xzf xinhuaproject.tar.gz -C xinhuaproject
cd xinhuaproject

# 配置并启动
sudo cp .env.example .env
sudo vi .env
sudo docker compose up -d
```

---

## 🎯 推荐操作流程

**我建议使用方案A（SSH密钥），步骤如下：**

```bash
# 1. 连接服务器
ssh ubuntu@82.156.165.194
# 输入密码: H6~4]+9wFt8pSd

# 2. 生成SSH密钥
ssh-keygen -t ed25519 -C "deploy@xinhuaproject"
# 三次回车

# 3. 查看公钥
cat ~/.ssh/id_ed25519.pub

# 4. 复制输出的公钥，添加到GitHub
# https://github.com/settings/keys

# 5. 测试
ssh -T git@github.com

# 6. 克隆项目
cd /opt
sudo git clone git@github.com:dtlqwb/xinhuaproject.git
cd xinhuaproject

# 7. 配置
sudo cp .env.example .env
sudo vi .env

# 8. 启动
sudo docker compose up -d
```

---

## 📝 后续更新

配置好SSH密钥后，以后更新就很简单了：

```bash
# 本地推送
git push

# 服务器拉取并重启
ssh ubuntu@82.156.165.194 "cd /opt/xinhuaproject && sudo git pull && sudo docker compose up -d --build"
```

或者使用自动部署脚本：
```bash
python auto-deploy.py
```

---

## ❓ 需要帮助？

如果您在配置过程中遇到问题，请告诉我：
1. 哪一步出错了
2. 错误信息是什么
3. 我会帮您解决
