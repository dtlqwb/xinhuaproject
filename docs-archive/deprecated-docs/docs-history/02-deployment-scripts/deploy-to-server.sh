#!/bin/bash

# ============================================
# 智销助手 - 云服务器自动部署脚本
# 适用于 Ubuntu Server 24.04 LTS
# ============================================

set -e  # 遇到错误立即退出

echo "========================================"
echo "  智销助手 - 自动部署脚本"
echo "  Ubuntu Server 24.04 LTS"
echo "========================================"
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 日志函数
log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查是否为root或sudo
if [ "$EUID" -ne 0 ]; then 
    log_error "请使用 sudo 运行此脚本"
    exit 1
fi

# 步骤1: 更新系统
log_info "[步骤1/8] 更新系统..."
apt-get update -y
apt-get upgrade -y

# 步骤2: 安装必要工具
log_info "[步骤2/8] 安装必要工具..."
apt-get install -y \
    curl \
    wget \
    git \
    vim \
    ufw \
    fail2ban \
    apt-transport-https \
    ca-certificates \
    software-properties-common

# 步骤3: 安装Docker
log_info "[步骤3/8] 安装Docker..."
if ! command -v docker &> /dev/null; then
    curl -fsSL https://get.docker.com | sh
    systemctl start docker
    systemctl enable docker
    log_info "Docker安装完成"
else
    log_info "Docker已安装"
fi

# 步骤4: 安装Docker Compose
log_info "[步骤4/8] 安装Docker Compose..."
if ! docker compose version &> /dev/null; then
    DOCKER_CONFIG=${DOCKER_CONFIG:-$HOME/.docker}
    mkdir -p $DOCKER_CONFIG/cli-plugins
    curl -SL https://github.com/docker/compose/releases/latest/download/docker-compose-linux-x86_64 -o $DOCKER_CONFIG/cli-plugins/docker-compose
    chmod +x $DOCKER_CONFIG/cli-plugins/docker-compose
    ln -s $DOCKER_CONFIG/cli-plugins/docker-compose /usr/local/bin/docker-compose
    log_info "Docker Compose安装完成"
else
    log_info "Docker Compose已安装"
fi

# 验证安装
log_info "验证Docker安装..."
docker --version
docker compose version

# 步骤5: 配置防火墙
log_info "[步骤5/8] 配置防火墙..."
ufw --force reset
ufw default deny incoming
ufw default allow outgoing
ufw allow ssh
ufw allow 80/tcp
ufw allow 443/tcp
ufw allow 8080/tcp
ufw --force enable
log_info "防火墙配置完成"

# 步骤6: 创建部署目录
log_info "[步骤6/8] 创建部署目录..."
DEPLOY_DIR="/opt/xinhuaproject"
mkdir -p $DEPLOY_DIR
cd $DEPLOY_DIR

# 步骤7: 克隆项目
log_info "[步骤7/8] 克隆项目代码..."
if [ -d ".git" ]; then
    log_info "项目已存在，拉取最新代码..."
    git pull
else
    git clone https://github.com/dtlqwb/xinhuaproject.git .
fi

# 步骤8: 配置环境变量
log_info "[步骤8/8] 配置环境变量..."
if [ ! -f .env ]; then
    cp .env.example .env
    log_warn "请编辑 .env 文件修改密码和密钥！"
    
    # 生成随机密码
    DB_PASSWORD=$(openssl rand -base64 32 | tr -dc 'a-zA-Z0-9' | head -c 16)
    JWT_SECRET=$(openssl rand -base64 48 | tr -dc 'a-zA-Z0-9' | head -c 64)
    
    sed -i "s/DB_PASSWORD=.*/DB_PASSWORD=$DB_PASSWORD/" .env
    sed -i "s/JWT_SECRET=.*/JWT_SECRET=$JWT_SECRET/" .env
    
    log_info "已生成随机密码并保存到 .env 文件"
    log_warn "请记录以下信息："
    echo ""
    echo "数据库密码: $DB_PASSWORD"
    echo "JWT密钥: $JWT_SECRET"
    echo ""
else
    log_info ".env 文件已存在，跳过配置"
fi

# 构建并启动服务
echo ""
log_info "开始构建和启动服务..."
echo ""

# 停止旧服务
docker compose down 2>/dev/null || true

# 构建镜像
log_info "构建Docker镜像（首次可能需要几分钟）..."
docker compose build

# 启动服务
log_info "启动服务..."
docker compose up -d

# 等待服务启动
log_info "等待服务启动..."
sleep 15

# 检查服务状态
echo ""
log_info "检查服务状态..."
docker compose ps

# 获取服务器IP
SERVER_IP=$(curl -s ifconfig.me)

echo ""
echo "========================================"
echo "  🎉 部署完成！"
echo "========================================"
echo ""
echo "📍 访问地址："
echo "   销售端H5: http://$SERVER_IP:80"
echo "   管理端H5: http://$SERVER_IP:81"
echo "   后端API:  http://$SERVER_IP:8080"
echo ""
echo "🔑 默认账号："
echo "   销售端: 13800138000 / 123456"
echo "   管理端: admin / admin123"
echo ""
echo "📊 常用命令："
echo "   查看日志:     cd $DEPLOY_DIR && docker compose logs -f"
echo "   重启服务:     cd $DEPLOY_DIR && docker compose restart"
echo "   停止服务:     cd $DEPLOY_DIR && docker compose down"
echo "   更新服务:     cd $DEPLOY_DIR && git pull && docker compose up -d --build"
echo ""
echo "🛡️ 安全提示："
echo "   1. 已配置防火墙，仅开放必要端口"
echo "   2. 请修改默认密码"
echo "   3. 建议配置HTTPS证书"
echo ""
echo "📝 配置文件位置："
echo "   项目目录: $DEPLOY_DIR"
echo "   环境配置: $DEPLOY_DIR/.env"
echo "   数据卷:   /var/lib/docker/volumes/"
echo ""
log_info "部署日志已保存，如有问题请查看日志"
echo ""
