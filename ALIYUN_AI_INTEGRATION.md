# 阿里百炼AI集成指南

**集成日期:** 2026-04-26  
**状态:** ✅ 已完成  

---

## 📋 概述

本项目已成功集成阿里百炼大模型API,用于:
1. ✅ 客户信息智能解析
2. ✅ AI日报自动生成
3. ✅ 营销方案智能生成

---

## 🔧 配置说明

### 1. API密钥配置

#### 方式A: 环境变量(推荐)

在服务器或本地环境中设置:

```bash
export ALIYUN_BAILIAN_API_KEY=sk-440c8d370a8744f08a6a0
```

或在 `.env` 文件中:
```
ALIYUN_BAILIAN_API_KEY=sk-440c8d370a8744f08a6a0
```

#### 方式B: application.yml (已配置默认值)

```yaml
aliyun:
  bailian:
    api-key: ${ALIYUN_BAILIAN_API_KEY:sk-440c8d370a8744f08a6a0}
    base-url: https://dashscope.aliyuncs.com/api/v1
    model: qwen-plus
```

**优先级:** 环境变量 > 配置文件默认值

---

### 2. 依赖添加

已在 `pom.xml` 中添加:

```xml
<!-- HTTP Client for AI API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

---

## 📁 新增文件

### 1. AliyunBailianClient.java

**位置:** `backend/src/main/java/com/sales/customer/client/AliyunBailianClient.java`

**功能:**
- 封装阿里百炼API调用
- 处理请求和响应
- 错误处理和降级方案

**核心方法:**
```java
public String callAI(String prompt)
```

---

### 2. 更新的AiServiceImpl.java

**改进:**
- ✅ 注入AliyunBailianClient
- ✅ parseCustomerInfo使用AI解析
- ✅ generateDailyReport使用AI生成
- ✅ generateMarketingPlan使用AI生成
- ✅ 所有方法都有降级方案

---

## 🎯 功能说明

### 1. 客户信息解析

**原方式:** 正则表达式  
**新方式:** AI智能解析 + 正则降级

**提示词示例:**
```
请从以下文本中提取客户信息，并以JSON格式返回：
{
  "name": "客户姓名",
  "company": "公司名称",
  "position": "职位",
  "phone": "手机号",
  "requirement": "客户需求"
}

文本内容：张三，某某科技公司，销售经理，电话13800138000，需要采购办公软件

如果某些字段不存在，请设置为null。只返回JSON，不要其他内容。
```

**降级方案:** 如果AI调用失败,自动使用正则表达式解析

---

### 2. AI日报生成

**提示词示例:**
```
请根据以下今日客户拜访记录，生成一份专业的销售日报：

客户拜访记录：
- 张三（某某公司，经理）：需要采购办公软件
- 李四（XYZ公司，总监）：意向强烈

要求：
1. 包含今日工作总结
2. 列出重点客户
3. 分析客户意向
4. 制定明日计划
5. 用简洁专业的中文，分段落输出
```

**降级方案:** 返回简化的统计报告

---

### 3. 营销方案生成

**提示词示例:**
```
请为以下客户生成一份专业的营销方案：

客户信息：
- 姓名：张三
- 公司：某某公司
- 职位：经理
- 电话：13800138000
- 需求：需要采购办公软件

要求：
1. 分析客户背景和需求
2. 制定营销策略（至少4条）
3. 制定跟进计划（分阶段，含时间节点）
4. 提供话术建议
5. 用专业、简洁的中文输出，分段落
```

**降级方案:** 返回通用营销方案模板

---

## 🧪 测试方法

### 1. 启动后端服务

```bash
cd backend
mvn spring-boot:run
```

### 2. 测试客户信息解析

```bash
curl -X POST http://localhost:8080/api/customer/add \
  -F "content=张三，某某科技公司，销售经理，13800138000，需要采购办公软件" \
  -F "salesId=1"
```

**查看日志:**
```
INFO  - 调用阿里百炼AI, 模型: qwen-plus
INFO  - AI响应: {...}
INFO  - 解析结果: Customer{name='张三', company='某某科技公司', ...}
```

---

### 3. 测试日报生成

```bash
curl -X POST http://localhost:8080/api/daily-report/generate \
  -H "Content-Type: application/json" \
  -d '{"salesId":1,"date":"2026-04-26"}'
```

**预期响应:**
```json
{
  "code": 200,
  "data": {
    "reportContent": "今日工作总结：\n\n今日共拜访3位客户...\n\n重点客户：\n1. 张三...\n\n明日计划：\n..."
  }
}
```

---

### 4. 测试营销方案生成

```bash
curl -X POST http://localhost:8080/api/plan/generate?customerId=1
```

**预期响应:**
```json
{
  "code": 200,
  "data": {
    "planContent": "营销方案 - 张三\n\n客户背景分析：\n张三来自某某公司...\n\n营销策略：\n1. ...\n2. ...\n\n跟进计划：\n- 第1天：...\n- 第3天：..."
  }
}
```

---

## 📊 日志监控

### 成功日志示例:

```
INFO  - 调用阿里百炼AI, 模型: qwen-plus
INFO  - AI响应: {"output":{"choices":[...]}}
INFO  - 为客户生成营销方案: 张三
```

### 错误日志示例:

```
ERROR - 调用阿里百炼AI失败: Connection timeout
WARN  - 使用降级方案生成响应
```

---

## ⚠️ 注意事项

### 1. API配额管理

阿里百炼API有调用次数限制,建议:
- 监控每日调用次数
- 设置限流策略
- 缓存常用结果

### 2. 成本控制

- qwen-plus模型按token计费
- 优化提示词长度
- 避免重复调用

### 3. 超时处理

WebClient默认超时时间较短,如需调整:

```java
WebClient.builder()
    .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
    .build();
```

### 4. 安全建议

- **不要**将API密钥提交到Git
- 使用环境变量或密钥管理服务
- 定期轮换密钥

---

## 🔍 故障排查

### 问题1: API调用失败

**症状:**
```
ERROR - 调用阿里百炼AI失败: 401 Unauthorized
```

**原因:**
- API密钥错误或过期
- 密钥格式不正确

**解决:**
1. 检查环境变量是否正确设置
2. 验证密钥有效性
3. 确认密钥前缀为`sk-`

---

### 问题2: 响应解析失败

**症状:**
```
ERROR - 无法解析AI响应
```

**原因:**
- API返回格式变化
- 网络问题导致响应不完整

**解决:**
1. 检查完整响应内容
2. 查看阿里百炼API文档更新
3. 重试请求

---

### 问题3: 降级方案频繁触发

**症状:**
```
WARN  - 使用降级方案生成响应
```

**原因:**
- 网络连接不稳定
- API服务暂时不可用
- 超时设置过短

**解决:**
1. 检查网络连接
2. 增加超时时间
3. 实现重试机制

---

## 📈 性能优化建议

### 1. 异步调用

对于非实时场景,可以使用异步调用:

```java
public CompletableFuture<String> callAIAsync(String prompt) {
    return CompletableFuture.supplyAsync(() -> callAI(prompt));
}
```

### 2. 结果缓存

对于相同内容的请求,可以缓存结果:

```java
@Cacheable(value = "ai_responses", key = "#prompt")
public String callAI(String prompt) {
    // ...
}
```

### 3. 批量处理

对于多个客户的方案生成,可以批量调用:

```java
public List<String> batchGenerate(List<Customer> customers) {
    return customers.parallelStream()
        .map(this::generateMarketingPlan)
        .collect(Collectors.toList());
}
```

---

## 🚀 部署到生产环境

### 1. 设置环境变量

在Docker Compose中:

```yaml
backend:
  environment:
    ALIYUN_BAILIAN_API_KEY: ${ALIYUN_BAILIAN_API_KEY}
```

在`.env`文件中:
```
ALIYUN_BAILIAN_API_KEY=sk-your-actual-api-key
```

### 2. 监控和告警

- 监控API调用成功率
- 设置错误率告警阈值
- 记录响应时间指标

### 3. 备份方案

确保降级方案正常工作,当AI服务不可用时系统仍能运行。

---

## 📝 相关文档

- [阿里百炼官方文档](https://help.aliyun.com/zh/model-studio/)
- [Spring WebClient文档](https://docs.spring.io/spring-framework/docs/current/reference/html/web-reactive.html#webflux-client)
- [AiServiceImpl实现](file://d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend\src\main\java\com\sales\customer\service\impl\AiServiceImpl.java)
- [AliyunBailianClient客户端](file://d:\360MoveData\Users\wangbo\Desktop\客户信息收集\xinhuaproject\backend\src\main\java\com\sales\customer\client\AliyunBailianClient.java)

---

**集成完成时间:** 2026-04-26  
**Git提交:** 待提交  
**测试状态:** 待测试
