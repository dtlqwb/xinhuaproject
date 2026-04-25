# 智销助手 - 部署前检查清单

## ✅ 准备工作

### 1. 本地准备

- [ ] 已将代码推送到GitHub: https://github.com/dtlqwb/xinhuaproject
- [ ] 已测试本地运行正常
- [ ] 已准备好服务器信息

### 2. 服务器信息确认

```
✅ IP地址: 82.156.165.194
✅ 操作系统: Ubuntu Server 24.04 LTS 64位
✅ 用户名: ubuntu
✅ 密码: H6~4]+9wFt8pSd
✅ SSH端口: 22（默认）
```

### 3. 网络检查

在本地执行：
```bash
# 测试SSH连接
ssh ubuntu@82.156.165.194

# 测试GitHub访问
ping github.com
```

---

## 📋 部署步骤清单

### 阶段一：连接服务器

- [ ] 使用SSH成功连接到服务器
- [ ] 能够执行sudo命令
- [ ] 网络连接正常

### 阶段二：执行部署脚本

- [ ] 下载部署脚本
  ```bash
  curl -O https://raw.githubusercontent.com/dtlqwb/xinhuaproject/main/deploy-to-server.sh
  ```

- [ ] 添加执行权限
  ```bash
  chmod +x deploy-to-server.sh
  ```

- [ ] 以sudo运行脚本
  ```bash
  sudo ./deploy-to-server.sh
  ```

- [ ] 等待脚本执行完成（约5-10分钟）

### 阶段三：验证部署

- [ ] 记录生成的密码（数据库密码和JWT密钥）
- [ ] 检查Docker容器状态
  ```bash
  sudo docker compose ps
  ```
  应该看到4个running状态的容器

- [ ] 检查端口监听
  ```bash
  sudo ss -tlnp | grep -E ':(80|81|8080)'
  ```

- [ ] 测试销售端访问
  - 浏览器打开: http://82.156.165.194:80
  - 使用账号登录: 13800138000 / 123456

- [ ] 测试管理端访问
  - 浏览器打开: http://82.156.165.194:81
  - 使用账号登录: admin / admin123

- [ ] 测试API接口
  ```bash
  curl http://82.156.165.194:8080/api/sales/login \
    -H "Content-Type: application/json" \
    -d '{"phone":"13800138000","password":"123456"}'
  ```

### 阶段四：安全加固

- [ ] 记录.env文件中的密码
  ```bash
  cat /opt/xinhuaproject/.env
  ```

- [ ] 修改默认管理员密码
  - 通过管理端界面修改
  - 或直接修改数据库

- [ ] 配置防火墙规则（脚本已自动配置）
  ```bash
  sudo ufw status
  ```
  应该只开放22, 80, 81, 8080端口

- [ ] （可选）配置HTTPS证书
- [ ] （可选）设置自动备份

---

## 🔍 功能测试清单

### 销售端测试

- [ ] 能够正常登录
- [ ] 语音录入功能正常（需要Chrome浏览器）
- [ ] 文字输入模式正常
- [ ] 可以上传附件（图片）
- [ ] 客户列表显示正常
- [ ] 今日统计数据显示

### 管理端测试

- [ ] 能够正常登录
- [ ] Dashboard统计数据正常显示
- [ ] 可以看到客户总数
- [ ] 可以看到今日新增
- [ ] 底部导航栏切换正常

### 后端API测试

- [ ] 登录接口返回正确
- [ ] 可以创建客户
- [ ] 可以查询客户列表
- [ ] 文件上传功能正常

---

## ⚠️ 常见问题预防

### 网络问题

如果下载速度慢：
```bash
# 使用国内镜像
# Docker镜像加速
sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
  "registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"]
}
EOF
sudo systemctl restart docker
```

### 端口冲突

检查端口是否被占用：
```bash
sudo ss -tlnp | grep -E ':(80|81|8080|3306)'
```

如果有冲突，修改 `docker-compose.yml` 中的端口映射。

### 内存不足

检查内存：
```bash
free -h
```

如果内存小于2GB，建议增加Swap或升级配置。

---

## 📝 部署后记录

部署完成后，请填写以下信息：

```
部署时间: _______________
部署人: _______________

生成的数据库密码: _______________
生成的JWT密钥: _______________

访问地址:
- 销售端: http://82.156.165.194:80
- 管理端: http://82.156.165.194:81

测试结果:
- [ ] 销售端正常
- [ ] 管理端正常
- [ ] API正常

备注:
_______________
_______________
```

---

## 🆘 紧急回滚方案

如果部署失败，可以：

### 方案1：重新部署
```bash
cd /opt/xinhuaproject
sudo docker compose down
sudo git pull
sudo docker compose up -d --build
```

### 方案2：手动修复
```bash
# 查看错误日志
sudo docker compose logs

# 重启特定服务
sudo docker compose restart backend
```

### 方案3：完全重置
```bash
cd /opt/xinhuaproject
sudo docker compose down -v  # 删除数据卷
sudo rm -rf .env
sudo ./deploy-to-server.sh
```

---

## ✅ 完成确认

部署成功后，确认以下事项：

- [ ] 所有服务正常运行
- [ ] 可以通过外网访问
- [ ] 核心功能测试通过
- [ ] 密码已记录并妥善保管
- [ ] 防火墙配置正确
- [ ] 已设置监控和告警（可选）

---

**祝您部署顺利！** 🎉

如有问题，请查看：
- [SERVER_DEPLOY_GUIDE.md](./SERVER_DEPLOY_GUIDE.md) - 详细部署指南
- [QUICK_DEPLOY_CARD.md](./QUICK_DEPLOY_CARD.md) - 快速参考
