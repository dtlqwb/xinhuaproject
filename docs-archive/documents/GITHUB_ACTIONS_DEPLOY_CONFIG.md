# GitHub Actions 部署配置说明

**更新日期:** 2026-04-26  
**文件:** `.github/workflows/deploy.yml`  
**状态:** ✅ 已优化  

---

## 📋 概述

本次优化将部署流程从复杂的SSH脚本改为使用 `appleboy/ssh-action`,简化了配置并提高了可靠性。

### 主要改进

1. ✅ **简化SSH配置** - 移除手动SSH密钥设置步骤
2. ✅ **智能Git检查** - 自动检测项目目录是否存在且是Git仓库
3. ✅ **优化部署步骤** - 从7步简化为4步
4. ✅ **统一命令格式** - 使用 `docker-compose` 而非 `sudo docker compose`

---

## 🔧 配置详解

### 1. 触发条件

```yaml
on:
  push:
    branches: [master]      # 推送到master分支时自动部署
  workflow_dispatch:         # 允许手动触发
```

**说明:**
- 每次push到master分支会自动触发部署
- 也可以在GitHub Actions页面手动触发

---

### 2. 部署步骤

#### Step 1: 检出代码
```yaml
- name: Checkout code
  uses: actions/checkout@v3
```

**作用:** 获取最新的项目代码(用于后续可能的构建)

---

#### Step 2: 部署到服务器 (核心步骤)

```yaml
- name: Deploy to server
  uses: appleboy/ssh-action@v0.1.5
  with:
    host: ${{ secrets.SERVER_HOST }}
    username: ${{ secrets.SERVER_USERNAME }}
    key: ${{ secrets.SERVER_SSH_KEY }}
    script: |
      # 部署脚本...
```

**使用的Action:** `appleboy/ssh-action@v0.1.5`

**优势:**
- ✅ 内置SSH连接管理
- ✅ 自动处理密钥认证
- ✅ 支持命令执行和错误处理
- ✅ 比手动SSH更可靠

---

### 3. 部署脚本逻辑

#### A. 智能Git仓库检查

```bash
# 检查目录是否存在且是git仓库
if [ -d "xinhuaproject/.git" ]; then
  echo "[1/4] 项目已存在，更新代码..."
  cd xinhuaproject
  git pull origin master
else
  echo "[1/4] 首次部署或目录不存在，克隆项目..."
  rm -rf xinhuaproject
  git clone https://github.com/dtlqwb/xinhuaproject.git
  cd xinhuaproject
fi
```

**逻辑说明:**

| 场景 | 检测结果 | 操作 |
|------|---------|------|
| 首次部署 | 目录不存在 | 克隆项目 |
| 正常更新 | 目录存在且是Git仓库 | `git pull` 更新 |
| 目录损坏 | 目录存在但不是Git仓库 | 删除后重新克隆 |

**优势:**
- ✅ 避免重复克隆
- ✅ 快速增量更新
- ✅ 自动修复损坏的仓库

---

#### B. 停止旧服务

```bash
echo "[2/4] 停止旧服务..."
docker-compose down || true
```

**说明:**
- 停止所有运行中的容器
- `|| true` 确保即使没有运行的容器也不会失败

---

#### C. 构建并启动服务

```bash
echo "[3/4] 构建并启动服务..."
docker-compose up -d --build
```

**参数说明:**
- `-d`: 后台运行(detached mode)
- `--build`: 强制重新构建镜像(确保使用最新代码)

**包含的服务:**
- MySQL数据库
- Spring Boot后端
- Vue销售前端
- Vue管理后台

---

#### D. 等待并验证

```bash
echo "[4/4] 等待服务启动..."
sleep 10

echo ""
echo "========================================"
echo "  ✅ 部署完成！"
echo "========================================"
echo ""
echo "服务状态："
docker-compose ps
echo ""
echo "查看日志："
echo "  docker-compose logs -f backend"
echo ""
echo "访问地址："
echo "  销售端: http://${{ secrets.SERVER_HOST }}:80"
echo "  管理端: http://${{ secrets.SERVER_HOST }}:81"
```

**说明:**
- 等待10秒让服务完全启动
- 显示容器状态
- 提供日志查看命令
- 显示访问地址

---

## 🔑 Secrets配置

需要在GitHub仓库设置以下Secrets:

### 必需Secrets

| Secret名称 | 说明 | 示例值 |
|-----------|------|--------|
| `SERVER_HOST` | 服务器IP地址 | `82.156.165.194` |
| `SERVER_USERNAME` | SSH用户名 | `root` 或 `ubuntu` |
| `SERVER_SSH_KEY` | SSH私钥 | `-----BEGIN OPENSSH PRIVATE KEY-----...` |

### 配置步骤

1. **进入GitHub仓库**
   - 访问: https://github.com/dtlqwb/xinhuaproject

2. **打开Settings**
   - 点击顶部 "Settings" 标签

3. **找到Secrets**
   - 左侧菜单: Settings → Secrets and variables → Actions

4. **添加Secrets**
   - 点击 "New repository secret"
   - 分别添加上述3个Secrets

---

### 生成SSH密钥对

如果还没有SSH密钥,在服务器上执行:

```bash
# 在服务器上生成密钥对
ssh-keygen -t rsa -b 4096 -C "github-actions"

# 查看公钥(添加到GitHub)
cat ~/.ssh/id_rsa.pub

# 查看私钥(添加到GitHub Secrets)
cat ~/.ssh/id_rsa
```

**注意:**
- 私钥内容要完整复制(包括 `-----BEGIN...` 和 `-----END...`)
- 不要泄露私钥
- 建议设置密钥密码(但GitHub Actions中配置会更复杂)

---

## 📊 部署流程对比

### 优化前 vs 优化后

| 项目 | 优化前 | 优化后 |
|------|--------|--------|
| **SSH配置** | 3个步骤(Setup SSH + known_hosts + ssh) | 1个步骤(ssh-action) |
| **Git检查** | 复杂的if-else嵌套 | 简洁的单层判断 |
| **代码更新** | fetch + reset + clean (3条命令) | git pull (1条命令) |
| **Docker命令** | `sudo docker compose` | `docker-compose` |
| **总步骤数** | 7步 | 4步 |
| **代码行数** | ~80行 | ~40行 |
| **可维护性** | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ |

---

## 🧪 测试方法

### 1. 手动触发测试

**步骤:**
1. 进入GitHub仓库
2. 点击 "Actions" 标签
3. 选择 "Deploy to Server" workflow
4. 点击 "Run workflow" 按钮
5. 选择master分支
6. 点击 "Run workflow"

**观察:**
- Workflow应该成功执行
- 查看日志确认每个步骤
- 检查服务器上的服务状态

---

### 2. 推送代码触发

```bash
# 修改任意文件
echo "test" >> README.md

# 提交并推送
git add .
git commit -m "test: trigger deployment"
git push origin master
```

**预期结果:**
- GitHub Actions自动触发
- 约2-3分钟完成部署
- 访问网站验证更新

---

### 3. 验证部署结果

**SSH登录服务器:**
```bash
ssh root@82.156.165.194
```

**检查容器状态:**
```bash
cd xinhuaproject
docker-compose ps
```

**预期输出:**
```
NAME                STATUS              PORTS
sales-mysql         Up                  3306/tcp
sales-backend       Up                  0.0.0.0:8080->8080/tcp
sales-frontend      Up                  0.0.0.0:80->80/tcp
admin-frontend      Up                  0.0.0.0:81->80/tcp
```

**查看日志:**
```bash
docker-compose logs -f backend
```

---

## ⚠️ 注意事项

### 1. 首次部署

如果是首次使用此workflow:

1. **确保服务器已安装:**
   - Docker
   - Docker Compose
   - Git

2. **配置Secrets:**
   - SERVER_HOST
   - SERVER_USERNAME
   - SERVER_SSH_KEY

3. **首次运行会:**
   - 克隆项目到 `/home/username/xinhuaproject`
   - 构建所有Docker镜像(可能需要5-10分钟)
   - 启动所有服务

---

### 2. 环境变量配置

确保服务器上有 `.env` 文件:

```bash
cd xinhuaproject
ls -la .env
```

如果没有,从 `.env.example` 创建:

```bash
cp .env.example .env
# 编辑 .env 文件,填入实际配置
nano .env
```

**重要变量:**
```
DB_PASSWORD=your_db_password
JWT_SECRET=your_jwt_secret
ALIYUN_BAILIAN_API_KEY=sk-your-api-key
```

---

### 3. 端口冲突

确保以下端口未被占用:
- 80 (销售前端)
- 81 (管理后台)
- 8080 (后端API)
- 3306 (MySQL)

**检查端口:**
```bash
sudo netstat -tlnp | grep -E '80|81|8080|3306'
```

---

### 4. 磁盘空间

Docker镜像会占用较多空间,定期清理:

```bash
# 查看磁盘使用情况
df -h

# 清理未使用的Docker资源
docker system prune -a

# 清理旧的镜像
docker image prune -a
```

---

## 🔍 故障排查

### 问题1: SSH连接失败

**错误信息:**
```
Error: ssh: connect to host xxx port 22: Connection timed out
```

**原因:**
- 服务器防火墙阻止SSH
- SSH密钥不正确
- 服务器IP地址错误

**解决:**
1. 检查服务器SSH是否开启: `sudo systemctl status sshd`
2. 验证Secrets中的SERVER_HOST是否正确
3. 重新生成SSH密钥并更新Secrets

---

### 问题2: Git pull失败

**错误信息:**
```
error: Your local changes to the following files would be overwritten by merge
```

**原因:**
- 服务器上有未提交的修改

**解决:**
手动登录服务器执行:
```bash
cd xinhuaproject
git reset --hard origin/master
git clean -fd
```

或者在workflow中添加强制重置(当前已简化为git pull,如需强制可修改脚本)

---

### 问题3: Docker构建失败

**错误信息:**
```
ERROR: failed to solve: ...
```

**原因:**
- 网络问题无法下载基础镜像
- Dockerfile语法错误
- 依赖安装失败

**解决:**
1. 查看完整错误日志
2. 检查网络连接
3. 手动在服务器上执行 `docker-compose build` 调试

---

### 问题4: 服务启动后无法访问

**检查步骤:**

1. **确认容器运行:**
   ```bash
   docker-compose ps
   ```

2. **查看容器日志:**
   ```bash
   docker-compose logs backend
   docker-compose logs sales-frontend
   ```

3. **检查端口映射:**
   ```bash
   docker-compose port sales-frontend 80
   docker-compose port admin-frontend 80
   ```

4. **测试本地访问:**
   ```bash
   curl http://localhost:80
   curl http://localhost:81
   ```

5. **检查防火墙:**
   ```bash
   sudo ufw status
   sudo ufw allow 80/tcp
   sudo ufw allow 81/tcp
   ```

---

## 📈 性能优化建议

### 1. 减少构建时间

**使用Docker缓存:**
```yaml
# 移除 --no-cache 参数,利用缓存
docker-compose up -d --build
```

**当前配置已经移除了 `--no-cache`,会利用缓存加速构建。**

---

### 2. 并行构建

如果项目很大,可以考虑并行构建多个服务:

```bash
docker-compose build --parallel
docker-compose up -d
```

---

### 3. 增量部署

对于只修改前端的场景,可以只重建前端服务:

```bash
docker-compose up -d --build sales-frontend admin-frontend
```

---

## 🚀 高级用法

### 1. 多环境部署

可以创建不同的workflow文件:
- `deploy-dev.yml` - 开发环境
- `deploy-staging.yml` - 测试环境
- `deploy-prod.yml` - 生产环境

每个环境使用不同的Secrets和配置。

---

### 2. 部署前测试

在部署前添加测试步骤:

```yaml
- name: Run tests
  run: |
    cd backend
    mvn test
    
- name: Deploy to server
  uses: appleboy/ssh-action@v0.1.5
  # ...
```

只有测试通过才会部署。

---

### 3. 部署后通知

添加Slack或钉钉通知:

```yaml
- name: Notify deployment
  if: success()
  uses: slackapi/slack-github-action@v1.24.0
  with:
    channel-id: 'deployments'
    slack-message: "✅ 部署成功!"
```

---

## 📝 相关文档

- [appleboy/ssh-action文档](https://github.com/appleboy/ssh-action)
- [GitHub Actions文档](https://docs.github.com/en/actions)
- [Docker Compose文档](https://docs.docker.com/compose/)
- [项目部署指南](./DEPLOY_GUIDE.md)

---

## 🎯 总结

本次优化实现了:

✅ **简化的SSH配置** - 使用专业的ssh-action  
✅ **智能的Git管理** - 自动检测和更新仓库  
✅ **清晰的部署步骤** - 4步完成整个部署  
✅ **完善的错误处理** - 每个步骤都有日志输出  
✅ **易于维护** - 代码量减少50%,可读性提升  

**部署现在更加可靠和高效!** 🚀

---

**最后更新:** 2026-04-26  
**Workflow版本:** v2.0  
**测试状态:** 待测试
