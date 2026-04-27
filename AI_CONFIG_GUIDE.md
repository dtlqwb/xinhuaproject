# 阿里云百炼AI API配置指南

## 📋 前置准备

### 1. 注册阿里云账号

如果您还没有阿里云账号,请先注册:
- 访问: https://www.aliyun.com/
- 完成实名认证

### 2. 开通百炼服务

1. 登录阿里云控制台
2. 搜索"百炼"或"DashScope"
3. 点击"开通服务"
4. 同意服务协议并开通

### 3. 获取API Key

1. 访问百炼控制台: https://dashscope.console.aliyun.com/apiKey
2. 点击"创建API Key"
3. 复制生成的API Key(格式: `sk-xxxxxxxxxxxxxxxx`)
4. **妥善保管**,不要泄露给他人

---

## 🔧 配置方式

### 方式1: 本地开发环境配置

在项目根目录创建 `.env` 文件:

```bash
# 复制模板
cp .env.example .env

# 编辑.env文件
vim .env
```

修改以下内容:

```env
# 阿里云百炼AI配置
ALIYUN_BAILIAN_API_KEY=sk-你的实际API-Key
```

### 方式2: 服务器生产环境配置

#### 步骤1: SSH登录服务器

```bash
ssh ubuntu@82.156.165.194
```

#### 步骤2: 创建或编辑.env文件

```bash
cd ~/xinhuaproject

# 创建.env文件
cat > .env << EOF
DB_PASSWORD=root123
JWT_SECRET=salesCustomerSystemSecretKey2024Production
ALIYUN_BAILIAN_API_KEY=sk-你的实际API-Key
EOF
```

**重要**: 将 `sk-你的实际API-Key` 替换为您从阿里云获取的真实API Key!

#### 步骤3: 验证.env文件

```bash
cat .env
```

应该看到类似内容:
```
DB_PASSWORD=root123
JWT_SECRET=salesCustomerSystemSecretKey2024Production
ALIYUN_BAILIAN_API_KEY=sk-440c8d370a8744f08a6a0xxxxxxxxx
```

#### 步骤4: 重新部署

```bash
# 停止服务
docker-compose down

# 拉取最新代码
git pull origin master

# 重新构建并启动
docker-compose build --no-cache backend
docker-compose up -d

# 等待30秒
sleep 30
```

#### 步骤5: 验证配置

```bash
# 查看后端日志,确认AI配置已加载
docker logs sales-backend | grep -i aliyun

# 测试AI功能(客户解析)
curl -X POST http://localhost:8080/api/sales/customer/add \
  -H "Content-Type: application/json" \
  -d '{
    "content": "张三 13800138000 腾讯科技 产品经理",
    "salesId": 1
  }'
```

---

## ✅ 验证AI功能

### 测试1: 客户信息解析

```bash
curl -X POST http://localhost:8080/api/sales/customer/add \
  -H "Content-Type: application/json" \
  -d '{
    "content": "李四 13900139000 阿里巴巴 技术总监 需要采购云服务器",
    "salesId": 1
  }'
```

预期返回:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 5,
    "name": "李四",
    "phone": "13900139000",
    "company": "阿里巴巴",
    "position": "技术总监",
    "requirement": "需要采购云服务器",
    ...
  }
}
```

### 测试2: AI生成日报

访问管理端或通过API调用日报生成功能。

### 测试3: AI生成营销方案

在管理端为客户生成营销方案时,AI会自动分析客户需求并生成个性化方案。

---

## ⚠️ 注意事项

### 1. API Key安全

- ❌ **不要**将API Key提交到Git仓库
- ❌ **不要**在公开场合分享API Key
- ✅ 使用环境变量或`.env`文件管理
- ✅ 定期轮换API Key

### 2. .gitignore配置

确保`.env`文件已被忽略:

```bash
# 检查.gitignore
cat .gitignore | grep .env
```

应该看到:
```
.env
```

如果没有,请添加:

```bash
echo ".env" >> .gitignore
git add .gitignore
git commit -m "chore: 忽略.env文件"
git push
```

### 3. 费用说明

阿里云百炼API是**付费服务**,但新用户有免费额度:

- Qwen-Plus模型: 约 ¥0.008/千token
- 每月赠送一定额度的免费token
- 详细价格见: https://help.aliyun.com/pricing

建议:
- 开发测试时使用少量请求
- 监控用量避免超额
- 设置预算告警

### 4. 网络要求

确保服务器可以访问阿里云API:

```bash
# 测试网络连接
curl -I https://dashscope.aliyuncs.com
```

应该返回 `HTTP/1.1 200 OK`

如果无法访问,可能需要:
- 检查防火墙规则
- 配置代理(如果需要)
- 联系阿里云客服

---

## 🔍 故障排查

### 问题1: AI功能不工作

**症状**: 客户解析失败,返回原始文本

**检查步骤**:

```bash
# 1. 检查环境变量是否设置
docker exec sales-backend env | grep ALIYUN

# 2. 查看后端日志
docker logs sales-backend --tail 100 | grep -i error

# 3. 测试API连通性
docker exec sales-backend curl -I https://dashscope.aliyuncs.com
```

**解决方案**:

```bash
# 重新设置环境变量
cd ~/xinhuaproject
cat > .env << EOF
ALIYUN_BAILIAN_API_KEY=sk-你的实际API-Key
EOF

# 重启服务
docker-compose restart backend
```

### 问题2: API Key无效

**症状**: 日志显示 "Invalid API Key" 或 "Authentication failed"

**解决方案**:

1. 检查API Key是否正确复制(没有多余空格)
2. 确认API Key未过期
3. 重新生成新的API Key
4. 更新`.env`文件并重启服务

### 问题3: 配额不足

**症状**: 日志显示 "Quota exceeded" 或 "Rate limit"

**解决方案**:

1. 登录阿里云控制台查看用量
2. 充值或申请提高配额
3. 优化调用频率,避免短时间内大量请求

### 问题4: 模型不可用

**症状**: 返回 "Model not found" 错误

**解决方案**:

检查 `application.yml` 中的模型配置:

```yaml
aliyun:
  bailian:
    model: qwen-plus  # 确认模型名称正确
```

可用模型列表:
- `qwen-turbo` - 快速响应
- `qwen-plus` - 平衡性能(推荐)
- `qwen-max` - 最强能力

---

## 📊 监控用量

### 查看API调用统计

登录阿里云百炼控制台:
https://dashscope.console.aliyun.com/usage

可以看到:
- 今日调用次数
- Token消耗量
- 费用统计

### 设置告警

在阿里云控制台设置预算告警:
1. 进入"费用中心"
2. 选择"预算管理"
3. 创建预算告警规则
4. 设置阈值和通知方式

---

## 🎯 最佳实践

### 1. 缓存AI结果

对于相同的输入,可以考虑缓存AI的响应结果,避免重复调用。

### 2. 异步处理

对于耗时较长的AI任务(如批量生成方案),建议使用异步处理。

### 3. 错误重试

在网络不稳定时,实现简单的重试机制:

```java
// 示例: 最多重试3次
for (int i = 0; i < 3; i++) {
    try {
        return callAiApi();
    } catch (Exception e) {
        if (i == 2) throw e;
        Thread.sleep(1000 * (i + 1)); // 递增延迟
    }
}
```

### 4. 降级策略

当AI服务不可用时,提供降级方案:

```java
try {
    return aiService.parseCustomer(content);
} catch (Exception e) {
    log.warn("AI服务不可用,使用默认解析", e);
    return fallbackParse(content); // 简单的正则解析
}
```

---

## 📞 技术支持

如遇到问题:

1. 查看后端日志: `docker logs sales-backend`
2. 检查阿里云控制台错误信息
3. 参考阿里云文档: https://help.aliyun.com/product/334875.html
4. 联系阿里云客服

---

## ✨ 总结

配置完成后,AI功能将自动启用:

- ✅ 语音输入自动解析客户信息
- ✅ 自动生成销售日报
- ✅ 智能生成营销方案
- ✅ AI辅助数据分析

祝您使用愉快! 🎉
