#!/bin/bash

echo "========================================"
echo "  智销助手 - 快速部署脚本"
echo "========================================"
echo ""

# 检查Docker
if ! command -v docker &> /dev/null; then
    echo "[错误] Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose
if ! docker compose version &> /dev/null; then
    echo "[错误] Docker Compose未安装，请先安装"
    exit 1
fi

echo "[步骤1] 检查配置文件..."
if [ ! -f .env ]; then
    echo "[提示] 未找到.env文件，从.env.example创建"
    cp .env.example .env
    echo "[警告] 请编辑.env文件，修改密码和密钥！"
    read -p "按回车继续..."
fi

echo ""
echo "[步骤2] 停止旧服务..."
docker compose down 2>/dev/null

echo ""
echo "[步骤3] 拉取最新镜像..."
docker compose pull

echo ""
echo "[步骤4] 构建并启动服务..."
docker compose up -d --build

echo ""
echo "[步骤5] 等待服务启动..."
sleep 10

echo ""
echo "[步骤6] 检查服务状态..."
docker compose ps

echo ""
echo "========================================"
echo "  部署完成！"
echo "========================================"
echo ""
echo "访问地址："
echo "  销售端: http://$(hostname -I | awk '{print $1}'):80"
echo "  管理端: http://$(hostname -I | awk '{print $1}'):81"
echo "  后端API: http://$(hostname -I | awk '{print $1}'):8080"
echo ""
echo "默认账号："
echo "  销售端: 13800138000 / 123456"
echo "  管理端: admin / admin123"
echo ""
echo "查看日志："
echo "  docker compose logs -f"
echo ""
echo "停止服务："
echo "  docker compose down"
echo ""
