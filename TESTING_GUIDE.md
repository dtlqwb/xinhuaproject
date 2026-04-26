# 测试体系使用指南

**项目名称:** 智销助手 - 销售客户资料收集系统  
**生成日期:** 2026-04-26  

---

## 📋 目录

1. [测试体系概览](#测试体系概览)
2. [后端单元测试](#后端单元测试)
3. [API接口测试](#api接口测试)
4. [前端功能验收](#前端功能验收)
5. [快速开始](#快速开始)

---

## 测试体系概览

本项目包含完整的测试体系,涵盖:

### ✅ 自动化测试
- **JUnit 5 单元测试**: Service层业务逻辑测试 (9个测试用例)
- **MockMvc 集成测试**: Controller层API测试 (7个测试用例)

### ✅ 手动测试
- **API测试用例清单**: 37个API测试场景详细说明
- **Postman集合**: 可直接导入的API测试集合
- **功能验收Checklist**: 前后端完整的功能验收清单

---

## 后端单元测试

### 测试文件结构

```
backend/src/test/java/com/sales/customer/
├── BaseTest.java                          # 单元测试基础类
├── BaseIntegrationTest.java               # 集成测试基础类
├── service/
│   ├── AdminServiceTest.java              # 管理员服务测试 (5个用例)
│   └── SalesPersonServiceTest.java        # 销售人员服务测试 (4个用例)
└── controller/
    ├── AdminControllerTest.java           # 管理员控制器测试 (3个用例)
    └── SalesPersonControllerTest.java     # 销售人员控制器测试 (4个用例)
```

### 运行测试

#### 方式1: Maven命令
```bash
cd backend
mvn test
```

#### 方式2: 运行特定测试类
```bash
# 运行AdminService测试
mvn test -Dtest=AdminServiceTest

# 运行AdminController测试
mvn test -Dtest=AdminControllerTest
```

#### 方式3: IDE运行
- IntelliJ IDEA: 右键测试类 → Run 'AdminServiceTest'
- Eclipse: 右键测试类 → Run As → JUnit Test

### 测试覆盖范围

| 模块 | 测试类 | 测试方法数 | 覆盖率目标 |
|------|--------|-----------|-----------|
| AdminService | AdminServiceTest | 5 | 80%+ |
| SalesPersonService | SalesPersonServiceTest | 4 | 80%+ |
| AdminController | AdminControllerTest | 3 | 80%+ |
| SalesPersonController | SalesPersonControllerTest | 4 | 80%+ |
| **总计** | **4个测试类** | **16个测试方法** | **80%+** |

### 测试报告

测试完成后会生成HTML报告:
```
backend/target/site/jacoco/index.html
```

---

## API接口测试

### 1. API测试用例清单

查看完整文档: [API_测试用例清单.md](./API_测试用例清单.md)

包含:
- 37个详细测试场景
- 每个场景的请求参数和预期结果
- curl命令示例
- 优先级标注 (P0/P1/P2)

### 2. Postman集合

**文件:** `Postman_测试集合.json`

#### 导入步骤:
1. 打开Postman
2. 点击左上角 "Import"
3. 选择 `Postman_测试集合.json` 文件
4. 导入成功后在左侧Collections中查看

#### 使用步骤:
1. 设置环境变量:
   - baseUrl: `http://82.156.165.194:8080`
   
2. 按顺序执行:
   - 先执行登录接口(自动保存token)
   - 再执行其他需要认证的接口

3. 查看测试结果:
   - 每个请求的Tests标签页有自动断言
   - 绿色表示通过,红色表示失败

#### 集合结构:
```
智销助手 API 测试集合
├── 1. 认证接口 (4个)
│   ├── 销售人员登录
│   ├── 销售人员登录 - 错误密码
│   ├── 管理员登录
│   └── 管理员登录 - 错误用户名
├── 2. 客户管理 (5个)
│   ├── 添加客户
│   ├── 查询客户列表
│   ├── 查询客户详情
│   ├── 更新客户信息
│   └── 删除客户
├── 3. 销售管理 (1个)
│   └── 获取今日客户数量
├── 4. AI日报 (2个)
│   ├── 生成AI日报
│   └── 查询日报列表
└── 5. 营销方案 (3个)
    ├── 生成营销方案
    ├── 查询营销方案列表
    └── 查询指定客户的营销方案
```

---

## 前端功能验收

### 验收文档

查看完整文档: [功能验收checklist.md](./功能验收checklist.md)

### 测试内容

#### 销售前端 (H5) - 47个测试项
- 登录页面 (6项)
- 语音录入横条 (8项)
- 客户列表 (6项)
- 客户详情 (6项)
- AI日报 (6项)
- 营销方案 (4项)
- 个人中心 (3项)
- 通用功能 (7项)

#### 管理后台 (PC) - 42个测试项
- 登录页面 (5项)
- Dashboard (4项)
- 客户管理 (8项)
- 销售人员管理 (6项)
- 日报管理 (5项)
- 营销方案管理 (4项)
- 数据统计 (4项)
- 系统设置 (3项)
- 通用功能 (6项)

#### 性能测试 - 7个测试项
- 页面加载性能 (4项)
- 并发测试 (3项)

#### 兼容性测试
- 浏览器兼容 (5种)
- 移动端兼容 (3种设备)

#### 安全测试 - 5个测试项

**总计: 101个手动测试项**

### 验收流程

1. **打印checklist**: 建议打印纸质版方便勾选
2. **逐项测试**: 按照清单顺序逐一测试
3. **记录问题**: 发现问题填写到"问题汇总"表格
4. **签字确认**: 测试完成后相关人员签字

---

## 快速开始

### 环境准备

#### 必需软件
- JDK 8+
- Maven 3.6+
- Postman (可选,用于API测试)
- 现代浏览器 (Chrome/Firefox/Safari)

#### 启动后端服务
```bash
cd backend
mvn spring-boot:run
```

或者使用Docker:
```bash
docker compose up -d backend
```

### 执行测试

#### 第1步: 运行单元测试
```bash
cd backend
mvn clean test
```

查看测试结果:
- 控制台输出测试统计
- HTML报告: `target/site/jacoco/index.html`

#### 第2步: 导入Postman集合
1. 打开Postman
2. Import → 选择 `Postman_测试集合.json`
3. 设置baseUrl变量
4. 依次执行各个请求

#### 第3步: 执行前端验收
1. 打开浏览器访问:
   - 销售前端: http://82.156.165.194
   - 管理后台: http://82.156.165.194:81
2. 按照 `功能验收checklist.md` 逐项测试
3. 记录测试结果和问题

---

## 测试数据准备

### 数据库初始化

确保数据库中有测试数据:

```sql
-- 插入测试销售人员
INSERT INTO sales_person (name, phone, department, password) 
VALUES ('张三', '13800138000', '销售部', '123456');

-- 插入测试客户
INSERT INTO customer (name, company, position, phone, content, sales_id)
VALUES 
  ('李四', 'ABC公司', '经理', '13900139000', '意向客户', 1),
  ('王五', 'XYZ公司', '总监', '13700137000', '需要跟进', 1);
```

### 测试账号

| 角色 | 账号 | 密码 | 说明 |
|------|------|------|------|
| 销售人员 | 13800138000 | 123456 | 测试用销售账号 |
| 管理员 | admin | admin123 | 管理后台账号 |

---

## 常见问题

### Q1: 单元测试失败怎么办?

**A:** 检查以下几点:
1. 确保Maven依赖正确安装: `mvn clean install`
2. 检查测试代码是否有编译错误
3. 查看具体失败的测试用例和错误信息
4. 确认Mock对象配置正确

### Q2: Postman请求返回401?

**A:** 需要先执行登录接口获取token:
1. 执行"销售人员登录"或"管理员登录"
2. Tests脚本会自动保存token到环境变量
3. 后续请求会自动携带token

### Q3: 前端验收时发现bug如何记录?

**A:** 在checklist的"问题汇总"表格中记录:
- 问题描述
- 严重程度 (P0/P1/P2)
- 发现页面
- 截图(如有)
- 当前状态

### Q4: 如何增加新的测试用例?

**A:** 
1. **单元测试**: 在对应的Test类中添加@Test方法
2. **API测试**: 在Postman集合中添加新请求
3. **功能验收**: 在checklist.md中添加新行

---

## 持续集成

### GitHub Actions配置

项目已配置CI/CD,推送代码后自动:
1. 运行单元测试
2. 构建Docker镜像
3. 部署到服务器

配置文件: `.github/workflows/deploy.yml`

### 本地开发建议

每次提交前:
```bash
# 1. 运行测试
mvn test

# 2. 检查代码格式
mvn checkstyle:check

# 3. 提交代码
git add .
git commit -m "feat: xxx"
git push
```

---

## 测试指标

### 目标指标

| 指标 | 目标值 | 当前值 |
|------|--------|--------|
| 单元测试覆盖率 | ≥80% | 待测量 |
| API测试通过率 | 100% | 待测试 |
| 功能验收通过率 | ≥95% | 待验收 |
| P0级Bug数 | 0 | 待统计 |

### 质量门禁

代码合并前必须满足:
- ✅ 所有单元测试通过
- ✅ 测试覆盖率≥80%
- ✅ 无P0级Bug
- ✅ Code Review通过

---

## 相关文档

- [API测试用例清单](./API_测试用例清单.md)
- [功能验收checklist](./功能验收checklist.md)
- [Postman测试集合](./Postman_测试集合.json)
- [项目梳理报告](./PROJECT_REVIEW.md)

---

## 联系与支持

如有测试相关问题,请联系开发团队。

**最后更新:** 2026-04-26
