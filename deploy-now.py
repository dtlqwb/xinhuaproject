#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
自动连接服务器并完成部署
"""

import paramiko
import time
import sys

HOST = '82.156.165.194'
USERNAME = 'ubuntu'
PASSWORD = 'H6~4]+9wFt8pSd'

def execute_command(ssh, command, description=""):
    """执行命令并输出结果"""
    if description:
        print(f"\n{description}")
        print("-" * 60)
    
    print(f"执行: {command}")
    stdin, stdout, stderr = ssh.exec_command(command)
    
    # 等待命令执行
    exit_status = stdout.channel.recv_exit_status()
    output = stdout.read().decode('utf-8')
    error = stderr.read().decode('utf-8')
    
    if output:
        print(output)
    if error and exit_status != 0:
        print(f"错误: {error}")
    
    return exit_status, output, error

def main():
    print("=" * 60)
    print("  智销助手 - 自动部署到服务器")
    print("=" * 60)
    
    try:
        # 连接服务器
        print("\n[1/7] 连接到服务器...")
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        ssh.connect(HOST, username=USERNAME, password=PASSWORD, timeout=30)
        print("✓ 连接成功")
        
        # 检查项目是否存在
        print("\n[2/7] 检查项目目录...")
        exit_status, output, _ = execute_command(
            ssh, 
            "test -d /opt/xinhuaproject && echo 'exists' || echo 'not_exists'",
            ""
        )
        
        if 'not_exists' in output:
            print("\n项目不存在，开始克隆...")
            execute_command(
                ssh,
                "cd /opt && sudo git clone https://github.com/dtlqwb/xinhuaproject.git",
                "[3/7] 克隆项目"
            )
        else:
            print("✓ 项目已存在")
            execute_command(
                ssh,
                "cd /opt/xinhuaproject && sudo git pull origin master",
                "[3/7] 更新代码"
            )
        
        # 检查Docker是否安装
        print("\n[3/7] 检查Docker...")
        exit_status, output, _ = execute_command(
            ssh,
            "docker --version 2>/dev/null || echo 'not_installed'",
            ""
        )
        
        if 'not_installed' in output:
            print("\nDocker未安装，开始安装...")
            execute_command(
                ssh,
                "curl -fsSL https://get.docker.com | sudo sh",
                "安装Docker"
            )
            execute_command(
                ssh,
                "sudo usermod -aG docker ubuntu",
                "配置权限"
            )
            print("✓ Docker安装完成，请重新连接以使配置生效")
            # 重新连接
            ssh.close()
            time.sleep(2)
            ssh = paramiko.SSHClient()
            ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            ssh.connect(HOST, username=USERNAME, password=PASSWORD, timeout=30)
        else:
            print(f"✓ Docker已安装: {output.strip()}")
        
        # 检查Docker Compose
        print("\n[4/7] 检查Docker Compose...")
        exit_status, output, _ = execute_command(
            ssh,
            "docker compose version 2>/dev/null || echo 'not_installed'",
            ""
        )
        
        if 'not_installed' in output:
            print("安装Docker Compose插件...")
            execute_command(
                ssh,
                "sudo apt-get update && sudo apt-get install -y docker-compose-plugin",
                ""
            )
        else:
            print(f"✓ Docker Compose已安装: {output.strip()}")
        execute_command(
            ssh,
            "cd /opt/xinhuaproject && sudo cp .env.example .env 2>/dev/null || echo 'Config exists'",
            ""
        )
        
        # 配置环境变量
        print("\n[5/7] 配置环境变量...")
        execute_command(
            ssh,
            "cd /opt/xinhuaproject && grep -q 'DB_PASSWORD=prod' .env || echo 'DB_PASSWORD=prod_db_pass_2024' | sudo tee -a .env",
            ""
        )
        execute_command(
            ssh,
            "cd /opt/xinhuaproject && grep -q 'JWT_SECRET=jwt' .env || echo 'JWT_SECRET=jwt_prod_secret_2024_min_32_chars' | sudo tee -a .env",
            ""
        )
        print("✓ 配置完成")
        
        # 构建和启动Docker
        print("\n[6/7] 构建Docker镜像...")
        execute_command(
            ssh,
            "cd /opt/xinhuaproject && sudo docker compose build",
            ""
        )
        
        print("\n[7/7] 启动服务...")
        execute_command(
            ssh,
            "cd /opt/xinhuaproject && sudo docker compose down && sudo docker compose up -d",
            ""
        )
        
        # 等待服务启动
        print("\n[7/7] 等待服务启动...")
        time.sleep(15)
        
        # 检查服务状态
        print("\n" + "=" * 60)
        print("  服务状态")
        print("=" * 60)
        execute_command(
            ssh,
            "cd /opt/xinhuaproject && sudo docker compose ps",
            ""
        )
        
        # 测试访问
        print("\n" + "=" * 60)
        print("  测试访问")
        print("=" * 60)
        execute_command(
            ssh,
            "curl -s -o /dev/null -w '销售端 HTTP状态: %{http_code}\\n' http://localhost:80",
            ""
        )
        execute_command(
            ssh,
            "curl -s -o /dev/null -w '管理端 HTTP状态: %{http_code}\\n' http://localhost:81",
            ""
        )
        
        print("\n" + "=" * 60)
        print("  ✅ 部署完成！")
        print("=" * 60)
        print("\n访问地址：")
        print(f"  销售端: http://{HOST}:80")
        print(f"  管理端: http://{HOST}:81")
        print()
        
        ssh.close()
        
    except Exception as e:
        print(f"\n❌ 部署失败: {str(e)}")
        sys.exit(1)

if __name__ == '__main__':
    main()
