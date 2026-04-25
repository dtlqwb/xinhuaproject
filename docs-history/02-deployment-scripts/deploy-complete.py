#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
完整部署 - 安装Maven、编译后端、修复镜像源、构建并启动
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
    print(f"> {cmd[:100]}...")
    stdin, stdout, stderr = ssh.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    if out:
        print(out[-2000:] if len(out) > 2000 else out)
    if err and exit_code != 0:
        print(f"错误: {err[:500]}")
    return exit_code

# 1. 修复Docker镜像源
print("=" * 60)
print("1. 修复Docker镜像源")
print("=" * 60)
run("""
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://registry.docker-cn.com",
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
""")

# 2. 安装Maven
print("\n2. 安装Maven（这可能需要3-5分钟）")
print("=" * 60)
run("sudo apt-get install -y maven")
run("mvn --version")

# 3. 编译后端
print("\n3. 编译后端（这可能需要5-10分钟）")
print("=" * 60)
run("cd /opt/xinhuaproject/backend && mvn clean package -DskipTests", timeout=600)
run("ls -lh /opt/xinhuaproject/backend/target/*.jar")

# 4. 构建镜像
print("\n4. 构建Docker镜像（这可能需要10-15分钟）")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose build", timeout=900)

# 5. 启动服务
print("\n5. 启动服务")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose down")
run("cd /opt/xinhuaproject && docker compose up -d")

# 等待
print("\n等待服务启动...")
time.sleep(30)

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
