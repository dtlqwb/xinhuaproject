import paramiko

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('82.156.165.194', username='ubuntu', password='H6~4]+9wFt8pSd')

# 检查后端日志中是否有AdminController
print("检查后端日志中的AdminController:")
stdin, stdout, stderr = ssh.exec_command('sudo docker logs sales-backend 2>&1 | grep -i "admincontroller" | tail -5')
print(stdout.read().decode('utf-8'))

# 检查API映射
print("\n检查API映射:")
stdin, stdout, stderr = ssh.exec_command('sudo docker logs sales-backend 2>&1 | grep -i "mapped.*admin" | tail -5')
print(stdout.read().decode('utf-8'))

# 测试API
print("\n测试API:")
stdin, stdout, stderr = ssh.exec_command('curl -s -X POST http://localhost:8080/api/admin/login -H "Content-Type: application/json" -d \'{"username":"admin","password":"admin123"}\'')
result = stdout.read().decode('utf-8')
print(result[:500] if result else '无响应')

ssh.close()
