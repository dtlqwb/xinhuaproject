import paramiko

ssh = paramiko.SSHClient()
ssh.set_missing_host_key_policy(paramiko.AutoAddPolicy())
ssh.connect('82.156.165.194', username='ubuntu', password='H6~4]+9wFt8pSd')

print("检查JAR文件中的SecurityConfig类:")
stdin, stdout, stderr = ssh.exec_command('cd /opt/xinhuaproject/backend && find target -name "*.jar" -exec jar tf {} \\; 2>/dev/null | grep SecurityConfig')
result = stdout.read().decode('utf-8')
print(result if result else '❌ 没有找到SecurityConfig类')

print("\n检查Docker构建:")
stdin, stdout, stderr = ssh.exec_command('cd /opt/xinhuaproject && sudo docker compose build backend 2>&1 | grep -E "COPY|target|jar" | tail -10')
print(stdout.read().decode('utf-8'))

ssh.close()
