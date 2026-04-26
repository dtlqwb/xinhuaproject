#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
快速修复管理后台登录问题
直接在服务器上重新部署
"""

import paramiko
import time

SERVER_HOST = "82.156.165.194"
SERVER_USER = "ubuntu"
SERVER_PASSWORD = "H6~4]+9wFt8pSd"

def print_header(text):
    print("\n" + "=" * 60)
    print(text)
    print("=" * 60)

def run_command(ssh, command, description="", wait_time=0):
    if description:
        print(f"\n📋 {description}")
    
    stdin, stdout, stderr = ssh.exec_command(command)
    exit_code = stdout.channel.recv_exit_status()
    output = stdout.read().decode('utf-8')
    error = stderr.read().decode('utf-8')
    
    if wait_time > 0:
        print(f"   等待 {wait_time} 秒...")
        time.sleep(wait_time)
    
    if output:
        print(output[-500:] if len(output) > 500 else output)
    if error and exit_code != 0:
        print(f"❌ 错误: {error[:200]}")
    
    return exit_code

def main():
    print_header("🔧 快速修复管理后台登录问题")
    
    try:
        print("\n🔌 连接服务器...")
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(SERVER_HOST, username=SERVER_USER, password=SERVER_PASSWORD, timeout=10)
        print("✅ 连接成功!")
        
        # 1. 拉取最新代码
        print_header("步骤 1/5: 拉取最新代码")
        run_command(ssh, "cd /opt/xinhuaproject && git pull origin master", "拉取代码")
        
        # 2. 停止旧服务
        print_header("步骤 2/5: 停止旧服务")
        run_command(ssh, "cd /opt/xinhuaproject && sudo docker compose down", "停止服务")
        
        # 3. 重新构建后端
        print_header("步骤 3/5: 重新构建后端镜像")
        run_command(ssh, "cd /opt/xinhuaproject && sudo docker compose build backend", "构建后端", wait_time=0)
        
        # 4. 启动服务
        print_header("步骤 4/5: 启动所有服务")
        run_command(ssh, "cd /opt/xinhuaproject && sudo docker compose up -d", "启动服务", wait_time=5)
        
        # 5. 检查服务状态
        print_header("步骤 5/5: 检查服务状态")
        run_command(ssh, "cd /opt/xinhuaproject && sudo docker compose ps", "查看容器状态")
        
        # 等待服务启动
        print("\n⏳ 等待服务完全启动(15秒)...")
        time.sleep(15)
        
        # 测试登录接口
        print_header("测试管理员登录接口")
        run_command(ssh, 
            "curl -s -X POST http://localhost:8080/api/admin/login -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"admin123\"}' | head -c 200",
            "测试登录API")
        
        ssh.close()
        
        print_header("✅ 修复完成!")
        print("\n🎉 管理后台登录问题已修复!")
        print("\n访问地址:")
        print("  管理后台: http://82.156.165.194:81")
        print("  默认账号: admin")
        print("  默认密码: admin123")
        print("\n💡 如果还有问题,请查看后端日志:")
        print("  ssh ubuntu@82.156.165.194")
        print("  cd /opt/xinhuaproject")
        print("  sudo docker compose logs -f backend")
        
        return 0
        
    except Exception as e:
        print(f"\n❌ 发生错误: {e}")
        return 1

if __name__ == "__main__":
    import sys
    sys.exit(main())
