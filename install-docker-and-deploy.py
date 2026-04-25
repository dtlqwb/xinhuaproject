#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
安装Docker并部署项目
"""

import paramiko
import time

HOST = '82.156.165.194'
USERNAME = 'ubuntu'
PASSWORD = 'H6~4]+9wFt8pSd'

def run(ssh, cmd, show=True):
    if show:
        print(f"> {cmd}")
    stdin, stdout, stderr = ssh.exec_command(cmd)
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    if out:
        print(out)
    if err:
        print(err)
    return stdout.channel.recv_exit_status()

print("=" * 60)
print("  安装Docker并部署")
print("=" * 60)

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
print("\n连接服务器...")
ssh.connect(HOST, username=USERNAME, password=PASSWORD, timeout=30)
print("✓ 已连接\n")

# 1. 安装Docker
print("=" * 60)
print("步骤1: 安装Docker")
print("=" * 60)
run(ssh, "docker --version 2>/dev/null && echo 'Docker已安装' || echo '需要安装'")
print("\n开始安装Docker（这可能需要几分钟）...")
run(ssh, "curl -fsSL https://get.docker.com | sudo sh")
run(ssh, "sudo usermod -aG docker $USER")
print("\n✓ Docker安装完成\n")

# 重新连接以应用docker组权限
print("重新连接服务器以应用权限...")
ssh.close()
time.sleep(2)
ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect(HOST, username=USERNAME, password=PASSWORD, timeout=30)
print("✓ 已重新连接\n")

# 2. 安装Docker Compose
print("=" * 60)
print("步骤2: 安装Docker Compose")
print("=" * 60)
run(ssh, "sudo apt-get update")
run(ssh, "sudo apt-get install -y docker-compose-plugin")
print("✓ Docker Compose安装完成\n")

# 3. 更新代码
print("=" * 60)
print("步骤3: 更新项目代码")
print("=" * 60)
run(ssh, "cd /opt/xinhuaproject && git pull origin master")
print("✓ 代码已更新\n")

# 4. 配置环境变量
print("=" * 60)
print("步骤4: 配置环境变量")
print("=" * 60)
run(ssh, "cd /opt/xinhuaproject && cp .env.example .env 2>/dev/null || echo 'Config exists'")
run(ssh, "cd /opt/xinhuaproject && grep -q 'DB_PASSWORD=prod' .env || echo 'DB_PASSWORD=prod_db_pass_2024' >> .env")
run(ssh, "cd /opt/xinhuaproject && grep -q 'JWT_SECRET=jwt' .env || echo 'JWT_SECRET=jwt_prod_secret_2024_min_32_chars' >> .env")
print("✓ 环境变量配置完成\n")

# 5. 构建和启动
print("=" * 60)
print("步骤5: 构建Docker镜像（这可能需要5-10分钟）")
print("=" * 60)
run(ssh, "cd /opt/xinhuaproject && docker compose build")
print("\n✓ 镜像构建完成\n")

# 6. 启动服务
print("=" * 60)
print("步骤6: 启动服务")
print("=" * 60)
run(ssh, "cd /opt/xinhuaproject && docker compose down")
run(ssh, "cd /opt/xinhuaproject && docker compose up -d")
print("✓ 服务已启动\n")

# 等待
print("等待服务启动...")
time.sleep(15)

# 7. 检查状态
print("\n" + "=" * 60)
print("  服务状态")
print("=" * 60)
run(ssh, "cd /opt/xinhuaproject && docker compose ps")

print("\n" + "=" * 60)
print("  测试访问")
print("=" * 60)
run(ssh, "curl -s -o /dev/null -w '销售端 HTTP状态: %{http_code}\\n' http://localhost:80")
run(ssh, "curl -s -o /dev/null -w '管理端 HTTP状态: %{http_code}\\n' http://localhost:81")

print("\n" + "=" * 60)
print("  ✅ 部署完成！")
print("=" * 60)
print("\n访问地址：")
print(f"  销售端: http://{HOST}:80")
print(f"  管理端: http://{HOST}:81")

ssh.close()
