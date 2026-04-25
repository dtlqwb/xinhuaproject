#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
直接在服务器上执行命令修复问题
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
    return exit_code, out, err

# 1. 使用sed精确修复pom.xml
print("=" * 60)
print("1. 使用sed修复pom.xml")
print("=" * 60)

# 先查看当前内容
run("cd /opt/xinhuaproject/backend && grep -B 2 -A 2 'mysql-connector-java' pom.xml")

# 使用sed在mysql-connector-java后面添加版本号
run("""cd /opt/xinhuaproject/backend && sed -i '/<artifactId>mysql-connector-java<\/artifactId>/a\            <version>8.0.33</version>' pom.xml""")

# 验证
print("\n验证修复结果:")
run("cd /opt/xinhuaproject/backend && grep -B 2 -A 4 'mysql-connector-java' pom.xml")

# 2. 编译后端
print("\n" + "=" * 60)
print("2. 编译后端")
print("=" * 60)
exit_code, out, err = run("cd /opt/xinhuaproject/backend && mvn clean package -DskipTests", timeout=600)

if exit_code == 0:
    print("\n✓ 后端编译成功！")
    run("ls -lh /opt/xinhuaproject/backend/target/*.jar")
else:
    print("\n✗ 后端编译失败，查看详细错误:")
    run("cd /opt/xinhuaproject/backend && tail -100 target/maven-status/maven-compiler-plugin/compile/default-compile/inputFiles.lst 2>/dev/null || echo '检查pom.xml'")
    run("cd /opt/xinhuaproject/backend && cat pom.xml | grep -A 10 'mysql-connector-java'")

ssh.close()
print("\n完成！")
