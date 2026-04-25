#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
最终修复 - 移除镜像源、更新代码、重新编译部署
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

def run(cmd, timeout=300, show_output=True):
    print(f"> {cmd[:80]}...")
    stdin, stdout, stderr = ssh.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    if show_output and out:
        print(out[-1500:] if len(out) > 1500 else out)
    if err and exit_code != 0:
        print(f"错误: {err[:500]}")
    return exit_code

# 1. 移除所有Docker镜像源，直接连接Docker Hub
print("=" * 60)
print("1. 移除Docker镜像源配置")
print("=" * 60)
run("sudo rm -f /etc/docker/daemon.json")
run("sudo systemctl daemon-reload")
run("sudo systemctl restart docker")
print("✓ 已移除镜像源\n")

# 2. 更新代码（获取修复后的pom.xml）
print("2. 更新项目代码")
print("=" * 60)
run("cd /opt/xinhuaproject && sudo git reset --hard origin/master")
run("cd /opt/xinhuaproject && sudo chown -R $USER:$USER .")
print("✓ 代码已更新\n")

# 3. 编译后端
print("3. 编译后端（这可能需要5-10分钟）")
print("=" * 60)
run("cd /opt/xinhuaproject/backend && mvn clean package -DskipTests", timeout=600)
run("ls -lh /opt/xinhuaproject/backend/target/*.jar")

# 4. 清除旧镜像
print("\n4. 清除旧Docker镜像")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose down --rmi all")
run("docker system prune -f")

# 5. 构建镜像
print("\n5. 构建Docker镜像（这可能需要15-20分钟）")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose build", timeout=1200)

# 6. 启动服务
print("\n6. 启动服务")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose up -d")

# 等待
print("\n等待服务启动...")
time.sleep(30)

# 7. 检查状态
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
