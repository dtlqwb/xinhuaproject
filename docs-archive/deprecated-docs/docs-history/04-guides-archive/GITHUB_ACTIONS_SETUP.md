# GitHub Actions 自动化部署配置指南

## 📋 概述

配置完成后，每次push代码到master分支，GitHub Actions会自动：
1. ✅ 检出代码
2. ✅ SSH连接到服务器
3. ✅ 拉取最新代码
4. ✅ 重新构建Docker
5. ✅ 重启服务
6. ✅ 发送部署通知

**完全自动化，无需手动操作！**

---

## 🔧 配置步骤

### 第1步：在服务器上生成SSH密钥

```bash
# 连接到服务器
ssh ubuntu@82.156.165.194
# 密码: H6~4]+9wFt8pSd

# 生成SSH密钥（用于GitHub Actions）
ssh-keygen -t rsa -b 4096 -C "github-actions@xinhuaproject" -f ~/.ssh/github_actions -N ''

# 查看私钥（复制全部内容）
cat ~/.ssh/github_actions

# 查看公钥
cat ~/.ssh/github_actions.pub
```

**重要：** 
- 复制私钥内容（包括`-----BEGIN RSA PRIVATE KEY-----`和`-----END RSA PRIVATE KEY-----`）
- 复制公钥内容

### 第2步：配置服务器authorized_keys

```bash
# 将公钥添加到authorized_keys
cat ~/.ssh/github_actions.pub >> ~/.ssh/authorized_keys

# 设置权限
chmod 600 ~/.ssh/authorized_keys
chmod 700 ~/.ssh
```

### 第3步：添加Secrets到GitHub

1. 访问您的仓库：https://github.com/dtlqwb/xinhuaproject
2. 点击 **Settings** → **Secrets and variables** → **Actions**
3. 点击 **New repository secret**

添加以下3个secrets：

#### Secret 1: SSH_PRIVATE_KEY
- **Name**: `SSH_PRIVATE_KEY`
- **Value**: 粘贴第1步中复制的**私钥**内容（完整的，包括BEGIN和END行）

#### Secret 2: SERVER_HOST
- **Name**: `SERVER_HOST`
- **Value**: `82.156.165.194`

#### Secret 3: SERVER_USER
- **Name**: `SERVER_USER`
- **Value**: `ubuntu`

### 第4步：测试工作流

#### 方法1：推送代码触发

```bash
# 修改任意文件
echo "# Test" >> README.md

# 提交并推送
git add .
git commit -m "Test GitHub Actions deployment"
git push
```

然后访问：https://github.com/dtlqwb/xinhuaproject/actions

查看部署进度。

#### 方法2：手动触发

1. 访问：https://github.com/dtlqwb/xinhuaproject/actions
2. 点击左侧 **Deploy to Server**
3. 点击 **Run workflow**
4. 选择分支 **master**
5. 点击 **Run workflow**

---

## 📊 工作流程说明

```yaml
触发条件：
  - push到master分支
  - 手动触发（workflow_dispatch）

执行步骤：
  1. Checkout code        # 检出代码
  2. Setup SSH           # 配置SSH密钥
  3. Add known hosts     # 添加服务器指纹
  4. Deploy to server    # 执行部署脚本
```

### 部署脚本执行内容：

```bash
cd /opt/xinhuaproject          # 进入项目目录
git pull origin master         # 拉取最新代码
docker compose build           # 构建镜像
docker compose down            # 停止旧服务
docker compose up -d           # 启动新服务
sleep 10                       # 等待启动
docker compose ps              # 显示状态
```

---

## ✅ 验证部署

### 查看工作流状态

访问：https://github.com/dtlqwb/xinhuaproject/actions

- ✅ 绿色勾 = 部署成功
- ❌ 红色叉 = 部署失败（点击查看日志）

### 检查服务器

```bash
ssh ubuntu@82.156.165.194
cd /opt/xinhuaproject
sudo docker compose ps
```

应该看到4个running状态的容器。

### 访问网站

- 销售端：http://82.156.165.194:80
- 管理端：http://82.156.165.194:81

---

## 🔍 故障排查

### 问题1：SSH连接失败

**错误信息：**
```
Permission denied (publickey).
```

**解决：**
1. 检查私钥是否正确复制到Secrets
2. 检查公钥是否添加到服务器的authorized_keys
3. 测试SSH连接：
   ```bash
   ssh -i ~/.ssh/github_actions ubuntu@82.156.165.194
   ```

### 问题2：Git pull失败

**错误信息：**
```
fatal: Could not read from remote repository.
```

**解决：**
在服务器上配置Git SSH密钥：
```bash
ssh-keygen -t ed25519 -C "server@github"
cat ~/.ssh/id_ed25519.pub
# 添加到 GitHub: https://github.com/settings/keys
```

### 问题3：Docker命令失败

**错误信息：**
```
permission denied while trying to connect to the Docker daemon socket
```

**解决：**
```bash
# 将ubuntu用户添加到docker组
sudo usermod -aG docker ubuntu

# 重新登录
exit
ssh ubuntu@82.156.165.194
```

### 问题4：工作流一直运行

**原因：** 网络慢或Docker构建时间长

**解决：**
- 耐心等待（首次可能需要10-15分钟）
- 检查工作流日志查看进度
- 如果超过30分钟，可以取消并重新运行

---

## 🎯 最佳实践

### 1. 分支策略

```
master分支  → 自动部署到生产环境
develop分支 → 不触发部署（可选配置）
```

### 2. 部署前检查

在本地测试通过后再push：
```bash
# 前端测试
npm run build

# 后端测试
mvn test

# 确认无误后推送
git push
```

### 3. 监控部署

设置通知：
1. GitHub仓库 → Settings → Notifications
2. 启用Email通知
3. 或使用Slack/Discord集成

### 4. 回滚策略

如果新版本有问题：

```bash
# 方法1：回滚Git版本
cd /opt/xinhuaproject
sudo git log --oneline
sudo git reset --hard <previous-commit>
sudo docker compose up -d --build

# 方法2：重新部署旧版本
git revert HEAD
git push
# 会触发新的部署
```

---

## 📝 高级配置

### 只部署特定文件变化

修改 `.github/workflows/deploy.yml`：

```yaml
on:
  push:
    branches: [master]
    paths:
      - 'backend/**'
      - 'sales-frontend/**'
      - 'admin-frontend/**'
      - 'docker-compose.yml'
```

### 添加部署通知

在workflow中添加：

```yaml
- name: Notify deployment
  if: always()
  uses: actions/github-script@v6
  with:
    script: |
      const status = '${{ job.status }}';
      console.log(`Deployment ${status}`);
      // 可以集成Slack、钉钉等通知
```

### 多环境部署

```yaml
jobs:
  deploy-staging:
    if: github.ref == 'refs/heads/develop'
    # 部署到测试环境
    
  deploy-production:
    if: github.ref == 'refs/heads/master'
    # 部署到生产环境
```

---

## 🚀 完成！

配置完成后，您的部署流程变为：

```
本地开发 → git push → GitHub Actions自动部署 → 完成！
```

**无需任何手动操作！**

---

## 📞 需要帮助？

如遇到问题：
1. 查看工作流日志：https://github.com/dtlqwb/xinhuaproject/actions
2. 检查Secrets配置是否正确
3. 查看本文档的故障排查部分

祝您部署顺利！🎉
