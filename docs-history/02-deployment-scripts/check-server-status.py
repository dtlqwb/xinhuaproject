#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查服务器上的服务状态和日志
"""

import paramiko

HOST = '82.156.165.194'
USERNAME = 'ubuntu'
PASSWORD = 'H6~4]+9wFt8pSd'

print("连接服务器...")
ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect(HOST, username=USERNAME, password=PASSWORD, timeout=30)
print("✓ 已连接\n")

def run(cmd, timeout=30):
    print(f"执行: {cmd}")
    stdin, stdout, stderr = ssh.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    if out:
        print(out)
    if err and exit_code != 0:
        print(f"错误: {err}")
    print("-" * 60)
    return exit_code

# 1. 检查容器状态
print("=" * 60)
print("1. Docker容器状态")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose ps")

# 2. 检查后端日志（最近50行）
print("\n2. 后端服务日志（最近50行）")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose logs --tail=50 backend")

# 3. 测试后端API
print("\n3. 测试后端API")
print("=" * 60)
run("curl -s http://localhost:8080/api/sales/today-count?salesId=1 | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/api/sales/today-count?salesId=1")

# 4. 测试客户列表API
print("\n4. 测试客户列表API")
print("=" * 60)
run("curl -s http://localhost:8080/api/customer/list?salesId=1 | python3 -m json.tool 2>/dev/null || curl -s http://localhost:8080/api/customer/list?salesId=1")

# 5. 检查Nginx配置
print("\n5. Nginx配置")
print("=" * 60)
run("cat /opt/xinhuaproject/sales-frontend/nginx.conf")

# 6. 检查数据库连接
print("\n6. 检查MySQL容器日志")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose logs --tail=20 mysql")

ssh.close()
print("\n✅ 检查完成！")
