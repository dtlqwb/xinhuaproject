#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
完整修复 - 在服务器上重新编译后端
"""

import paramiko
import time

SERVER_HOST = "82.156.165.194"
SERVER_USER = "ubuntu"
SERVER_PASSWORD = "H6~4]+9wFt8pSd"

def run_cmd(ssh, cmd, desc="", timeout=300):
    if desc:
        print(f"\n📋 {desc}")
        print(f"命令: {cmd[:100]}...")
    stdin, stdout, stderr = ssh.exec_command(cmd, timeout=timeout)
    exit_code = stdout.channel.recv_exit_status()
    output = stdout.read().decode('utf-8')
    error = stderr.read().decode('utf-8')
    if output:
        lines = output.split('\n')
        for line in lines[-20:]:
            if line.strip():
                print(line)
    if error and exit_code != 0:
        print(f"❌ 错误: {error[:300]}")
    return exit_code

def main():
    print("=" * 70)
    print("🔧 完整修复 - 重新编译后端代码")
    print("=" * 70)
    
    try:
        print("\n🔌 连接服务器...")
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(SERVER_HOST, username=SERVER_USER, password=SERVER_PASSWORD, timeout=10)
        print("✅ 连接成功!")
        
        # 1. 拉取最新代码
        print("\n" + "=" * 70)
        print("步骤 1/5: 拉取最新代码")
        print("=" * 70)
        run_cmd(ssh, "cd /opt/xinhuaproject && git reset --hard HEAD && git pull origin master", "拉取代码")
        
        # 2. 检查Maven是否可用
        print("\n" + "=" * 70)
        print("步骤 2/5: 检查构建环境")
        print("=" * 70)
        run_cmd(ssh, "mvn -version", "检查Maven版本")
        
        # 3. 编译后端
        print("\n" + "=" * 70)
        print("步骤 3/5: 编译后端代码(这可能需要2-3分钟)")
        print("=" * 70)
        run_cmd(ssh, "cd /opt/xinhuaproject/backend && mvn clean package -DskipTests", "Maven编译", timeout=300)
        
        # 4. 检查编译结果
        print("\n" + "=" * 70)
        print("步骤 4/5: 检查编译结果")
        print("=" * 70)
        run_cmd(ssh, "cd /opt/xinhuaproject/backend && ls -lh target/*.jar", "查看JAR文件")
        run_cmd(ssh, "cd /opt/xinhuaproject/backend && jar tf target/*.jar | grep SecurityConfig", "检查SecurityConfig类")
        
        # 5. 重新构建并启动Docker
        print("\n" + "=" * 70)
        print("步骤 5/5: 重新构建并启动Docker容器")
        print("=" * 70)
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose down", "停止所有容器")
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose build --no-cache backend", "重新构建后端镜像")
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose up -d", "启动所有服务")
        
        # 等待启动
        print("\n⏳ 等待服务启动(25秒)...")
        time.sleep(25)
        
        # 测试API
        print("\n" + "=" * 70)
        print("测试API")
        print("=" * 70)
        
        print("\n测试管理员登录:")
        run_cmd(ssh, 
            "curl -s -w '\\nHTTP:%{http_code}' -X POST http://localhost:8080/api/admin/login -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"admin123\"}'",
            "管理员登录API")
        
        print("\n查看后端日志:")
        run_cmd(ssh, "sudo docker logs sales-backend --tail 15", "后端日志")
        
        ssh.close()
        
        print("\n" + "=" * 70)
        print("✅ 修复完成!")
        print("=" * 70)
        print("\n🎉 管理后台登录问题应该已经修复!")
        print("\n现在可以访问:")
        print("  📍 管理后台: http://82.156.165.194:81")
        print("  👤 账号: admin")
        print("  🔑 密码: admin123")
        
        return 0
        
    except Exception as e:
        print(f"\n❌ 错误: {e}")
        import traceback
        traceback.print_exc()
        return 1

if __name__ == "__main__":
    import sys
    sys.exit(main())
