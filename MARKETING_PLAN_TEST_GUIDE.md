# 营销方案批量生成功能 - 快速测试指南

**测试目标:** 验证批量生成时每个方案正确关联到对应的客户ID  
**测试文件:** `MarketingPlanServiceImpl.java`  

---

## 🧪 测试环境准备

### 1. 启动后端服务
```bash
cd backend
mvn spring-boot:run
```

### 2. 准备测试数据

确保数据库中有测试客户:

```sql
-- 插入测试客户
INSERT INTO customer (name, company, position, phone, content, sales_id) 
VALUES 
  ('张三', '公司A', '经理', '13800138001', '意向客户', 1),
  ('李四', '公司B', '总监', '13800138002', '需要跟进', 1),
  ('王五', '公司C', 'CEO', '13800138003', '重要客户', 1);

-- 查看客户ID
SELECT id, name, company FROM customer;
```

假设得到的ID为: 1, 2, 3

---

## ✅ 测试用例

### 测试1: Postman批量生成测试

#### 步骤:

1. **打开Postman**

2. **创建新请求:**
   - Method: POST
   - URL: `http://localhost:8080/api/plan/batch-generate`
   - Headers: `Content-Type: application/json`

3. **请求体:**
   ```json
   [1, 2, 3]
   ```

4. **发送请求**

5. **检查响应:**
   ```json
   {
     "code": 200,
     "message": "success",
     "data": [
       {
         "id": 1,
         "customerId": 1,  // ← 应该对应请求中的第一个ID
         "planContent": "...",
         "status": "pending",
         "priority": "medium"
       },
       {
         "id": 2,
         "customerId": 2,  // ← 应该对应请求中的第二个ID
         "planContent": "...",
         "status": "pending",
         "priority": "medium"
       },
       {
         "id": 3,
         "customerId": 3,  // ← 应该对应请求中的第三个ID
         "planContent": "...",
         "status": "pending",
         "priority": "medium"
       }
     ]
   }
   ```

#### 验证点:
- ✅ 返回3个方案
- ✅ 每个方案的customerId分别是1、2、3
- ✅ 没有所有方案的customerId都是1的情况
- ✅ planContent各不相同(因为客户信息不同)

---

### 测试2: 数据库验证

#### SQL查询:

```sql
-- 查看最新生成的方案
SELECT 
    id,
    customer_id,
    SUBSTRING(plan_content, 1, 50) as content_preview,
    status,
    priority,
    create_time
FROM marketing_plan
ORDER BY create_time DESC
LIMIT 10;
```

#### 预期结果:

| id | customer_id | content_preview | status | priority |
|----|-------------|-----------------|--------|----------|
| 3  | 3           | 针对王五的...    | pending| medium   |
| 2  | 2           | 针对李四的...    | pending| medium   |
| 1  | 1           | 针对张三的...    | pending| medium   |

**关键检查:**
- ✅ customer_id列应该是 1, 2, 3 (不是全部为1)
- ✅ 每个customer_id只出现一次(除非多次生成)

---

### 测试3: 部分客户不存在

#### 请求:
```json
[1, 999, 3]
```
(假设999号客户不存在)

#### 预期响应:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "customerId": 1,
      ...
    },
    {
      "customerId": 3,
      ...
    }
  ]
}
```

**验证点:**
- ✅ 只返回2个方案(跳过999)
- ✅ 不包含customerId=999的方案
- ✅ 其他客户正常生成

---

### 测试4: 空列表测试

#### 请求:
```json
[]
```

#### 预期响应:
```json
{
  "code": 500,
  "message": "客户ID列表不能为空",
  "data": null
}
```

---

### 测试5: 日志验证

#### 查看应用日志:

```bash
# Linux/Mac
tail -f backend/logs/application.log

# Windows PowerShell
Get-Content backend\logs\application.log -Wait -Tail 50
```

#### 执行批量生成后,日志应该显示:

```
INFO  - 开始批量生成营销方案, 客户数量: 3
INFO  - 正在为客户 ID=1 生成营销方案
INFO  - 客户 ID=1 的营销方案生成成功
INFO  - 正在为客户 ID=2 生成营销方案
INFO  - 客户 ID=2 的营销方案生成成功
INFO  - 正在为客户 ID=3 生成营销方案
INFO  - 客户 ID=3 的营销方案生成成功
INFO  - 批量生成完成: 总数=3, 成功=3, 失败=0
```

**检查点:**
- ✅ 每个客户ID都有"正在生成"和"生成成功"的日志
- ✅ 没有重复的客户ID
- ✅ 统计数字正确(总数=成功+失败)

---

### 测试6: 单元测试运行

#### 运行测试:

```bash
cd backend
mvn test -Dtest=MarketingPlanServiceBatchTest
```

#### 预期输出:

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.sales.customer.service.MarketingPlanServiceBatchTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
```

**6个测试用例:**
1. ✅ 所有客户都存在
2. ✅ 部分客户不存在
3. ✅ 空列表
4. ✅ 验证方案内容不同
5. ✅ 验证方案状态和优先级
6. ✅ AI服务异常时继续处理

---

## 🔍 常见错误排查

### 问题1: 所有方案的customerId都相同

**症状:**
```json
[
  {"customerId": 1, ...},
  {"customerId": 1, ...},  // ← 错误! 应该是客户2
  {"customerId": 1, ...}   // ← 错误! 应该是客户3
]
```

**原因:**
代码中可能错误地使用了固定的customerId:
```java
// ❌ 错误代码
Long firstId = customerIds.get(0);
for (Long customerId : customerIds) {
    plan.setCustomerId(firstId); // 始终使用第一个ID
}
```

**解决:**
已修复,当前代码正确使用循环变量:
```java
// ✅ 正确代码
for (Long customerId : customerIds) {
    plan.setCustomerId(customerId); // 使用当前的customerId
}
```

---

### 问题2: 批量生成超时

**症状:**
请求超过30秒无响应

**原因:**
- AI服务调用过慢
- 客户数量过多

**解决:**
1. 减少批量大小(建议每次10-20个)
2. 检查AI服务响应时间
3. 考虑异步处理

---

### 问题3: 部分方案生成失败

**症状:**
返回的方案数量少于请求的客户数量

**检查日志:**
```
ERROR - 为客户 ID=2 生成营销方案时发生错误: AI服务异常
```

**原因:**
- 某个客户的AI生成失败
- 客户不存在

**解决:**
这是预期行为,失败的会被跳过,其他客户正常生成。查看日志确认失败原因。

---

## 📊 性能测试

### 测试不同批量大小:

| 客户数量 | 预期时间 | 内存占用 | 建议 |
|---------|---------|---------|------|
| 1-10    | < 5秒   | 低      | ✅ 推荐 |
| 11-50   | 5-30秒  | 中      | ✅ 可用 |
| 51-100  | 30-60秒 | 中高    | ⚠️ 注意监控 |
| 100+    | > 60秒  | 高      | ❌ 建议分批 |

### 压力测试脚本:

```bash
# 使用ab(Apache Bench)测试
ab -n 10 -c 2 -p batch_data.json -T application/json \
   http://localhost:8080/api/plan/batch-generate
```

**batch_data.json:**
```json
[1, 2, 3, 4, 5]
```

---

## ✅ 验收清单

### 功能验收

- [ ] 批量接口可以正常调用
- [ ] 返回的方案数量正确
- [ ] 每个方案的customerId与请求对应
- [ ] 没有所有方案customerId相同的情况
- [ ] 不存在的客户被正确跳过
- [ ] 空列表返回错误提示

### 数据验收

- [ ] 数据库中customer_id字段值正确
- [ ] 没有重复的方案(除非多次生成)
- [ ] 方案内容与客户信息匹配
- [ ] 状态和优先级字段正确

### 日志验收

- [ ] 有"开始批量生成"日志
- [ ] 每个客户都有"正在生成"日志
- [ ] 成功/失败计数正确
- [ ] 没有未捕获的异常

### 性能验收

- [ ] 10个客户 < 5秒
- [ ] 50个客户 < 30秒
- [ ] 内存占用合理(< 500MB)
- [ ] 无内存泄漏

---

## 🎯 快速验证脚本

### curl命令测试:

```bash
# 批量生成
curl -X POST http://localhost:8080/api/plan/batch-generate \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'

# 查看结果
curl -X GET http://localhost:8080/api/plan/list
```

### Python验证脚本:

```python
import requests
import json

# 批量生成
response = requests.post(
    'http://localhost:8080/api/plan/batch-generate',
    json=[1, 2, 3]
)

data = response.json()
plans = data['data']

# 验证customerId
expected_ids = [1, 2, 3]
actual_ids = [plan['customerId'] for plan in plans]

print(f"期望的ID: {expected_ids}")
print(f"实际的ID: {actual_ids}")

if sorted(expected_ids) == sorted(actual_ids):
    print("✅ 验证通过: 所有方案都关联到正确的客户ID")
else:
    print("❌ 验证失败: 客户ID不匹配")
    print(f"缺少的ID: {set(expected_ids) - set(actual_ids)}")
    print(f"多余的ID: {set(actual_ids) - set(expected_ids)}")
```

---

## 📝 测试报告模板

```markdown
# 营销方案批量生成测试报告

**测试日期:** _____________
**测试人员:** _____________

## 测试结果

- 批量接口调用: □ 通过 □ 失败
- 客户ID关联正确性: □ 通过 □ 失败
- 数据库验证: □ 通过 □ 失败
- 日志完整性: □ 通过 □ 失败
- 单元测试: □ 通过 □ 失败

## 发现的问题

1. ________________________________
2. ________________________________

## 性能数据

- 10个客户耗时: _____秒
- 50个客户耗时: _____秒
- 内存峰值: _____MB

## 结论

□ 可以发布
□ 需要修复后重新测试

签名: _____________
```

---

**测试文档版本:** 1.0  
**最后更新:** 2026-04-26  
**相关文档:** [MARKETING_PLAN_BATCH_FIX.md](./MARKETING_PLAN_BATCH_FIX.md)
