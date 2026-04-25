# Git自动化部署指南

## 📋 目录

- [方案对比](#方案对比)
- [方案一：手动Git部署（简单）](#方案一手動git部署简单)
- [方案二：自动部署脚本（推荐）](#方案二自动部署脚本推荐)
- [方案三：GitHub Actions CI/CD（高级）](#方案三github-actions-cicd高级)
- [常见问题](#常见问题)

---

## 方案对比

| 方案 | 难度 | 自动化程度 | 适用场景 |
|------|------|-----------|---------|
| 手动Git部署 | ⭐ | 低 | 学习、测试 |
| 自动部署脚本 | ⭐⭐ | 中 | 个人项目、小团队 |
| GitHub Actions | ⭐⭐⭐ | 高 | 生产环境、团队协作 |

---

## 方案一：手动Git部署（简单）

### 适用场景
- 首次部署
- 学习Git部署流程
- 偶尔更新

### 步骤

#### 1. 首次部署

```bash
# 连接服务器
ssh ubuntu@82.156.165.194

# 克隆项目
cd /opt
sudo git clone https://github.com/dtlqwb/xinhuaproject.git
cd xinhuaproject

# 配置环境变量
sudo cp .env.example .env
sudo vi .env  # 修改密码

# 启动服务
sudo docker compose up -d
```

#### 2. 更新代码

```bash
# 连接服务器
ssh ubuntu@82.156.165.194

# 进入项目目录
cd /opt/xinhuaproject

# 拉取最新代码
sudo git pull

# 重新构建并启动
sudo docker compose up -d --build
```

**优点：**
- ✅ 简单直接
- ✅ 完全控制
- ✅ 适合学习

**缺点：**
- ❌ 需要手动执行
- ❌ 容易忘记步骤
- ❌ 不适合频繁更新

---

## 方案二：自动部署脚本（推荐⭐）

### 适用场景
- 个人项目
- 小团队
- 定期更新

### 设置步骤

#### 第1步：上传自动部署脚本到服务器

```bash
# 在本地电脑上
scp auto-deploy.sh ubuntu@82.156.165.194:/opt/xinhuaproject/

# 连接服务器
ssh ubuntu@82.156.165.194

# 添加执行权限
sudo chmod +x /opt/xinhuaproject/auto-deploy.sh
```

#### 第2步：配置SSH密钥（免密码Git操作）

```bash
# 在服务器上生成SSH密钥
ssh-keygen -t ed25519 -C "deploy@xinhuaproject"

# 查看公钥
cat ~/.ssh/id_ed25519.pub

# 将公钥添加到GitHub
# 访问: https://github.com/settings/keys
# 点击 "New SSH key"
# 粘贴公钥内容
```

#### 第3步：使用SSH方式克隆（如果还没克隆）

```bash
cd /opt
sudo rm -rf xinhuaproject
sudo git clone git@github.com:dtlqwb/xinhuaproject.git
cd xinhuaproject
```

#### 第4步：执行自动部署

```bash
# 每次需要部署时，只需执行：
sudo /opt/xinhuaproject/auto-deploy.sh
```

脚本会自动：
1. ✅ 备份当前版本
2. ✅ 拉取最新代码
3. ✅ 检查配置文件
4. ✅ 重新构建Docker镜像
5. ✅ 重启服务
6. ✅ 清理旧备份

### 简化版：创建快捷命令

```bash
# 创建软链接
sudo ln -s /opt/xinhuaproject/auto-deploy.sh /usr/local/bin/deploy-xinhua

# 以后只需执行
sudo deploy-xinhua
```

**优点：**
- ✅ 一键部署
- ✅ 自动备份
- ✅ 减少人为错误
- ✅ 适合定期更新

**缺点：**
- ❌ 仍需手动触发
- ❌ 需要SSH访问服务器

---

## 方案三：GitHub Actions CI/CD（高级）

### 适用场景
- 生产环境
- 团队协作
- 完全自动化

### 设置步骤

#### 第1步：创建GitHub Actions工作流

在项目根目录创建 `.github/workflows/deploy.yml`：

```yaml
name: Deploy to Server

on:
  push:
    branches: [master]

jobs:
  deploy:
    runs-on: ubuntu-latest
    
    steps:
    - name: Checkout code
      uses: actions/checkout@v3
    
    - name: Deploy to server
      uses: appleboy/ssh-action@master
      with:
        host: ${{ secrets.SERVER_HOST }}
        username: ${{ secrets.SERVER_USER }}
        key: ${{ secrets.SSH_PRIVATE_KEY }}
        script: |
          cd /opt/xinhuaproject
          git pull
          docker compose build
          docker compose up -d
```

#### 第2步：配置GitHub Secrets

在GitHub仓库中设置：
- `Settings` → `Secrets and variables` → `Actions`

添加以下secrets：
- `SERVER_HOST`: 82.156.165.194
- `SERVER_USER`: ubuntu
- `SSH_PRIVATE_KEY`: 您的SSH私钥内容

#### 第3步：配置服务器SSH信任

```bash
# 在服务器上
mkdir -p ~/.ssh
chmod 700 ~/.ssh

# 将GitHub Actions的公钥添加到authorized_keys
echo "your-public-key" >> ~/.ssh/authorized_keys
chmod 600 ~/.ssh/authorized_keys
```

#### 第4步：推送代码触发自动部署

```bash
# 本地修改代码后
git add .
git commit -m "更新功能"
git push

# GitHub Actions会自动触发部署！
```

**优点：**
- ✅ 完全自动化
- ✅ 推送即部署
- ✅ 适合团队协作
- ✅ 有部署历史记录

**缺点：**
- ❌ 配置较复杂
- ❌ 需要暴露SSH密钥给GitHub
- ❌ 调试相对困难

---

## 🔧 实际使用建议

### 对于您当前的项目

**推荐使用方案二（自动部署脚本）**

原因：
1. 个人项目，不需要复杂的CI/CD
2. 简单易用，维护成本低
3. 可以完全控制部署过程

### 具体操作流程

#### 日常开发流程

```bash
# 1. 在本地开发
# 修改代码...

# 2. 提交到GitHub
git add .
git commit -m "描述更改"
git push

# 3. 连接服务器并部署
ssh ubuntu@82.156.165.194
sudo deploy-xinhua

# 或者一行命令
ssh ubuntu@82.156.165.194 "sudo /opt/xinhuaproject/auto-deploy.sh"
```

#### 创建别名简化操作

在本地电脑的 `~/.bashrc` 或 `~/.zshrc` 中添加：

```bash
alias deploy='ssh ubuntu@82.156.165.194 "sudo /opt/xinhuaproject/auto-deploy.sh"'
```

然后只需执行：
```bash
deploy
```

---

## 📊 三种方案详细对比

### 方案一：手动Git部署

**完整命令序列：**
```bash
# 首次部署
ssh ubuntu@82.156.165.194
cd /opt
sudo git clone https://github.com/dtlqwb/xinhuaproject.git
cd xinhuaproject
sudo cp .env.example .env
sudo vi .env
sudo docker compose up -d

# 后续更新
ssh ubuntu@82.156.165.194
cd /opt/xinhuaproject
sudo git pull
sudo docker compose up -d --build
```

**时间成本：** 每次约2-3分钟

---

### 方案二：自动部署脚本

**设置（一次性）：**
```bash
# 上传脚本
scp auto-deploy.sh ubuntu@82.156.165.194:/opt/xinhuaproject/
ssh ubuntu@82.156.165.194
sudo chmod +x /opt/xinhuaproject/auto-deploy.sh
sudo ln -s /opt/xinhuaproject/auto-deploy.sh /usr/local/bin/deploy-xinhua
```

**日常使用：**
```bash
# 本地推送代码
git push

# 远程部署（一行命令）
ssh ubuntu@82.156.165.194 "sudo deploy-xinhua"
```

**时间成本：** 每次约30秒操作 + 3-5分钟自动执行

---

### 方案三：GitHub Actions

**设置（一次性，较复杂）：**
1. 创建workflow文件
2. 配置GitHub Secrets
3. 配置服务器SSH信任
4. 测试部署流程

**日常使用：**
```bash
# 只需推送代码
git push

# 自动部署，无需任何操作
```

**时间成本：** 0秒操作 + 5-10分钟自动执行

---

## ❓ 常见问题

### Q1: Git pull时提示需要密码？

**解决方案：** 使用SSH密钥或配置credential helper

```bash
# 方法1：使用SSH（推荐）
git remote set-url origin git@github.com:dtlqwb/xinhuaproject.git

# 方法2：缓存凭据
git config --global credential.helper cache
```

### Q2: Docker构建失败？

**检查日志：**
```bash
docker compose logs
```

**常见原因：**
- 内存不足
- 网络问题
- 代码有语法错误

### Q3: 如何回滚到之前的版本？

```bash
cd /opt/xinhuaproject

# 查看提交历史
git log --oneline

# 回滚到指定版本
git reset --hard <commit-hash>

# 重新部署
docker compose up -d --build
```

### Q4: 部署后网站无法访问？

**排查步骤：**
```bash
# 1. 检查服务状态
sudo docker compose ps

# 2. 检查端口
sudo ss -tlnp | grep -E ':(80|81)'

# 3. 检查防火墙
sudo ufw status

# 4. 查看日志
sudo docker compose logs -f
```

### Q5: 如何实现零停机部署？

使用滚动更新策略：

```bash
# 在docker-compose.yml中配置
services:
  backend:
    deploy:
      update_config:
        parallelism: 1
        delay: 10s
```

---

## 🎯 推荐方案总结

### 立即开始（今天）

使用**方案二：自动部署脚本**

1. 上传 `auto-deploy.sh` 到服务器
2. 添加执行权限
3. 创建快捷命令
4. 以后只需执行 `sudo deploy-xinhua`

### 未来升级（可选）

当项目成熟后，可以考虑：
- 迁移到方案三（GitHub Actions）
- 添加监控和告警
- 实现蓝绿部署

---

## 📝 快速参考

### 最常用的命令

```bash
# 部署
sudo deploy-xinhua

# 查看状态
sudo docker compose ps

# 查看日志
sudo docker compose logs -f

# 重启服务
sudo docker compose restart

# 停止服务
sudo docker compose down
```

---

**选择适合您的方案，开始Git部署吧！** 🚀
