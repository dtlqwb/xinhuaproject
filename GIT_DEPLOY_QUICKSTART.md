# Git部署 - 快速开始（5分钟）

## 🎯 最简单的Git部署方式

### 前提条件

- ✅ 代码已推送到GitHub
- ✅ 可以SSH连接到服务器
- ✅ 服务器上已安装Docker

---

## 🚀 3步完成Git部署

### 第1步：首次部署（如果还没部署）

```bash
# 连接服务器
ssh ubuntu@82.156.165.194
# 密码: H6~4]+9wFt8pSd

# 克隆项目并部署
cd /opt
sudo git clone https://github.com/dtlqwb/xinhuaproject.git
cd xinhuaproject

# 配置环境变量
sudo cp .env.example .env
sudo vi .env  # 修改DB_PASSWORD和JWT_SECRET

# 启动服务
sudo docker compose up -d
```

等待5-10分钟，访问 http://82.156.165.194:80 测试。

---

### 第2步：设置自动部署脚本（一次性）

```bash
# 在服务器上执行
cd /opt/xinhuaproject

# 上传的auto-deploy.sh已经在项目中
sudo chmod +x auto-deploy.sh

# 创建快捷命令
sudo ln -s /opt/xinhuaproject/auto-deploy.sh /usr/local/bin/deploy-xinhua
```

---

### 第3步：日常更新（每次修改代码后）

```bash
# 在本地电脑上
git add .
git commit -m "描述更改"
git push

# 在服务器上（一行命令）
ssh ubuntu@82.156.165.194 "sudo deploy-xinhua"
```

**就这么简单！** 🎉

---

## 📊 完整工作流程

### 开发 → 部署流程

```
1. 本地开发代码
   ↓
2. git add . && git commit -m "..."
   ↓
3. git push
   ↓
4. ssh ubuntu@82.156.165.194 "sudo deploy-xinhua"
   ↓
5. 等待3-5分钟自动完成
   ↓
6. 访问网站验证
```

---

## 🔧 常用命令速查

### 部署相关

```bash
# 部署最新版本
sudo deploy-xinhua

# 手动部署（不使用脚本）
cd /opt/xinhuaproject
sudo git pull
sudo docker compose up -d --build
```

### 查看状态

```bash
# 查看服务状态
sudo docker compose ps

# 查看日志
sudo docker compose logs -f
sudo docker compose logs -f backend

# 查看资源使用
docker stats
```

### 维护操作

```bash
# 重启服务
sudo docker compose restart

# 停止服务
sudo docker compose down

# 清理空间
sudo docker system prune -a
```

---

## ⚡ 进阶技巧

### 技巧1：创建本地别名

在本地电脑的 `~/.bashrc` 或 PowerShell profile 中添加：

**Linux/Mac:**
```bash
alias xinhua-deploy='ssh ubuntu@82.156.165.194 "sudo deploy-xinhua"'
```

**Windows PowerShell:**
```powershell
function Deploy-Xinhua {
    ssh ubuntu@82.156.165.194 "sudo deploy-xinhua"
}
```

然后只需执行：
```bash
xinhua-deploy
# 或
Deploy-Xinhua
```

### 技巧2：一键推送并部署

创建脚本 `push-and-deploy.sh`：

```bash
#!/bin/bash
git add .
git commit -m "$1"
git push
ssh ubuntu@82.156.165.194 "sudo deploy-xinhua"
```

使用：
```bash
./push-and-deploy.sh "更新功能"
```

### 技巧3：监控部署状态

```bash
# 实时查看部署日志
ssh ubuntu@82.156.165.194 "tail -f /opt/xinhuaproject/deploy.log"
```

---

## ❓ 常见问题

### Q: 第一次部署需要多久？

A: 约5-10分钟，主要时间是下载Docker镜像和编译前端。

### Q: 后续更新需要多久？

A: 约2-5分钟，取决于代码改动大小。

### Q: 部署会中断服务吗？

A: 会有短暂中断（约10-30秒），对于个人项目可以接受。

### Q: 如何回滚？

```bash
ssh ubuntu@82.156.165.194
cd /opt/xinhuaproject
git log --oneline  # 查看历史
git reset --hard <commit-hash>
sudo docker compose up -d --build
```

---

## 🎯 总结

**Git部署的核心就是：**

1. **本地push代码到GitHub**
2. **服务器pull代码并重新部署**

使用自动部署脚本可以让这个过程更简单、更安全。

---

**现在开始试试吧！** 🚀

```bash
# 修改一些代码
git push
ssh ubuntu@82.156.165.194 "sudo deploy-xinhua"
```
