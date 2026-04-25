#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复docker-compose.yml中的数据库连接URL
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

def run(cmd, timeout=30):
    print(f"执行: {cmd[:100]}")
    stdin, stdout, stderr = ssh.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    if out:
        lines = out.split('\n')
        if len(lines) > 30:
            print('\n'.join(lines[-30:]))
        else:
            print(out)
    if err and exit_code != 0:
        print(f"错误: {err[:500]}")
    return exit_code

# 1. 修改docker-compose.yml
print("=" * 60)
print("1. 修改docker-compose.yml")
print("=" * 60)

run("""
cd /opt/xinhuaproject
sed -i 's|DB_URL: jdbc:mysql://mysql:3306/sales_customer_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai|DB_URL: jdbc:mysql://mysql:3306/sales_customer_db?useUnicode=true\\&characterEncoding=utf8\\&useSSL=false\\&serverTimezone=Asia/Shanghai\\&allowPublicKeyRetrieval=true|' docker-compose.yml
echo "docker-compose.yml已更新"
""")

# 验证修改
print("\n验证修改:")
run("cd /opt/xinhuaproject && grep -A 2 'DB_URL' docker-compose.yml")

# 2. 重启服务
print("\n2. 重启服务")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose down")
time.sleep(5)
run("cd /opt/xinhuaproject && docker compose up -d")

# 等待
print("\n等待服务启动（30秒）...")
time.sleep(30)

# 3. 检查日志
print("\n3. 后端日志")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose logs --tail=30 backend | grep -E '(Started|Error|Exception|Connected)' || docker compose logs --tail=30 backend")

# 4. 测试API
print("\n4. 测试API")
print("=" * 60)
run("curl -s http://localhost:8080/api/sales/today-count?salesId=1")
print()
run("curl -s http://localhost:8080/api/customer/list?salesId=1")

print("\n" + "=" * 60)
print("✅ 修复完成！")
print("=" * 60)

ssh.close()
