# 智销助手 - 销售客户资料收集系统

## 项目简介
一个极简设计的销售客户资料收集系统，支持语音、文字输入和附件上传，后端集成AI智能体接口进行数据分析和日报生成。

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- JWT认证

### 前端（销售端）
- Vue 3 + TypeScript
- Vite 5
- Vant UI 4
- Pinia状态管理
- Web Speech API语音识别

## 快速开始

### 1. 数据库初始化

在MySQL中执行以下SQL脚本：
```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

或者手动执行 `backend/src/main/resources/schema.sql` 文件中的SQL语句。

### 2. 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端将在 http://localhost:8080 启动

### 3. 启动销售端H5

```bash
cd sales-frontend
npm install
npm run dev
```

销售端将在 http://localhost:3000 启动

### 4. 访问应用

浏览器打开 http://localhost:3000

**测试账号：**
- 手机号：13800138000
- 密码：123456

## 核心功能

### 销售端
1. **语音录入**：按住底部横条说话，自动识别并解析客户信息
2. **文字录入**：点击键盘图标切换到文字输入模式
3. **附件上传**：点击"+"按钮上传图片或文档
4. **客户列表**：实时显示今日录入的客户
5. **客户详情**：点击客户卡片查看详情

### 交互设计
- 极简聊天式界面
- 按住说话，松开即提交
- AI自动解析结构化信息
- 新客户卡片动画展示

## 项目结构

```
xinhuaproject/
├── backend/                    # Java后端
│   ├── src/main/java/com/sales/customer/
│   │   ├── controller/        # 控制器
│   │   ├── service/           # 业务逻辑
│   │   ├── mapper/            # 数据访问
│   │   ├── entity/            # 实体类
│   │   ├── dto/               # 数据传输对象
│   │   ├── config/            # 配置类
│   │   └── util/              # 工具类
│   └── src/main/resources/
│       ├── application.yml    # 配置文件
│       └── schema.sql         # 数据库脚本
├── sales-frontend/            # 销售端H5
│   ├── src/
│   │   ├── views/             # 页面
│   │   ├── components/        # 组件
│   │   ├── stores/            # 状态管理
│   │   ├── api/               # API接口
│   │   └── utils/             # 工具函数
│   └── package.json
└── README.md
```

## API接口

### 销售人员
- POST `/api/sales/login` - 登录
- GET `/api/sales/today-count` - 获取今日客户数量

### 客户管理
- POST `/api/customer/add` - 添加客户（支持附件）
- GET `/api/customer/list` - 查询客户列表
- GET `/api/customer/{id}` - 查询客户详情
- PUT `/api/customer/update` - 更新客户
- DELETE `/api/customer/{id}` - 删除客户

### 日报管理
- POST `/api/report/generate` - 生成日报
- GET `/api/report/list` - 查询日报列表
- GET `/api/report/{id}` - 查询日报详情

### 营销方案
- POST `/api/plan/generate` - 生成营销方案
- GET `/api/plan/list` - 查询营销方案列表
- PUT `/api/plan/{id}/execute` - 标记为已执行

## AI智能体接口

项目中预留了AI服务接口（`AiService`），当前使用简单的正则表达式解析作为默认实现。后期可以对接真实的AI服务：

- OpenAI API
- 文心一言
- 通义千问
- 其他大语言模型

只需修改 `AiServiceImpl` 中的实现即可。

## 注意事项

1. **语音识别**：Web Speech API在Chrome浏览器中支持最好
2. **文件上传**：默认存储在 `./uploads/` 目录
3. **跨域配置**：后端已配置CORS，支持前端跨域访问
4. **数据库连接**：修改 `application.yml` 中的数据库配置

## 后续开发计划

- [ ] 管理端H5开发
- [ ] 对接真实AI服务
- [ ] 数据导出功能
- [ ] 消息推送
- [ ] 微信小程序版本

## 许可证

MIT License
