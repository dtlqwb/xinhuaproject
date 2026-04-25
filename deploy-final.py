#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
配置Docker镜像源并部署
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
        print(f"错误: {err[:300]}")
    return exit_code

# 1. 配置Docker国内镜像源
print("=" * 60)
print("1. 配置Docker镜像源（使用腾讯云镜像）")
print("=" * 60)
run("""
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": [
    "https://mirror.ccs.tencentyun.com",
    "https://registry.docker-cn.com",
    "https://docker.mirrors.ustc.edu.cn"
  ]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker
""")
print("✓ Docker镜像源配置完成\n")

# 2. 修复Git权限
print("2. 修复Git权限")
print("=" * 60)
run("sudo chown -R $USER:$USER /opt/xinhuaproject/.git")
run("git config --global --add safe.directory /opt/xinhuaproject")
run("cd /opt/xinhuaproject && git pull origin master")
print("✓ 代码已更新\n")

# 3. 构建镜像
print("3. 构建Docker镜像（这可能需要10-15分钟）")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose build", timeout=900)

# 4. 启动服务
print("\n4. 启动服务")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose down")
run("cd /opt/xinhuaproject && docker compose up -d")

# 等待
print("\n等待服务启动...")
time.sleep(25)

# 5. 检查状态
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
