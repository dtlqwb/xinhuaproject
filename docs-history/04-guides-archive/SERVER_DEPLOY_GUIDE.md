# 智销助手 - 云服务器部署操作指南

## 📋 服务器信息

- **IP地址**: 82.156.165.194
- **操作系统**: Ubuntu Server 24.04 LTS 64位
- **默认账户**: ubuntu
- **初始密码**: H6~4]+9wFt8pSd
- **GitHub**: https://github.com/dtlqwb/xinhuaproject

---

## 🚀 快速部署（推荐）

### 步骤1: 连接到服务器

使用SSH客户端连接：

```bash
ssh ubuntu@82.156.165.194
```

输入密码：`H6~4]+9wFt8pSd`

**Windows用户推荐使用：**
- PowerShell: `ssh ubuntu@82.156.165.194`
- PuTTY: 主机名填写 `82.156.165.194`，端口22

### 步骤2: 下载并执行部署脚本

在服务器上执行以下命令：

```bash
# 下载部署脚本
curl -O https://raw.githubusercontent.com/dtlqwb/xinhuaproject/main/deploy-to-server.sh

# 添加执行权限
chmod +x deploy-to-server.sh

# 以sudo权限运行
sudo ./deploy-to-server.sh
```

**脚本会自动完成：**
1. ✅ 更新系统
2. ✅ 安装Docker和Docker Compose
3. ✅ 配置防火墙
4. ✅ 克隆项目代码
5. ✅ 生成随机密码
6. ✅ 构建并启动所有服务

### 步骤3: 等待部署完成

首次部署约需 **5-10分钟**（取决于网络速度），主要是：
- 下载Docker镜像
- 编译前端代码
- 构建后端镜像

看到以下输出表示成功：

```
========================================
  🎉 部署完成！
========================================

📍 访问地址：
   销售端H5: http://82.156.165.194:80
   管理端H5: http://82.156.165.194:81
   后端API:  http://82.156.165.194:8080
```

### 步骤4: 记录重要信息

部署完成后，脚本会显示生成的密码，**请务必记录**：

```
数据库密码: xxxxxxxxxxxxxxxx
JWT密钥: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

这些密码保存在 `/opt/xinhuaproject/.env` 文件中。

### 步骤5: 访问系统

在浏览器中访问：

- **销售端H5**: http://82.156.165.194:80
- **管理端H5**: http://82.156.165.194:81

使用默认账号登录测试：
- 销售端: `13800138000` / `123456`
- 管理端: `admin` / `admin123`

---

## 🔧 手动部署（备选方案）

如果自动脚本失败，可以手动执行：

### 1. 连接服务器

```bash
ssh ubuntu@82.156.165.194
```

### 2. 安装Docker

```bash
# 更新系统
sudo apt-get update
sudo apt-get upgrade -y

# 安装Docker
curl -fsSL https://get.docker.com | sh
sudo systemctl start docker
sudo systemctl enable docker

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证
docker --version
docker-compose --version
```

### 3. 克隆项目

```bash
cd /opt
sudo git clone https://github.com/dtlqwb/xinhuaproject.git
cd xinhuaproject
```

### 4. 配置环境变量

```bash
# 复制示例配置
sudo cp .env.example .env

# 编辑配置
sudo vim .env
```

修改以下内容：
```env
DB_PASSWORD=your_secure_password_here
JWT_SECRET=your_jwt_secret_here_minimum_32_chars
```

### 5. 启动服务

```bash
# 构建并启动
sudo docker compose up -d

# 查看状态
sudo docker compose ps

# 查看日志
sudo docker compose logs -f
```

### 6. 配置防火墙

```bash
sudo ufw allow ssh
sudo ufw allow 80/tcp
sudo ufw allow 81/tcp
sudo ufw allow 8080/tcp
sudo ufw --force enable
```

---

## 📊 部署后检查清单

### ✅ 基础检查

```bash
# 1. 检查Docker服务
sudo docker ps

# 应该看到4个容器：
# - sales-mysql
# - sales-backend
# - sales-frontend
# - admin-frontend

# 2. 检查端口监听
sudo ss -tlnp | grep -E ':(80|81|8080)'

# 3. 测试API
curl http://localhost:8080/api/sales/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"123456"}'
```

### ✅ 功能测试

1. **销售端测试**
   - 访问 http://82.156.165.194:80
   - 使用账号登录
   - 尝试语音录入客户
   - 上传附件测试

2. **管理端测试**
   - 访问 http://82.156.165.194:81
   - 使用admin/admin123登录
   - 查看Dashboard统计
   - 检查数据是否正常

3. **后端API测试**
   ```bash
   # 测试登录接口
   curl http://82.156.165.194:8080/api/sales/login \
     -H "Content-Type: application/json" \
     -d '{"phone":"13800138000","password":"123456"}'
   
   # 应返回JSON格式的token
   ```

---

## 🛡️ 安全加固（重要！）

### 1. 修改默认密码

**立即修改管理员密码！**

通过管理端界面或数据库直接修改：

```bash
# 进入MySQL容器
sudo docker exec -it sales-mysql mysql -u root -p

# 输入.env中的DB_PASSWORD

# 修改管理员密码
USE sales_customer_db;
UPDATE admin_user SET password = '$2a$10$新的加密密码' WHERE username = 'admin';
```

或使用BCrypt生成新密码后更新。

### 2. 配置HTTPS（推荐）

使用Let's Encrypt免费证书：

```bash
# 安装certbot
sudo apt-get install certbot python3-certbot-nginx -y

# 申请证书（需要先配置域名）
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo crontab -e
# 添加: 0 3 * * * certbot renew --quiet
```

### 3. 配置fail2ban

防止暴力破解：

```bash
sudo apt-get install fail2ban -y
sudo systemctl enable fail2ban
sudo systemctl start fail2ban
```

### 4. 定期备份

创建备份脚本 `/opt/backup.sh`：

```bash
#!/bin/bash
BACKUP_DIR=/opt/backups
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# 备份数据库
sudo docker exec sales-mysql mysqldump \
  -u root -p$DB_PASSWORD \
  sales_customer_db > $BACKUP_DIR/db_$DATE.sql

# 备份上传文件
sudo tar -czf $BACKUP_DIR/uploads_$DATE.tar.gz /var/lib/docker/volumes/xinhuaproject_upload-data/

# 保留最近7天
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete

echo "Backup completed: $DATE"
```

添加到crontab：
```bash
sudo crontab -e
# 每天凌晨2点备份
0 2 * * * /opt/backup.sh >> /var/log/backup.log 2>&1
```

---

## 📝 常用运维命令

### 查看服务状态

```bash
cd /opt/xinhuaproject

# 查看所有容器
sudo docker compose ps

# 查看实时日志
sudo docker compose logs -f

# 查看特定服务日志
sudo docker compose logs -f backend
sudo docker compose logs -f sales-frontend
```

### 重启服务

```bash
# 重启所有服务
sudo docker compose restart

# 重启单个服务
sudo docker compose restart backend
```

### 更新服务

```bash
cd /opt/xinhuaproject

# 拉取最新代码
sudo git pull

# 重新构建并启动
sudo docker compose up -d --build
```

### 停止服务

```bash
sudo docker compose down
```

### 清理空间

```bash
# 清理未使用的镜像
sudo docker image prune -a

# 清理未使用的卷
sudo docker volume prune

# 查看磁盘使用
df -h
du -sh /opt/xinhuaproject/*
```

### 数据库操作

```bash
# 进入MySQL
sudo docker exec -it sales-mysql mysql -u root -p

# 备份数据库
sudo docker exec sales-mysql mysqldump -u root -p$DB_PASSWORD sales_customer_db > backup.sql

# 恢复数据库
sudo docker exec -i sales-mysql mysql -u root -p$DB_PASSWORD sales_customer_db < backup.sql
```

---

## ❓ 常见问题

### Q1: 部署脚本报错？

**检查网络连接：**
```bash
ping github.com
ping get.docker.com
```

**手动执行各步骤：**
按照"手动部署"章节逐步执行。

### Q2: 无法访问网站？

**检查防火墙：**
```bash
sudo ufw status
sudo ufw allow 80/tcp
sudo ufw allow 81/tcp
```

**检查服务是否运行：**
```bash
sudo docker compose ps
sudo docker compose logs backend
```

**检查端口监听：**
```bash
sudo ss -tlnp | grep :80
```

### Q3: 数据库连接失败？

**检查MySQL容器：**
```bash
sudo docker logs sales-mysql
sudo docker exec -it sales-mysql mysql -u root -p
```

**重置数据库：**
```bash
sudo docker compose down
sudo docker volume rm xinhuaproject_mysql-data
sudo docker compose up -d
```

### Q4: 前端页面空白？

**检查Nginx配置：**
```bash
sudo docker logs sales-frontend
sudo docker logs admin-frontend
```

**重新构建前端：**
```bash
sudo docker compose build sales-frontend admin-frontend
sudo docker compose up -d
```

### Q5: 内存不足？

**调整JVM参数：**
编辑 `backend/Dockerfile`：
```dockerfile
ENV JAVA_OPTS="-Xms256m -Xmx512m"
```

**增加Swap：**
```bash
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

---

## 📞 技术支持

如遇到问题：

1. **查看日志**
   ```bash
   sudo docker compose logs -f
   ```

2. **检查系统资源**
   ```bash
   free -h
   df -h
   top
   ```

3. **提供以下信息寻求帮助：**
   - 错误日志
   - `docker compose ps` 输出
   - 系统版本：`cat /etc/os-release`
   - Docker版本：`docker --version`

---

## 🎉 部署完成！

恭喜！您的智销助手系统已成功部署到云服务器。

**下一步建议：**
1. ✅ 测试所有功能
2. ✅ 修改默认密码
3. ✅ 配置HTTPS
4. ✅ 设置自动备份
5. ✅ 监控服务器资源

祝使用愉快！🚀
