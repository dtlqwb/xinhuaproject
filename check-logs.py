import paramiko

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('82.156.165.194', username='ubuntu', password='H6~4]+9wFt8pSd')

# 检查后端完整日志
print("检查后端日志中的SecurityConfig:")
stdin, stdout, stderr = ssh.exec_command('sudo docker logs sales-backend 2>&1 | grep -E "SecurityConfig|security|filter" | tail -20')
print(stdout.read().decode('utf-8'))

# 检查启动时间
print("\n检查后端启动时间:")
stdin, stdout, stderr = ssh.exec_command('sudo docker logs sales-backend 2>&1 | grep "Started CustomerSystemApplication"')
print(stdout.read().decode('utf-8'))

ssh.close()
