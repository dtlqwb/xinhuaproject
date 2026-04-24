#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
智销助手 - 自动化部署脚本
使用SSH密钥或密码自动部署到服务器
"""

import paramiko
import sys
import time

# 服务器配置
HOST = '82.156.165.194'
USERNAME = 'ubuntu'
PASSWORD = 'H6~4]+9wFt8pSd'
KEY_FILE = r'D:\360MoveData\Users\wangbo\Desktop\客户信息收集\dtlqyd.pem'
DEPLOY_DIR = '/opt/xinhuaproject'

def deploy_with_password():
    """使用密码认证部署"""
    print("=" * 60)
    print("  智销助手 - 自动化部署")
    print("=" * 60)
    print()
    
    try:
        # 创建SSH客户端
        ssh = paramiko.SSHClient()
        ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
        
        print("[1/5] 连接到服务器...")
        ssh.connect(HOST, username=USERNAME, password=PASSWORD, timeout=30)
        print("✓ 连接成功")
        print()
        
        # 检查项目目录是否存在
        stdin, stdout, stderr = ssh.exec_command(f"test -d {DEPLOY_DIR} && echo 'exists' || echo 'not_exists'")
        result = stdout.read().decode('utf-8').strip()
        
        if result == 'not_exists':
            print("[1.5/5] 首次部署 - 克隆项目...")
            # 尝试多次克隆
            max_retries = 3
            for attempt in range(max_retries):
                print(f"  尝试 {attempt + 1}/{max_retries}...")
                stdin, stdout, stderr = ssh.exec_command(
                    f"sudo mkdir -p /opt && cd /opt && sudo git clone https://github.com/dtlqwb/xinhuaproject.git",
                    timeout=180
                )
                exit_status = stdout.channel.recv_exit_status()
                error = stderr.read().decode('utf-8')
                
                if exit_status == 0:
                    print("✓ 项目克隆成功")
                    break
                else:
                    print(f"  失败: {error[:200]}")
                    if attempt < max_retries - 1:
                        print("  等待5秒后重试...")
                        time.sleep(5)
            else:
                print(f"✗ 项目克隆失败，已重试{max_retries}次")
                print("\n建议：")
                print("1. 检查服务器网络连接")
                print("2. 手动在服务器上执行: sudo git clone https://github.com/dtlqwb/xinhuaproject.git")
                return
            print()
        
        # 执行部署命令
        commands = [
            (f"cd {DEPLOY_DIR} && sudo git pull", "拉取最新代码"),
            (f"cd {DEPLOY_DIR} && [ ! -f .env ] && sudo cp .env.example .env || echo 'Config exists'", "检查配置文件"),
            (f"cd {DEPLOY_DIR} && sudo docker compose build", "构建Docker镜像"),
            (f"cd {DEPLOY_DIR} && sudo docker compose down", "停止旧服务"),
            (f"cd {DEPLOY_DIR} && sudo docker compose up -d", "启动新服务"),
        ]
        
        for i, (cmd, desc) in enumerate(commands, 2):
            print(f"[{i}/5] {desc}...")
            stdin, stdout, stderr = ssh.exec_command(cmd, timeout=300)
            
            # 等待命令执行
            exit_status = stdout.channel.recv_exit_status()
            output = stdout.read().decode('utf-8')
            error = stderr.read().decode('utf-8')
            
            if exit_status == 0:
                print(f"✓ {desc}成功")
                if output:
                    print(output[:500])  # 只显示前500字符
            else:
                print(f"✗ {desc}失败")
                if error:
                    print(f"错误: {error[:500]}")
            print()
        
        print("[5/5] 检查服务状态...")
        stdin, stdout, stderr = ssh.exec_command(
            f"cd {DEPLOY_DIR} && sudo docker compose ps",
            timeout=30
        )
        output = stdout.read().decode('utf-8')
        print(output)
        print()
        
        print("=" * 60)
        print("  ✅ 部署完成！")
        print("=" * 60)
        print()
        print("访问地址：")
        print(f"  销售端: http://{HOST}:80")
        print(f"  管理端: http://{HOST}:81")
        print()
        
        ssh.close()
        
    except Exception as e:
        print(f"\n❌ 部署失败: {str(e)}")
        sys.exit(1)

if __name__ == '__main__':
    deploy_with_password()
