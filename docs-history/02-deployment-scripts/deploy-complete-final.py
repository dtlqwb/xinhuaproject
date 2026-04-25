#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
完整部署脚本 - 使用最新代码并完成所有部署步骤
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
    print(f"执行: {cmd[:100]}")
    stdin, stdout, stderr = ssh.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    if out:
        lines = out.split('\n')
        # 只显示最后30行
        if len(lines) > 30:
            print('\n'.join(lines[-30:]))
        else:
            print(out)
    if err and exit_code != 0:
        print(f"错误: {err[:500]}")
    return exit_code

# 1. 更新代码
print("=" * 60)
print("1. 拉取最新代码")
print("=" * 60)
run("cd /opt/xinhuaproject && git fetch origin", timeout=60)
run("cd /opt/xinhuaproject && git reset --hard origin/master", timeout=60)
run("cd /opt/xinhuaproject && sudo chown -R $USER:$USER .", timeout=30)

# 2. 验证pom.xml
print("\n2. 验证pom.xml")
print("=" * 60)
run("cd /opt/xinhuaproject/backend && grep -A 5 'mysql-connector-java' pom.xml")
run("cd /opt/xinhuaproject/backend && grep -A 3 'spring-boot-starter-security' pom.xml")

# 3. 编译后端
print("\n3. 编译后端（这可能需要5-10分钟）")
print("=" * 60)
exit_code = run("cd /opt/xinhuaproject/backend && mvn clean package -DskipTests", timeout=600)

if exit_code == 0:
    print("\n✓ 后端编译成功！")
    run("ls -lh /opt/xinhuaproject/backend/target/*.jar")
else:
    print("\n✗ 后端编译失败，停止部署")
    ssh.close()
    exit(1)

# 4. 配置Docker镜像加速器
print("\n4. 配置Docker镜像加速器")
print("=" * 60)
run("""
sudo mkdir -p /etc/docker
cat > /tmp/daemon.json << 'EOF'
{
  "registry-mirrors": [
    "https://docker.m.daocloud.io",
    "https://huecker.io",
    "https://dockerhub.timeweb.cloud",
    "https://noohub.ru"
  ]
}
EOF
sudo cp /tmp/daemon.json /etc/docker/daemon.json
sudo systemctl daemon-reload
sudo systemctl restart docker
echo "Docker已重启"
""")

# 5. 尝试拉取基础镜像
print("\n5. 拉取Docker基础镜像")
print("=" * 60)
run("docker pull mysql:8.0", timeout=300)
run("docker pull node:18-alpine", timeout=300)
run("docker pull openjdk:8-jre-slim", timeout=300)
run("docker pull nginx:alpine", timeout=300)

# 6. 构建应用镜像
print("\n6. 构建Docker镜像（这可能需要15-20分钟）")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose build", timeout=1200)

# 7. 启动服务
print("\n7. 启动服务")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose down", timeout=60)
run("cd /opt/xinhuaproject && docker compose up -d", timeout=120)

# 等待服务启动
print("\n等待服务启动...")
time.sleep(30)

# 8. 检查状态
print("\n" + "=" * 60)
print("8. 检查服务状态")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose ps")

print("\n" + "=" * 60)
print("9. 测试访问")
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
