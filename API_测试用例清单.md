# API 测试用例清单

**项目名称:** 智销助手 - 销售客户资料收集系统  
**生成日期:** 2026-04-26  
**测试环境:** http://82.156.165.194:8080  
**数据库:** sales_customer_db  

---

## 目录

1. [认证接口](#1-认证接口)
2. [客户管理接口](#2-客户管理接口)
3. [销售管理接口](#3-销售管理接口)
4. [AI日报接口](#4-ai日报接口)
5. [营销方案接口](#5-营销方案接口)

---

## 1. 认证接口

### 1.1 销售人员登录

**接口:** `POST /api/sales/login`  
**认证:** 不需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常登录 | `{"phone":"13800138000","password":"123456"}` | HTTP 200, 返回token和用户信息 | P0 |
| 手机号错误 | `{"phone":"99999999999","password":"123456"}` | HTTP 200, code=500, 错误信息 | P0 |
| 密码错误 | `{"phone":"13800138000","password":"wrong"}` | HTTP 200, code=500, 错误信息 | P0 |
| 空手机号 | `{"phone":"","password":"123456"}` | HTTP 200, code=500, 错误信息 | P1 |
| 空密码 | `{"phone":"13800138000","password":""}` | HTTP 200, code=500, 错误信息 | P1 |
| 空请求体 | `{}` | HTTP 200, code=500, 错误信息 | P2 |

**curl 示例:**
```bash
curl -X POST http://82.156.165.194:8080/api/sales/login \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","password":"123456"}'
```

---

### 1.2 管理员登录

**接口:** `POST /api/admin/login`  
**认证:** 不需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常登录 | `{"username":"admin","password":"admin123"}` | HTTP 200, 返回token | P0 |
| 用户名错误 | `{"username":"wrong","password":"admin123"}` | HTTP 200, code=500, 错误信息 | P0 |
| 密码错误 | `{"username":"admin","password":"wrong"}` | HTTP 200, code=500, 错误信息 | P0 |
| 空用户名 | `{"username":"","password":"admin123"}` | HTTP 200, code=500, 错误信息 | P1 |
| 空密码 | `{"username":"admin","password":""}` | HTTP 200, code=500, 错误信息 | P1 |

**curl 示例:**
```bash
curl -X POST http://82.156.165.194:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## 2. 客户管理接口

### 2.1 添加客户

**接口:** `POST /api/customer/add`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常添加(纯文本) | content="张三,某某公司,经理,13800138000", salesId=1 | HTTP 200, 返回客户信息 | P0 |
| 正常添加(带附件) | content="...", salesId=1, file=[附件] | HTTP 200, 返回客户信息和附件URL | P1 |
| salesId为空 | content="...", salesId= | HTTP 400, 参数错误 | P1 |
| content为空 | content="", salesId=1 | HTTP 400, 参数错误 | P1 |
| 超大content | content="10000字符..." | HTTP 400/500, 参数过长 | P2 |

**curl 示例:**
```bash
curl -X POST http://82.156.165.194:8080/api/customer/add \
  -H "Content-Type: multipart/form-data" \
  -F "content=张三,某某公司,经理,13800138000" \
  -F "salesId=1"
```

---

### 2.2 查询客户列表

**接口:** `GET /api/customer/list?salesId={id}`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常查询(有数据) | salesId=1 | HTTP 200, 返回客户列表 | P0 |
| 正常查询(无数据) | salesId=999 | HTTP 200, 返回空数组[] | P0 |
| salesId为空 | salesId= | HTTP 400, 参数错误 | P1 |
| salesId为负数 | salesId=-1 | HTTP 200, 返回空数组 | P2 |

**curl 示例:**
```bash
curl -X GET "http://82.156.165.194:8080/api/customer/list?salesId=1"
```

---

### 2.3 查询客户详情

**接口:** `GET /api/customer/{id}`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常查询 | id=1 | HTTP 200, 返回客户详情 | P0 |
| ID不存在 | id=999 | HTTP 200, data=null 或错误 | P0 |
| ID为0 | id=0 | HTTP 400/500, 参数错误 | P1 |
| ID为负数 | id=-1 | HTTP 400/500, 参数错误 | P2 |

**curl 示例:**
```bash
curl -X GET http://82.156.165.194:8080/api/customer/1
```

---

### 2.4 更新客户信息

**接口:** `PUT /api/customer/update`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常更新 | 完整客户对象,id=1 | HTTP 200, 返回success=true | P0 |
| ID不存在 | 完整客户对象,id=999 | HTTP 200, 返回success=false | P0 |
| 空对象 | {} | HTTP 400, 参数错误 | P1 |
| 缺少必填字段 | 缺少id或name | HTTP 400, 参数错误 | P1 |

**curl 示例:**
```bash
curl -X PUT http://82.156.165.194:8080/api/customer/update \
  -H "Content-Type: application/json" \
  -d '{"id":1,"name":"张三(已更新)","company":"某某公司","phone":"13800138000"}'
```

---

### 2.5 删除客户

**接口:** `DELETE /api/customer/{id}`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常删除 | id=1 | HTTP 200, 返回success=true | P0 |
| ID不存在 | id=999 | HTTP 200, 返回success=false | P0 |
| ID为0 | id=0 | HTTP 400/500, 参数错误 | P1 |

**curl 示例:**
```bash
curl -X DELETE http://82.156.165.194:8080/api/customer/1
```

---

## 3. 销售管理接口

### 3.1 获取今日客户数量

**接口:** `GET /api/sales/today-count?salesId={id}`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 有今日客户 | salesId=1 | HTTP 200, data.count>0 | P0 |
| 无今日客户 | salesId=1(未添加) | HTTP 200, data.count=0 | P0 |
| salesId不存在 | salesId=999 | HTTP 200, data.count=0 | P1 |
| salesId为空 | salesId= | HTTP 400, 参数错误 | P1 |

**curl 示例:**
```bash
curl -X GET "http://82.156.165.194:8080/api/sales/today-count?salesId=1"
```

---

## 4. AI日报接口

### 4.1 生成AI日报

**接口:** `POST /api/daily-report/generate`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常生成 | salesId=1, date="2026-04-26" | HTTP 200, 返回日报内容 | P0 |
| 无今日客户 | salesId=1(无客户) | HTTP 200, 返回空日报或提示 | P1 |
| salesId为空 | salesId=, date="..." | HTTP 400, 参数错误 | P1 |

**curl 示例:**
```bash
curl -X POST http://82.156.165.194:8080/api/daily-report/generate \
  -H "Content-Type: application/json" \
  -d '{"salesId":1,"date":"2026-04-26"}'
```

---

### 4.2 查询日报列表

**接口:** `GET /api/daily-report/list?salesId={id}`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常查询 | salesId=1 | HTTP 200, 返回日报列表 | P0 |
| 无日报 | salesId=999 | HTTP 200, 返回空数组 | P0 |
| salesId为空 | salesId= | HTTP 400, 参数错误 | P1 |

**curl 示例:**
```bash
curl -X GET "http://82.156.165.194:8080/api/daily-report/list?salesId=1"
```

---

## 5. 营销方案接口

### 5.1 生成营销方案

**接口:** `POST /api/marketing-plan/generate`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常生成 | customerId=1 | HTTP 200, 返回方案内容 | P0 |
| 客户不存在 | customerId=999 | HTTP 200/500, 错误信息 | P0 |
| customerId为空 | customerId= | HTTP 400, 参数错误 | P1 |

**curl 示例:**
```bash
curl -X POST http://82.156.165.194:8080/api/marketing-plan/generate \
  -H "Content-Type: application/json" \
  -d '{"customerId":1}'
```

---

### 5.2 查询营销方案列表

**接口:** `GET /api/marketing-plan/list`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常查询 | 无 | HTTP 200, 返回方案列表 | P0 |
| 无方案 | (数据库无数据) | HTTP 200, 返回空数组 | P0 |

**curl 示例:**
```bash
curl -X GET http://82.156.165.194:8080/api/marketing-plan/list
```

---

### 5.3 查询指定客户的营销方案

**接口:** `GET /api/marketing-plan/customer/{customerId}`  
**认证:** 需要

| 测试场景 | 请求参数 | 预期结果 | 优先级 |
|---------|---------|---------|--------|
| 正常查询 | customerId=1 | HTTP 200, 返回方案列表 | P0 |
| 客户无方案 | customerId=1 | HTTP 200, 返回空数组 | P0 |
| 客户不存在 | customerId=999 | HTTP 200, 返回空数组 | P1 |

**curl 示例:**
```bash
curl -X GET http://82.156.165.194:8080/api/marketing-plan/customer/1
```

---

## 测试执行记录

### 自动化测试 (JUnit 5)

| 测试类 | 测试方法数 | 状态 | 执行时间 |
|--------|-----------|------|---------|
| AdminServiceTest | 5 | ✅ 待执行 | - |
| SalesPersonServiceTest | 4 | ✅ 待执行 | - |
| AdminControllerTest | 3 | ✅ 待执行 | - |
| SalesPersonControllerTest | 4 | ✅ 待执行 | - |

### 手动测试 (Postman)

| 接口类别 | 测试用例数 | 通过数 | 失败数 | 通过率 |
|---------|-----------|--------|--------|--------|
| 认证接口 | 10 | - | - | - |
| 客户管理 | 12 | - | - | - |
| 销售管理 | 4 | - | - | - |
| AI日报 | 5 | - | - | - |
| 营销方案 | 6 | - | - | - |
| **总计** | **37** | **-** | **-** | **-** |

---

## 测试数据准备

### 销售人员测试账号
```sql
INSERT INTO sales_person (name, phone, department, password) 
VALUES ('张三', '13800138000', '销售部', '123456');
```

### 管理员测试账号
- 用户名: admin
- 密码: admin123

### 客户测试数据
```sql
INSERT INTO customer (name, company, position, phone, content, sales_id)
VALUES 
  ('李四', 'ABC公司', '经理', '13900139000', '意向客户,对AI产品感兴趣', 1),
  ('王五', 'XYZ公司', '总监', '13700137000', '需要跟进,约了下周拜访', 1);
```

---

## 注意事项

1. **测试环境隔离**: 使用独立的测试数据库,避免影响生产数据
2. **数据清理**: 每次测试后清理测试数据
3. **并发测试**: 模拟多用户同时访问,检查并发安全
4. **性能测试**: 关注接口响应时间,确保<500ms
5. **安全测试**: 检查SQL注入、XSS等安全漏洞

---

## 测试执行人员

- 开发人员: 执行自动化单元测试
- QA人员: 执行手动API测试
- 产品经理: 执行功能验收测试

**最后更新:** 2026-04-26
