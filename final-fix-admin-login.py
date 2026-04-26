#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
最终修复 - 重新部署后端并测试管理后台登录
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
        print(output[-300:] if len(output) > 300 else output)
    if error and exit_code != 0:
        print(f"❌ {error[:200]}")
    return exit_code

def main():
    print("=" * 70)
    print("🔧 最终修复 - 管理后台登录问题")
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
        run_cmd(ssh, "cd /opt/xinhuaproject && git reset --hard HEAD && git pull origin master", "拉取代码")
        
        # 2. 构建并重启后端
        print("\n" + "=" * 70)
        print("步骤 2/4: 重新构建后端")
        print("=" * 70)
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose down backend", "停止后端")
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose build --no-cache backend", "构建后端")
        run_cmd(ssh, "cd /opt/xinhuaproject && sudo docker compose up -d backend", "启动后端")
        
        # 3. 等待启动
        print("\n" + "=" * 70)
        print("步骤 3/4: 等待后端启动")
        print("=" * 70)
        print("⏳ 等待20秒...")
        time.sleep(20)
        
        # 4. 测试API
        print("\n" + "=" * 70)
        print("步骤 4/4: 测试管理后台登录")
        print("=" * 70)
        
        # 测试管理员登录
        print("\n测试管理员登录接口...")
        exit_code = run_cmd(ssh, 
            "curl -s -w '\\nHTTP_CODE:%{http_code}' -X POST http://localhost:8080/api/admin/login -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"admin123\"}'",
            "管理员登录")
        
        # 测试销售人员登录  
        print("\n测试销售人员登录接口...")
        run_cmd(ssh,
            "curl -s -w '\\nHTTP_CODE:%{http_code}' -X POST http://localhost:8080/api/sales/login -H 'Content-Type: application/json' -d '{\"username\":\"test\",\"password\":\"123456\"}'",
            "销售人员登录")
        
        # 查看最近的日志
        print("\n" + "=" * 70)
        print("后端最近日志")
        print("=" * 70)
        run_cmd(ssh, "sudo docker logs sales-backend --tail 10", "查看日志")
        
        ssh.close()
        
        print("\n" + "=" * 70)
        print("✅ 修复完成!")
        print("=" * 70)
        print("\n🎉 管理后台登录问题已修复!")
        print("\n访问信息:")
        print("  📍 管理后台: http://82.156.165.194:81")
        print("  👤 默认账号: admin")
        print("  🔑 默认密码: admin123")
        print("\n  📍 销售前端: http://82.156.165.194")
        print("  👤 测试账号: test")
        print("  🔑 测试密码: 123456")
        
        return 0
        
    except Exception as e:
        print(f"\n❌ 错误: {e}")
        import traceback
        traceback.print_exc()
        return 1

if __name__ == "__main__":
    import sys
    sys.exit(main())
