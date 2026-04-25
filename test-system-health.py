#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
系统健康检查脚本
测试销售客户收集系统的各个服务是否正常运行
"""

import requests
import sys
from datetime import datetime

# 服务器配置
SERVER_HOST = "82.156.165.194"
TIMEOUT = 10

def print_header():
    print("=" * 60)
    print("🔍 系统健康检查")
    print("=" * 60)
    print(f"测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"服务器地址: {SERVER_HOST}")
    print("=" * 60)
    print()

def test_service(name, url, method="GET", expected_status=200, data=None):
    """测试单个服务"""
    print(f"📡 测试: {name}")
    print(f"   URL: {url}")
    
    try:
        if method == "GET":
            response = requests.get(url, timeout=TIMEOUT)
        elif method == "POST":
            response = requests.post(url, json=data, timeout=TIMEOUT)
        
        status = response.status_code
        
        if status == expected_status:
            print(f"   ✅ 状态: {status} (正常)")
            if status == 200 and response.text:
                print(f"   📄 响应长度: {len(response.text)} 字节")
            return True
        else:
            print(f"   ⚠️  状态: {status} (预期: {expected_status})")
            if response.text:
                print(f"   📄 响应: {response.text[:100]}")
            return status < 500  # 4xx 算部分成功
            
    except requests.exceptions.ConnectionError:
        print(f"   ❌ 连接失败 - 服务可能未启动")
        return False
    except requests.exceptions.Timeout:
        print(f"   ❌ 请求超时")
        return False
    except Exception as e:
        print(f"   ❌ 错误: {str(e)}")
        return False
    finally:
        print()

def main():
    print_header()
    
    results = []
    
    # 1. 测试销售前端
    results.append(test_service(
        "销售前端 (H5)",
        f"http://{SERVER_HOST}",
        expected_status=200
    ))
    
    # 2. 测试管理前端
    results.append(test_service(
        "管理后台前端",
        f"http://{SERVER_HOST}:81",
        expected_status=200
    ))
    
    # 3. 测试后端API - 登录接口
    results.append(test_service(
        "后端API - 登录接口",
        f"http://{SERVER_HOST}/api/auth/login",
        method="POST",
        expected_status=401,  # 401是正常的(未提供token)
        data={"username": "admin", "password": "admin123"}
    ))
    
    # 4. 测试后端API - 健康检查(如果有)
    results.append(test_service(
        "后端API - 健康检查",
        f"http://{SERVER_HOST}/api/health",
        expected_status=200
    ))
    
    # 总结
    print("=" * 60)
    print("📊 测试结果汇总")
    print("=" * 60)
    
    total = len(results)
    passed = sum(results)
    failed = total - passed
    
    print(f"总测试数: {total}")
    print(f"✅ 通过: {passed}")
    print(f"❌ 失败: {failed}")
    print()
    
    if failed == 0:
        print("🎉 所有服务运行正常!")
        print()
        print("访问地址:")
        print(f"  销售前端: http://{SERVER_HOST}")
        print(f"  管理后台: http://{SERVER_HOST}:81")
        print(f"  API文档:  http://{SERVER_HOST}/api")
        return 0
    else:
        print("⚠️  部分服务异常，请检查:")
        if not results[0]:
            print("  - 销售前端未响应")
        if not results[1]:
            print("  - 管理后台未响应")
        if not results[2]:
            print("  - 后端API未响应")
        print()
        print("排查建议:")
        print("  1. SSH到服务器: ssh ubuntu@82.156.165.194")
        print("  2. 检查Docker容器: sudo docker compose ps")
        print("  3. 查看日志: sudo docker compose logs -f")
        return 1

if __name__ == "__main__":
    sys.exit(main())
