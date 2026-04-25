# 智销助手 - 云服务器部署指南

## 📋 目录

- [系统要求](#系统要求)
- [方案一：Docker部署（推荐）](#方案一docker部署推荐)
- [方案二：手动部署](#方案二手动部署)
- [配置说明](#配置说明)
- [常见问题](#常见问题)

---

## 系统要求

### 最低配置
- CPU: 2核
- 内存: 4GB
- 磁盘: 50GB
- 操作系统: CentOS 7+ / Ubuntu 18.04+ / Debian 9+

### 推荐配置
- CPU: 4核
- 内存: 8GB
- 磁盘: 100GB SSD
- 带宽: 5Mbps+

---

## 方案一：Docker部署（推荐⭐）

### 优点
- ✅ 一键部署，简单快速
- ✅ 环境隔离，避免冲突
- ✅ 易于维护和升级
- ✅ 自动管理依赖

### 步骤

#### 1. 安装Docker和Docker Compose

**CentOS/RHEL:**
```bash
# 安装Docker
curl -fsSL https://get.docker.com | sh
systemctl start docker
systemctl enable docker

# 安装Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

**Ubuntu/Debian:**
```bash
# 安装Docker
curl -fsSL https://get.docker.com | sh
sudo systemctl start docker
sudo systemctl enable docker

# 安装Docker Compose
sudo apt-get install docker-compose-plugin
```

验证安装：
```bash
docker --version
docker compose version
```

#### 2. 上传项目文件

将项目打包上传到服务器：
```bash
# 本地打包
tar -czf sales-system.tar.gz \
  --exclude='node_modules' \
  --exclude='.git' \
  --exclude='dist' \
  --exclude='target' \
  .

# 上传到服务器
scp sales-system.tar.gz root@your-server-ip:/opt/

# 服务器上解压
cd /opt
tar -xzf sales-system.tar.gz
cd xinhuaproject
```

#### 3. 配置环境变量

创建 `.env` 文件：
```bash
cp .env.example .env
vi .env
```

编辑配置：
```env
# 数据库密码
DB_PASSWORD=your_secure_password

# JWT密钥（生产环境务必修改）
JWT_SECRET=your_production_jwt_secret_key_here

# 其他配置保持默认即可
```

#### 4. 启动服务

```bash
# 构建并启动所有服务
docker compose up -d

# 查看运行状态
docker compose ps

# 查看日志
docker compose logs -f
```

#### 5. 访问系统

- **销售端H5**: http://your-server-ip:80
- **管理端H5**: http://your-server-ip:81
- **后端API**: http://your-server-ip:8080

默认账号：
- 销售端: 13800138000 / 123456
- 管理端: admin / admin123

---

## 方案二：手动部署

### 1. 准备服务器环境

#### 安装Java 8
```bash
# CentOS
yum install java-1.8.0-openjdk-devel -y

# Ubuntu
apt-get install openjdk-8-jdk -y

# 验证
java -version
```

#### 安装MySQL 8.0
```bash
# CentOS
yum install mysql-server -y
systemctl start mysqld
systemctl enable mysqld

# Ubuntu
apt-get install mysql-server -y
systemctl start mysql
systemctl enable mysql

# 初始化
mysql_secure_installation
```

#### 安装Nginx
```bash
# CentOS
yum install nginx -y
systemctl start nginx
systemctl enable nginx

# Ubuntu
apt-get install nginx -y
systemctl start nginx
systemctl enable nginx
```

#### 安装Node.js（用于构建前端）
```bash
curl -fsSL https://deb.nodesource.com/setup_18.x | bash -
apt-get install -y nodejs

# 或者使用nvm
curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.0/install.sh | bash
nvm install 18
```

### 2. 部署数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行SQL脚本
source /path/to/backend/src/main/resources/schema.sql

# 退出
exit
```

### 3. 部署后端

#### 编译打包
```bash
cd backend

# 首次需要安装Maven
# 下载: https://maven.apache.org/download.cgi
# 或使用项目中的auto-install-maven.ps1

# 打包
mvn clean package -DskipTests

# 生成的JAR包在 target/customer-system-1.0.0.jar
```

#### 创建启动脚本
```bash
cat > /opt/sales-backend/start.sh << 'EOF'
#!/bin/bash
APP_NAME=customer-system-1.0.0.jar
APP_DIR=/opt/sales-backend

export DB_URL=jdbc:mysql://localhost:3306/sales_customer_db?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai
export DB_USERNAME=root
export DB_PASSWORD=your_password
export JWT_SECRET=your_jwt_secret
export FILE_UPLOAD_PATH=/data/uploads/

mkdir -p /data/uploads /data/logs

nohup java -Xms512m -Xmx1024m -jar $APP_DIR/$APP_NAME \
  --spring.profiles.active=prod \
  > /data/logs/app.log 2>&1 &

echo $! > $APP_DIR/app.pid
echo "Application started with PID $(cat $APP_DIR/app.pid)"
EOF

chmod +x /opt/sales-backend/start.sh
```

#### 启动后端
```bash
/opt/sales-backend/start.sh

# 查看日志
tail -f /data/logs/app.log
```

### 4. 部署前端

#### 构建销售端
```bash
cd sales-frontend

# 安装依赖
npm install

# 构建
npm run build

# 上传到Nginx
cp -r dist/* /usr/share/nginx/html/sales/
```

#### 构建管理端
```bash
cd admin-frontend

# 安装依赖
npm install

# 构建
npm run build

# 上传到Nginx
cp -r dist/* /usr/share/nginx/html/admin/
```

#### 配置Nginx

创建配置文件 `/etc/nginx/conf.d/sales.conf`:
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 销售端
    location /sales {
        alias /usr/share/nginx/html/sales;
        try_files $uri $uri/ /sales/index.html;
    }

    # 管理端
    location /admin {
        alias /usr/share/nginx/html/admin;
        try_files $uri $uri/ /admin/index.html;
    }

    # 后端API代理
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket支持
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # 文件上传
    client_max_body_size 50M;
}
```

重启Nginx：
```bash
nginx -t
systemctl restart nginx
```

---

## 配置说明

### 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DB_URL | 数据库连接URL | jdbc:mysql://localhost:3306/sales_customer_db |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | root |
| JWT_SECRET | JWT密钥 | salesCustomerSystemSecretKey2024Production |
| JWT_EXPIRATION | Token过期时间(毫秒) | 86400000 (24小时) |
| FILE_UPLOAD_PATH | 文件上传路径 | /data/uploads/ |

### 端口说明

| 服务 | 端口 | 说明 |
|------|------|------|
| 销售端H5 | 80 | Nginx静态资源 |
| 管理端H5 | 81 | Nginx静态资源 |
| 后端API | 8080 | Spring Boot应用 |
| MySQL | 3306 | 数据库服务 |

---

## 安全配置

### 1. 配置防火墙

```bash
# CentOS (firewalld)
firewall-cmd --permanent --add-port=80/tcp
firewall-cmd --permanent --add-port=443/tcp
firewall-cmd --reload

# Ubuntu (ufw)
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable
```

### 2. 配置HTTPS（推荐）

使用Let's Encrypt免费证书：
```bash
# 安装certbot
yum install certbot python3-certbot-nginx -y

# 申请证书
certbot --nginx -d your-domain.com

# 自动续期
crontab -e
# 添加: 0 3 * * * certbot renew --quiet
```

### 3. 修改默认密码

**重要！** 部署后立即修改：
- MySQL root密码
- JWT_SECRET
- 管理员密码

---

## 维护命令

### Docker方式

```bash
# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f backend
docker compose logs -f sales-frontend

# 重启服务
docker compose restart backend

# 停止服务
docker compose down

# 更新服务
docker compose pull
docker compose up -d

# 清理空间
docker system prune -a
```

### 手动方式

```bash
# 查看后端进程
ps aux | grep customer-system

# 停止后端
kill $(cat /opt/sales-backend/app.pid)

# 启动后端
/opt/sales-backend/start.sh

# 查看日志
tail -f /data/logs/app.log

# 备份数据库
mysqldump -u root -p sales_customer_db > backup_$(date +%Y%m%d).sql

# 恢复数据库
mysql -u root -p sales_customer_db < backup_20240101.sql
```

---

## 常见问题

### Q1: Docker启动失败？

检查日志：
```bash
docker compose logs
```

常见原因：
- 端口被占用
- 内存不足
- 配置文件错误

### Q2: 后端无法连接数据库？

检查：
```bash
# 测试数据库连接
mysql -h localhost -u root -p

# 检查MySQL是否运行
systemctl status mysqld

# 检查防火墙
firewall-cmd --list-ports
```

### Q3: 前端页面空白？

检查：
- Nginx配置是否正确
- 前端资源路径是否正确
- 浏览器控制台是否有错误

### Q4: 文件上传失败？

检查：
- 上传目录权限：`chmod 755 /data/uploads`
- Nginx配置：`client_max_body_size 50M`
- 后端配置：`file.upload-path`

### Q5: 性能优化？

- 增加JVM内存：修改 `JAVA_OPTS`
- 启用Gzip压缩
- 配置CDN加速静态资源
- 使用Redis缓存（后续版本）

---

## 监控和日志

### 查看应用日志
```bash
# Docker方式
docker compose logs -f backend

# 手动方式
tail -f /data/logs/app.log
```

### 监控资源使用
```bash
# 查看CPU和内存
docker stats

# 查看磁盘使用
df -h
du -sh /data/*
```

---

## 备份策略

### 数据库备份
```bash
#!/bin/bash
BACKUP_DIR=/data/backups
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

mysqldump -u root -p$DB_PASSWORD \
  --single-transaction \
  --routines \
  --triggers \
  sales_customer_db > $BACKUP_DIR/db_$DATE.sql

# 保留最近7天
find $BACKUP_DIR -name "db_*.sql" -mtime +7 -delete

echo "Backup completed: db_$DATE.sql"
```

添加到crontab：
```bash
0 2 * * * /opt/backup-db.sh >> /var/log/backup.log 2>&1
```

---

## 技术支持

如遇到问题，请提供：
1. 错误日志
2. 系统环境信息
3. 复现步骤

祝您部署顺利！🎉
