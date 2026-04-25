#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
检查服务器Docker容器状态和日志
用于诊断"网络连接失败"问题
"""

import paramiko
import sys

# 服务器配置
SERVER_HOST = "82.156.165.194"
SERVER_USER = "ubuntu"
SERVER_PASSWORD = "H6~4]+9wFt8pSd"  # 从之前的记忆中获取

def print_header(text):
    print("\n" + "=" * 60)
    print(text)
    print("=" * 60)

def run_ssh_command(ssh, command, description=""):
    """执行SSH命令并返回结果"""
    if description:
        print(f"\n📋 {description}")
        print(f"命令: {command}")
    
    stdin, stdout, stderr = ssh.exec_command(command)
    exit_code = stdout.channel.recv_exit_status()
    output = stdout.read().decode('utf-8')
    error = stderr.read().decode('utf-8')
    
    if output:
        print(output)
    if error and exit_code != 0:
        print(f"❌ 错误: {error}")
    
    return exit_code, output, error

def main():
    print_header("🔍 服务器Docker容器状态检查")
    print(f"服务器: {SERVER_HOST}")
    print(f"用户: {SERVER_USER}")
    
    try:
        # 连接服务器
        print("\n🔌 正在连接服务器...")
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(SERVER_HOST, username=SERVER_USER, password=SERVER_PASSWORD, timeout=10)
        print("✅ 连接成功!")
        
        # 1. 检查Docker容器状态
        print_header("1. Docker容器状态")
        run_ssh_command(ssh, "cd /opt/xinhuaproject && sudo docker compose ps", "查看所有容器状态")
        
        # 2. 检查后端容器日志(最近50行)
        print_header("2. 后端容器日志(最近50行)")
        run_ssh_command(ssh, "cd /opt/xinhuaproject && sudo docker compose logs --tail=50 backend", "查看后端日志")
        
        # 3. 检查后端容器是否健康
        print_header("3. 后端健康检查")
        run_ssh_command(ssh, 
            "sudo docker exec sales-backend curl -s http://localhost:8080/actuator/health || echo '健康检查端点不可用'",
            "检查后端健康状态")
        
        # 4. 检查管理前端容器日志
        print_header("4. 管理前端容器日志(最近20行)")
        run_ssh_command(ssh, "cd /opt/xinhuaproject && sudo docker compose logs --tail=20 admin-frontend", "查看管理前端日志")
        
        # 5. 检查Docker网络
        print_header("5. Docker网络配置")
        run_ssh_command(ssh, "sudo docker network ls", "列出Docker网络")
        run_ssh_command(ssh, "sudo docker network inspect xinhuaproject_default 2>/dev/null || echo '网络不存在'", "检查项目网络")
        
        # 6. 测试容器间连通性
        print_header("6. 容器间连通性测试")
        run_ssh_command(ssh, 
            "sudo docker exec admin-frontend wget -qO- --timeout=3 http://backend:8080/actuator/health 2>&1 || echo '无法连接到backend:8080'",
            "从admin-frontend测试连接backend")
        
        # 7. 检查端口监听
        print_header("7. 端口监听状态")
        run_ssh_command(ssh, "sudo netstat -tlnp | grep -E ':(80|81|8080|3306)'", "检查端口监听")
        
        # 8. 测试API直接访问
        print_header("8. API直接访问测试")
        run_ssh_command(ssh, 
            "curl -s -o /dev/null -w 'HTTP状态码: %{http_code}\\n' http://localhost:8080/api/auth/login -X POST -H 'Content-Type: application/json' -d '{\"username\":\"admin\",\"password\":\"admin123\"}'",
            "从服务器本地测试API")
        
        ssh.close()
        
        print_header("✅ 检查完成")
        print("\n💡 诊断建议:")
        print("1. 如果backend容器未运行: sudo docker compose up -d backend")
        print("2. 如果backend启动失败: sudo docker compose logs backend")
        print("3. 如果网络连接问题: 检查Docker网络配置")
        print("4. 重启所有服务: cd /opt/xinhuaproject && sudo docker compose restart")
        
        return 0
        
    except paramiko.AuthenticationException:
        print("❌ 认证失败,请检查用户名和密码")
        return 1
    except paramiko.SSHException as e:
        print(f"❌ SSH连接错误: {e}")
        return 1
    except Exception as e:
        print(f"❌ 发生错误: {e}")
        return 1

if __name__ == "__main__":
    sys.exit(main())
