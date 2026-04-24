# 智销助手 - 服务器快速操作指南

## 🔑 服务器信息

```
IP: 82.156.165.194
用户: ubuntu
密码: H6~4]+9wFt8pSd
项目: https://github.com/dtlqwb/xinhuaproject
```

---

## 🚀 一键部署（3步完成）

### 第1步：连接服务器
```bash
ssh ubuntu@82.156.165.194
# 密码: H6~4]+9wFt8pSd
```

### 第2步：执行部署脚本
```bash
curl -O https://raw.githubusercontent.com/dtlqwb/xinhuaproject/main/deploy-to-server.sh
chmod +x deploy-to-server.sh
sudo ./deploy-to-server.sh
```

### 第3步：等待完成（约5-10分钟）

完成后访问：
- 销售端: http://82.156.165.194:80
- 管理端: http://82.156.165.194:81

---

## 📊 常用命令速查

### 查看服务状态
```bash
cd /opt/xinhuaproject
sudo docker compose ps
```

### 查看日志
```bash
sudo docker compose logs -f           # 所有服务
sudo docker compose logs -f backend   # 仅后端
sudo docker compose logs -f sales-frontend  # 仅销售端
```

### 重启服务
```bash
sudo docker compose restart           # 全部重启
sudo docker compose restart backend   # 重启后端
```

### 更新代码
```bash
cd /opt/xinhuaproject
sudo git pull
sudo docker compose up -d --build
```

### 停止服务
```bash
sudo docker compose down
```

---

## 🔧 故障排查

### 服务无法访问？
```bash
# 检查服务是否运行
sudo docker compose ps

# 检查防火墙
sudo ufw status

# 检查端口
sudo ss -tlnp | grep -E ':(80|81|8080)'
```

### 数据库问题？
```bash
# 进入MySQL
sudo docker exec -it sales-mysql mysql -u root -p

# 查看MySQL日志
sudo docker logs sales-mysql
```

### 内存不足？
```bash
# 查看内存使用
free -h

# 清理Docker空间
sudo docker system prune -a
```

---

## 🛡️ 安全提醒

⚠️ **部署后立即执行：**

1. **记录生成的密码**
   ```bash
   cat /opt/xinhuaproject/.env
   ```

2. **修改默认管理员密码**
   - 通过管理端界面修改
   - 或直接修改数据库

3. **配置HTTPS**（可选但推荐）
   ```bash
   sudo apt-get install certbot python3-certbot-nginx
   sudo certbot --nginx -d your-domain.com
   ```

---

## 📞 紧急联系

遇到问题时提供：
1. 错误截图
2. `sudo docker compose logs` 输出
3. `sudo docker compose ps` 输出

---

**详细文档**: 查看 `SERVER_DEPLOY_GUIDE.md`
