# 🚀 智销助手 - 云服务器部署完整指南

## 📍 服务器信息

```
IP地址: 82.156.165.194
操作系统: Ubuntu Server 24.04 LTS 64位
用户名: ubuntu
密码: H6~4]+9wFt8pSd
GitHub: https://github.com/dtlqwb/xinhuaproject
```

---

## 🎯 快速开始（3分钟）

### 最简单的方式：一键部署

**在您的本地电脑上打开PowerShell或终端：**

```bash
# 第1步：连接服务器
ssh ubuntu@82.156.165.194
# 输入密码: H6~4]+9wFt8pSd

# 第2步：下载并执行部署脚本
curl -O https://raw.githubusercontent.com/dtlqwb/xinhuaproject/main/deploy-to-server.sh
chmod +x deploy-to-server.sh
sudo ./deploy-to-server.sh

# 第3步：等待5-10分钟，看到"🎉 部署完成！"即可
```

**部署完成后访问：**
- 销售端: http://82.156.165.194:80
- 管理端: http://82.156.165.194:81

**默认账号：**
- 销售端: `13800138000` / `123456`
- 管理端: `admin` / `admin123`

---

## 📚 文档导航

本仓库提供了完整的部署文档，请根据需要选择：

### 🎓 新手推荐（按顺序阅读）

1. **[QUICK_DEPLOY_CARD.md](./QUICK_DEPLOY_CARD.md)** ⭐⭐⭐⭐⭐
   - 快速参考卡片
   - 最常用的命令
   - 适合日常运维

2. **[DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md)** ⭐⭐⭐⭐⭐
   - 部署前检查清单
   - 逐步验证步骤
   - 确保不遗漏任何环节

3. **[SERVER_DEPLOY_GUIDE.md](./SERVER_DEPLOY_GUIDE.md)** ⭐⭐⭐⭐
   - 详细部署指南
   - 手动部署步骤
   - 故障排查方法
   - 安全加固建议

### 🔧 进阶文档

4. **[DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md)**
   - 通用部署指南
   - Docker和手动部署
   - 配置说明
   - 维护命令

5. **[PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md)**
   - 项目完善总结
   - 功能清单
   - 技术栈说明

---

## 🛠️ 部署方式对比

### 方式一：自动脚本部署（推荐⭐）

**优点：**
- ✅ 一键完成，无需手动操作
- ✅ 自动安装所有依赖
- ✅ 自动生成安全密码
- ✅ 自动配置防火墙
- ✅ 约5-10分钟完成

**适用场景：**
- 首次部署
- 全新服务器
- 快速上线

**执行命令：**
```bash
ssh ubuntu@82.156.165.194
sudo ./deploy-to-server.sh
```

### 方式二：Docker Compose部署

**优点：**
- ✅ 标准化部署
- ✅ 易于维护
- ✅ 可重复执行

**适用场景：**
- 已有Docker环境
- 需要自定义配置

**执行命令：**
```bash
cd /opt/xinhuaproject
sudo cp .env.example .env
sudo vi .env  # 修改配置
sudo docker compose up -d
```

### 方式三：手动部署

**优点：**
- ✅ 完全控制每个步骤
- ✅ 适合学习和调试

**适用场景：**
- 学习目的
- 特殊定制需求
- 自动脚本失败时

**参考文档：** [SERVER_DEPLOY_GUIDE.md](./SERVER_DEPLOY_GUIDE.md) 中的"手动部署"章节

---

## 📊 部署架构

```
┌─────────────────────────────────────┐
│     云服务器 (82.156.165.194)       │
├─────────────────────────────────────┤
│                                     │
│  ┌──────────┐    ┌──────────────┐  │
│  │ Nginx:80 │───→│ Sales Frontend│  │
│  │          │    │  (Vue 3 H5)   │  │
│  └──────────┘    └──────────────┘  │
│                                     │
│  ┌──────────┐    ┌──────────────┐  │
│  │ Nginx:81 │───→│Admin Frontend│  │
│  │          │    │  (Vue 3 H5)   │  │
│  └──────────┘    └──────────────┘  │
│                                     │
│  ┌──────────────┐                  │
│  │ Spring Boot  │                  │
│  │   :8080      │                  │
│  └──────┬───────┘                  │
│         │                          │
│  ┌──────▼───────┐                  │
│  │   MySQL:3306 │                  │
│  └──────────────┘                  │
│                                     │
└─────────────────────────────────────┘
         ↑
    用户浏览器访问
```

---

## 🔑 重要提示

### ⚠️ 部署前

1. **确认代码已推送到GitHub**
   ```bash
   git push origin main
   ```

2. **测试本地运行正常**
   ```bash
   # 后端
   cd backend && mvn spring-boot:run
   
   # 前端
   cd sales-frontend && npm run dev
   ```

3. **备份重要数据**（如果是重新部署）

### ⚠️ 部署中

1. **保持网络连接稳定**
2. **不要中断脚本执行**
3. **记录生成的密码**

### ⚠️ 部署后

1. **立即修改默认密码**
2. **测试所有功能**
3. **配置HTTPS（推荐）**
4. **设置自动备份**

---

## 🆘 遇到问题？

### 快速诊断

```bash
# 1. 检查服务状态
sudo docker compose ps

# 2. 查看日志
sudo docker compose logs -f

# 3. 检查端口
sudo ss -tlnp | grep -E ':(80|81|8080)'

# 4. 测试API
curl http://localhost:8080/api/sales/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"123456"}'
```

### 常见错误解决

**错误1：无法连接服务器**
```bash
# 检查SSH服务
sudo systemctl status ssh

# 检查防火墙
sudo ufw status
```

**错误2：Docker启动失败**
```bash
# 查看Docker日志
sudo journalctl -u docker.service -f

# 重启Docker
sudo systemctl restart docker
```

**错误3：端口被占用**
```bash
# 查找占用端口的进程
sudo lsof -i :80
sudo lsof -i :8080

# 停止冲突的服务
sudo systemctl stop nginx  # 如果nginx占用了80端口
```

**错误4：内存不足**
```bash
# 查看内存使用
free -h

# 清理Docker空间
sudo docker system prune -a

# 增加Swap
sudo fallocate -l 2G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
```

### 获取帮助

提供以下信息以便快速定位问题：

1. **错误截图或日志**
   ```bash
   sudo docker compose logs > error.log
   ```

2. **系统信息**
   ```bash
   cat /etc/os-release
   docker --version
   free -h
   df -h
   ```

3. **服务状态**
   ```bash
   sudo docker compose ps
   sudo docker compose logs
   ```

---

## 📞 联系支持

如遇到无法解决的问题：

1. 查看完整文档：[SERVER_DEPLOY_GUIDE.md](./SERVER_DEPLOY_GUIDE.md)
2. 检查GitHub Issues
3. 提供详细的错误信息和日志

---

## 🎉 部署成功标志

当您看到以下内容时，表示部署成功：

```
========================================
  🎉 部署完成！
========================================

📍 访问地址：
   销售端H5: http://82.156.165.194:80
   管理端H5: http://82.156.165.194:81
   后端API:  http://82.156.165.194:8080

✅ 4个Docker容器正常运行
✅ 防火墙已配置
✅ 数据库已初始化
✅ 前后端可以正常访问
```

---

## 📈 后续优化建议

部署成功后，可以考虑：

1. **性能优化**
   - 配置CDN加速静态资源
   - 启用Gzip压缩
   - 添加Redis缓存

2. **安全加固**
   - 配置HTTPS证书
   - 设置fail2ban防暴力破解
   - 定期更新系统和依赖

3. **监控告警**
   - 配置服务器监控
   - 设置日志收集
   - 添加健康检查

4. **备份策略**
   - 每日自动备份数据库
   - 定期备份上传文件
   - 测试恢复流程

---

## 📝 版本历史

- v1.0 (2024-01-XX): 初始版本，支持Docker一键部署
- 包含完整的自动化脚本和文档

---

**准备好了吗？现在开始部署吧！** 🚀

```bash
ssh ubuntu@82.156.165.194
sudo ./deploy-to-server.sh
```
