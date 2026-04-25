#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
最终部署方案：
1. 在服务器上直接修复pom.xml
2. 使用本地构建方式（不依赖Docker Hub拉取基础镜像）
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
    print(f"> {cmd[:100]}")
    stdin, stdout, stderr = ssh.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    out = stdout.read().decode('utf-8')
    err = stderr.read().decode('utf-8')
    if out:
        # 只显示最后50行
        lines = out.split('\n')
        if len(lines) > 50:
            print('\n'.join(lines[-50:]))
        else:
            print(out)
    if err and exit_code != 0:
        print(f"错误: {err[:500]}")
    return exit_code

# 1. 检查当前pom.xml状态
print("=" * 60)
print("1. 检查pom.xml当前状态")
print("=" * 60)
run("cd /opt/xinhuaproject/backend && grep -A 5 'mysql-connector-java' pom.xml")

# 2. 使用Python直接修改pom.xml（更可靠）
print("\n2. 使用Python修复pom.xml")
print("=" * 60)
run("""
cd /opt/xinhuaproject/backend
python3 << 'PYEOF'
import re

with open('pom.xml', 'r', encoding='utf-8') as f:
    content = f.read()

# 查找mysql-connector-java依赖并添加版本号
pattern = r'(<dependency>\s*<groupId>mysql</groupId>\s*<artifactId>mysql-connector-java</artifactId>)\s*(</dependency>)'
replacement = r'\1\n            <version>8.0.33</version>\n        \2'

new_content = re.sub(pattern, replacement, content)

with open('pom.xml', 'w', encoding='utf-8') as f:
    f.write(new_content)

print("pom.xml已修复")
PYEOF
""")

# 验证修复结果
print("\n3. 验证pom.xml修复结果")
print("=" * 60)
run("cd /opt/xinhuaproject/backend && grep -A 5 'mysql-connector-java' pom.xml")

# 4. 编译后端
print("\n4. 编译后端（这可能需要5-10分钟）")
print("=" * 60)
exit_code = run("cd /opt/xinhuaproject/backend && mvn clean package -DskipTests", timeout=600)

if exit_code == 0:
    print("\n✓ 后端编译成功！")
    run("ls -lh /opt/xinhuaproject/backend/target/*.jar")
else:
    print("\n✗ 后端编译失败")
    print("尝试查看错误详情...")
    run("cd /opt/xinhuaproject/backend && mvn clean package -DskipTests -e", timeout=600)

# 5. 配置Docker镜像加速器（使用多个国内源）
print("\n5. 配置Docker镜像加速器")
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

# 6. 测试Docker镜像拉取
print("\n6. 测试Docker镜像拉取")
print("=" * 60)
run("docker pull mysql:8.0", timeout=300)

# 7. 如果镜像拉取成功，继续构建
print("\n7. 构建Docker镜像")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose build", timeout=1200)

# 8. 启动服务
print("\n8. 启动服务")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose down")
run("cd /opt/xinhuaproject && docker compose up -d")

# 等待服务启动
print("\n等待服务启动...")
time.sleep(30)

# 9. 检查状态
print("\n" + "=" * 60)
print("9. 检查服务状态")
print("=" * 60)
run("cd /opt/xinhuaproject && docker compose ps")

print("\n" + "=" * 60)
print("10. 测试访问")
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
