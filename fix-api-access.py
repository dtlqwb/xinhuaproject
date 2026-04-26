#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
快速修复 - 重新编译部署后端
"""

import paramiko
import time

SERVER_HOST = "82.156.165.194"
SERVER_USER = "ubuntu"
SERVER_PASSWORD = "H6~4]+9wFt8pSd"

def run_cmd(ssh, cmd, desc=""):
    if desc:
        print(f"\n📋 {desc}")
    stdin, stdout, stderr = ssh.exec_command(cmd)
    exit_code = stdout.channel.recv_exit_status()
    output = stdout.read().decode('utf-8')
    error = stderr.read().decode('utf-8')
    if output:
        lines = output.split('\n')
        # 只显示最后10行
        for line in lines[-10:]:
            if line.strip():
                print(line)
    if error and exit_code != 0:
        print(f"❌ {error[:200]}")
    return exit_code

def main():
    print("=" * 70)
    print("🔧 修复前端API访问被拒绝问题")
    print("=" * 70)
    
    try:
        print("\n🔌 连接服务器...")
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(SERVER_HOST, username=SERVER_USER, password=SERVER_PASSWORD, timeout=10)
        print("✅ 连接成功!")
        
        # 1. 拉取代码
        print("\n" + "=" * 70)
        print("步骤 1/4: 拉取最新代码")
        print("=" * 70)
        run_cmd(ssh, "cd /opt/xinhuaproject && git pull origin master", "拉取代码")
        
        # 2. 编译后端
        print("\n" + "=" * 70)
        print("步骤 2/4: 编译后端")
        print("=" * 70)
        run_cmd(ssh, "cd /opt/xinhuaproject/backend && mvn clean package -DskipTests", "Maven编译")
        
        # 3. 重新构建并启动
        print("\n" + "=" * 70)
        print("步骤 3/4: 重新构建并启动")
        print("=" * 70)
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose down backend", "停止后端")
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose build --no-cache backend", "构建后端")
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose up -d backend", "启动后端")
        
        # 4. 等待并测试
        print("\n" + "=" * 70)
        print("步骤 4/4: 测试API访问")
        print("=" * 70)
        print("⏳ 等待15秒...")
        time.sleep(15)
        
        # 测试登录
        print("\n测试销售人员登录:")
        run_cmd(ssh, 
            "curl -s -X POST http://localhost:8080/api/sales/login -H 'Content-Type: application/json' -d '{\"phone\":\"13800138000\",\"password\":\"123456\"}'",
            "登录API")
        
        # 测试获取客户列表
        print("\n测试获取客户列表(需要salesId=1):")
        run_cmd(ssh,
            "curl -s http://localhost:8080/api/customer/list?salesId=1",
            "客户列表API")
        
        # 测试今日数量
        print("\n测试今日客户数量:")
        run_cmd(ssh,
            "curl -s http://localhost:8080/api/sales/today-count?salesId=1",
            "今日数量API")
        
        ssh.close()
        
        print("\n" + "=" * 70)
        print("✅ 修复完成!")
        print("=" * 70)
        print("\n🎉 前端API访问问题已修复!")
        print("\n请刷新浏览器页面重新测试:")
        print("  销售前端: http://82.156.165.194")
        print("  管理后台: http://82.156.165.194:81")
        
        return 0
        
    except Exception as e:
        print(f"\n❌ 错误: {e}")
        import traceback
        traceback.print_exc()
        return 1

if __name__ == "__main__":
    import sys
    sys.exit(main())
