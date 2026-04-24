#!/bin/bash

# ============================================
# Git自动部署脚本
# 放置在服务器上，通过webhook或cron触发
# ============================================

set -e

DEPLOY_DIR="/opt/xinhuaproject"
BACKUP_DIR="/opt/backups/xinhuaproject"
DATE=$(date +%Y%m%d_%H%M%S)

echo "========================================"
echo "  Git自动部署开始"
echo "  时间: $(date)"
echo "========================================"

# 创建备份目录
mkdir -p $BACKUP_DIR

# 进入项目目录
cd $DEPLOY_DIR

# 备份当前版本（可选）
echo "[1/6] 备份当前版本..."
if [ -d ".git" ]; then
    git archive --format=tar HEAD | gzip > $BACKUP_DIR/backup_$DATE.tar.gz
    echo "备份完成: backup_$DATE.tar.gz"
fi

# 拉取最新代码
echo "[2/6] 拉取最新代码..."
git fetch origin
git reset --hard origin/master

# 检查是否有.env文件
echo "[3/6] 检查配置文件..."
if [ ! -f .env ]; then
    echo "[警告] .env文件不存在，从示例创建"
    cp .env.example .env
    echo "[警告] 请手动编辑.env文件配置密码"
fi

# 重新构建Docker镜像
echo "[4/6] 重新构建Docker镜像..."
docker compose build

# 重启服务
echo "[5/6] 重启服务..."
docker compose down
docker compose up -d

# 等待服务启动
echo "[6/6] 等待服务启动..."
sleep 10

# 检查服务状态
echo ""
echo "服务状态："
docker compose ps

# 清理旧备份（保留最近5个）
echo ""
echo "清理旧备份..."
cd $BACKUP_DIR
ls -t backup_*.tar.gz | tail -n +6 | xargs -r rm -f

echo ""
echo "========================================"
echo "  ✅ 部署完成！"
echo "========================================"
echo ""
echo "访问地址："
echo "  销售端: http://$(curl -s ifconfig.me):80"
echo "  管理端: http://$(curl -s ifconfig.me):81"
echo ""
