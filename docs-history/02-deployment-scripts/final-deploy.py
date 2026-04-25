#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
最终部署脚本 - 直接执行部署步骤
"""

import paramiko
import time

HOST = '82.156.165.194'
USERNAME = 'ubuntu'
PASSWORD = 'H6~4]+9wFt8pSd'

print("连接服务器...")
ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect(HOST, username=USERNAME, password=PASSWORD, timeout=30)
print("✓ 已连接\n")

def run(cmd, timeout=300):
    print(f"> {cmd[:80]}...")
    stdin, stdout, stderr = ssh.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    if out:
        print(out)
    if err and exit_code != 0:
        print(f"错误: {err[:500]}")
    return exit_code

# 1. 修复Git权限
print("=" * 60)
print("1. 修复Git权限")
print("=" * 60)
run("git config --global --add safe.directory /opt/xinhuaproject")

# 2. 更新代码
print("\n2. 更新代码")
print("=" * 60)
run("cd /opt/xinhuaproject && git pull origin master")

# 3. 配置环境变量
print("\n3. 配置环境变量")
print("=" * 60)
run("cd /opt/xinhuaproject && cp .env.example .env 2>/dev/null || echo 'exists'")
run("cd /opt/xinhuaproject && grep -q 'DB_PASSWORD=prod' .env || echo 'DB_PASSWORD=prod_db_pass_2024' >> .env")
run("cd /opt/xinhuaproject && grep -q 'JWT_SECRET=jwt' .env || echo 'JWT_SECRET=jwt_prod_secret_2024_min_32_chars' >> .env")

# 4. 构建镜像
print("\n4. 构建Docker镜像（这可能需要5-10分钟）")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose build", timeout=600)

# 5. 启动服务
print("\n5. 启动服务")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose down")
run("cd /opt/xinhuaproject && docker compose up -d")

# 等待
print("\n等待服务启动...")
time.sleep(20)

# 6. 检查状态
print("\n" + "=" * 60)
print("服务状态")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose ps")

print("\n" + "=" * 60)
print("测试访问")
print("=" * 60)
run("curl -s -o /dev/null -w '销售端 HTTP状态: %{http_code}\\n' http://localhost:80")
run("curl -s -o /dev/null -w '管理端 HTTP状态: %{http_code}\\n' http://localhost:81")

print("\n" + "=" * 60)
print("✅ 部署完成！")
print("=" * 60)
print(f"\n访问地址：")
print(f"  销售端: http://{HOST}:80")
print(f"  管理端: http://{HOST}:81")

ssh.close()
