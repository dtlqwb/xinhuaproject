#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
修复MySQL公钥检索问题
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

# 1. 修改application.yml，添加allowPublicKeyRetrieval
print("=" * 60)
print("1. 修改数据库连接配置")
print("=" * 60)

run("""
cd /opt/xinhuaproject/backend/src/main/resources
cat > application.yml << 'EOF'
server:
  port: 8080

spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ${DB_URL:jdbc:mysql://mysql:3306/sales_customer_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true}
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root123}
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: deleted
      logic-delete-value: 1
      logic-not-delete-value: 0

# JWT配置
jwt:
  secret: ${JWT_SECRET:salesCustomerSystemSecretKey2024Production}
  expiration: 86400000

# 文件上传路径
file:
  upload-path: ${FILE_UPLOAD_PATH:/data/uploads/}
EOF
echo "配置文件已更新"
""")

# 2. 重新编译
print("\n2. 重新编译后端")
print("=" * 60)
run("cd /opt/xinhuaproject/backend && mvn clean package -DskipTests", timeout=600)

# 3. 重建镜像
print("\n3. 重建Docker镜像")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose build backend", timeout=300)

# 4. 重启服务
print("\n4. 重启服务")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose down")
time.sleep(5)
run("cd /opt/xinhuaproject && docker compose up -d")

# 等待
print("\n等待服务启动（30秒）...")
time.sleep(30)

# 5. 检查日志
print("\n5. 后端日志")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose logs --tail=20 backend")

# 6. 测试API
print("\n6. 测试API")
print("=" * 60)
result1 = run("curl -s http://localhost:8080/api/sales/today-count?salesId=1")
print()
result2 = run("curl -s http://localhost:8080/api/customer/list?salesId=1")

print("\n" + "=" * 60)
print("✅ 修复完成！请刷新浏览器测试")
print("=" * 60)

ssh.close()
