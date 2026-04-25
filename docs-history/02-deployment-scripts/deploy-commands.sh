#!/bin/bash
set -e

echo "=========================================="
echo "  开始自动部署..."
echo "=========================================="

# 进入/opt目录
cd /opt

# 克隆项目
echo "[1/4] 克隆项目代码..."
sudo git clone https://github.com/dtlqwb/xinhuaproject.git

# 进入项目目录
cd xinhuaproject

# 配置文件
echo "[2/4] 配置环境变量..."
sudo cp .env.example .env

# 添加密码配置
echo 'DB_PASSWORD=prod_db_pass_2024' | sudo tee -a .env > /dev/null
echo 'JWT_SECRET=jwt_prod_secret_key_2024_min_32_chars' | sudo tee -a .env > /dev/null

echo "✓ 配置完成"

# 启动服务
echo "[3/4] 启动Docker服务..."
sudo docker compose up -d

echo "[4/4] 等待服务启动..."
sleep 15

# 检查状态
echo ""
echo "=========================================="
echo "  服务状态："
echo "=========================================="
sudo docker compose ps

echo ""
echo "=========================================="
echo "  ✅ 部署完成！"
echo "=========================================="
echo ""
echo "访问地址："
echo "  销售端: http://82.156.165.194:80"
echo "  管理端: http://82.156.165.194:81"
echo ""
echo "默认账号："
echo "  销售端: 13800138000 / 123456"
echo "  管理端: admin / admin123"
echo ""
