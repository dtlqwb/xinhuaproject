#!/usr/bin/env python3
import paramiko
import time

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('82.156.165.194', username='ubuntu', password='H6~4]+9wFt8pSd')

print("=" * 60)
print("检查后端容器状态")
print("=" * 60)
stdin, stdout, stderr = ssh.exec_command('cd /opt/xinhuaproject && sudo docker compose ps')
print(stdout.read().decode('utf-8'))

print("\n" + "=" * 60)
print("重新构建并启动后端")  
print("=" * 60)

# 停止后端
print("\n停止后端容器...")
stdin, stdout, stderr = ssh.exec_command('cd /opt/xinhuaproject && sudo docker compose stop backend')
print(stdout.read().decode('utf-8'))

# 重新构建
print("重新构建后端镜像...")
stdin, stdout, stderr = ssh.exec_command('cd /opt/xinhuaproject && sudo docker compose build --no-cache backend')
output = stdout.read().decode('utf-8')
print("构建输出(最后100行):")
lines = output.split('\n')
for line in lines[-100:]:
    print(line)

# 启动
print("\n启动后端容器...")
stdin, stdout, stderr = ssh.exec_command('cd /opt/xinhuaproject && sudo docker compose up -d backend')
print(stdout.read().decode('utf-8'))

# 等待启动
print("\n等待后端启动(20秒)...")
time.sleep(20)

# 查看日志
print("\n" + "=" * 60)
print("后端启动日志(最后50行)")
print("=" * 60)
stdin, stdout, stderr = ssh.exec_command('sudo docker logs sales-backend --tail 50')
print(stdout.read().decode('utf-8'))

# 测试API
print("\n" + "=" * 60)
print("测试管理员登录API")
print("=" * 60)
stdin, stdout, stderr = ssh.exec_command("curl -s -X POST http://localhost:8080/api/admin/login -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"admin123\"}'")
result = stdout.read().decode('utf-8')
print(f"响应: {result[:500]}")

ssh.close()

print("\n" + "=" * 60)
print("✅ 检查完成!")
print("=" * 60)
