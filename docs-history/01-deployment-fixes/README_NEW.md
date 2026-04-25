# 智销助手 - 销售客户资料收集系统

## 📖 项目简介

智销助手是一个智能化的销售客户资料收集和管理系统，支持语音、拍照、文字多种输入方式，通过AI智能体自动解析客户信息，生成日报和营销方案。

### 核心功能

- **销售端H5**：极简设计，按住说话即可录入客户信息
- **管理端H5**：查看客户明细、日报统计、生成营销方案
- **AI智能解析**：自动从语音/文字中提取客户关键信息
- **智能日报**：自动生成销售人员工作日报
- **营销方案**：为未成单客户智能生成跟进方案

## ✨ 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- JWT认证

### 前端
- Vue 3 + TypeScript
- Vite 5
- Vant 4 UI组件库
- Pinia状态管理

### 部署
- Docker & Docker Compose
- Nginx

## 🚀 快速开始

### 本地开发

#### 1. 启动后端
```bash
cd backend
mvn spring-boot:run
```

#### 2. 启动销售端
```bash
cd sales-frontend
npm install
npm run dev
# 访问 http://localhost:3001
```

#### 3. 启动管理端
```bash
cd admin-frontend
npm install
npm run dev
# 访问 http://localhost:3002
```

### Docker一键部署

```bash
# 创建配置文件
cp .env.example .env
vi .env  # 修改密码和密钥

# 一键部署
chmod +x deploy.sh
./deploy.sh

# 或使用docker compose
docker compose up -d
```

访问地址：
- 销售端: http://服务器IP:80
- 管理端: http://服务器IP:81

默认账号：
- 销售端: 13800138000 / 123456
- 管理端: admin / admin123

## 📁 项目结构

```
xinhuaproject/
├── backend/              # Spring Boot后端
├── sales-frontend/       # 销售端H5
├── admin-frontend/       # 管理端H5
├── docker-compose.yml    # Docker编排
├── deploy.sh             # 快速部署脚本
└── DEPLOYMENT_GUIDE.md   # 完整部署文档
```

## 📚 文档

- [DEPLOYMENT_GUIDE.md](./DEPLOYMENT_GUIDE.md) - 完整部署指南
- [PROJECT_SUMMARY.md](./PROJECT_SUMMARY.md) - 项目完善总结
- [BACKEND_QUICK_START.md](./BACKEND_QUICK_START.md) - 后端启动指南
- [MAVEN_INSTALL_GUIDE.md](./MAVEN_INSTALL_GUIDE.md) - Maven安装指南

## 🔧 配置说明

生产环境请修改 `.env` 文件：

```env
DB_PASSWORD=your_secure_password
JWT_SECRET=your_production_jwt_secret
```

## 🎯 功能清单

### 销售端
- ✅ 语音录入客户信息
- ✅ 文字输入模式
- ✅ 图片附件上传
- ✅ 客户列表查看
- ✅ 今日统计

### 管理端
- ✅ 管理员登录
- ✅ Dashboard数据统计
- ⏳ 客户管理（框架已建）
- ⏳ 日报管理（框架已建）
- ⏳ 营销方案（框架已建）

### 后端API
- ✅ 用户认证（JWT）
- ✅ 客户CRUD
- ✅ 附件上传
- ✅ 日报生成
- ✅ 营销方案生成
- ✅ AI解析服务

## 🛡️ 安全提示

部署到生产环境前务必：
1. 修改数据库密码
2. 修改JWT密钥
3. 配置HTTPS
4. 设置防火墙规则

## 📞 技术支持

如有问题，请查看部署文档或提交Issue。

---

**当前状态：✅ 可以部署到云服务器！**
