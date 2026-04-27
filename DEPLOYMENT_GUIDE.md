# 智销助手 - 云服务器部署指南

## 📦 最新版本更新

### 2026-04-27 更新内容

#### ✅ 已修复问题

1. **登录状态丢失问题**
   - 修复: 从localStorage恢复用户信息
   - 效果: 刷新页面或重新打开后,登录状态不会丢失

2. **添加退出按钮**
   - Home页面顶部添加用户名称显示
   - 添加退出登录按钮,点击确认后跳转登录页

3. **管理端客户列表**
   - 完善管理端客户列表页面
   - 支持搜索(姓名/公司/手机)
   - 支持查看客户详情(公司、职位、需求等)
   - 后端添加 `/api/admin/customers` 接口

#### ⚠️ 已知问题

1. **手机录音功能不稳定**
   - 原因: 浏览器Web Speech API兼容性问题
   - 影响: 部分手机浏览器录音经常失败
   - 解决方案: 见下方"录音问题解决方案"

---

## 🚀 部署到云服务器

### 步骤1: 推送代码到GitHub

```bash
# 在本地执行(已完成)
git push origin master
```

### 步骤2: 在服务器上拉取最新代码

```bash
ssh ubuntu@82.156.165.194

cd ~/xinhuaproject
git pull origin master
```

### 步骤3: 重新构建并启动服务

```bash
# 构建前端(包含登录状态修复和管理端客户列表)
docker-compose up -d --build sales-frontend admin-frontend

# 重启后端(包含新的管理端API)
docker-compose restart backend

# 等待30秒
sleep 30

# 检查所有容器状态
docker-compose ps

# 查看后端日志,确认数据库连接正常
docker logs sales-backend --tail=50
```

### 步骤4: 验证功能

#### 4.1 销售前端测试
```
http://82.156.165.194
```

✅ 验证清单:
- [ ] 登录后刷新页面,登录状态保持
- [ ] 顶部显示用户名称(👤 张三)
- [ ] 顶部右侧有"退出"按钮
- [ ] 点击退出按钮,确认对话框弹出
- [ ] 录入客户信息成功

#### 4.2 管理后台测试
```
http://82.156.165.194:81
```

✅ 验证清单:
- [ ] 登录后进入客户管理页面
- [ ] 能看到销售端录入的客户数据
- [ ] 搜索功能正常(姓名/公司/手机)
- [ ] 点击客户查看详情弹窗
- [ ] 详情显示完整信息

---

## 🎤 手机录音问题解决方案

### 问题分析

当前录音功能使用浏览器的 **Web Speech API** (语音识别),存在以下限制:

1. **兼容性差**
   - Chrome for Android: ✅ 支持较好
   - Safari iOS: ⚠️ 部分支持,需要用户手动授权
   - 微信内置浏览器: ❌ 不支持
   - 其他浏览器: ❌ 大多不支持

2. **需要HTTPS**
   - 麦克风权限在非HTTPS环境下不可用
   - 虽然localhost例外,但手机访问必须用HTTPS

3. **网络依赖**
   - 语音识别需要联网
   - 网络不稳定时识别失败

---

### 解决方案对比

| 方案 | 开发成本 | 录音稳定性 | 语音识别 | 部署成本 | 推荐度 |
|------|---------|-----------|---------|---------|--------|
| **方案1: 优化H5网页** | 低 | 中 | 浏览器API | 低 | ⭐⭐⭐ |
| **方案2: 微信小程序** | 中 | 高 | 微信API | 低 | ⭐⭐⭐⭐⭐ |
| **方案3: Android/iOS App** | 高 | 高 | 原生API | 高 | ⭐⭐⭐ |
| **方案4: Taro编译小程序** | 低 | 高 | 小程序API | 低 | ⭐⭐⭐⭐⭐ |

---

### 🎯 推荐方案: 转换为微信小程序(基于Taro)

您的项目已经使用 **Taro框架**,可以直接编译为小程序!

#### 优势

1. **开发成本最低**
   - 代码改动最小(主要改API调用)
   - Taro已经配置好,只需修改编译目标
   - 语音识别使用微信原生API,稳定性好

2. **录音稳定性高**
   - 微信小程序的录音API非常稳定
   - 无需HTTPS(小程序天然安全)
   - 微信语音识别准确率高

3. **用户体验好**
   - 小程序体验接近原生App
   - 可以添加到手机桌面
   - 支持离线缓存

4. **部署成本低**
   - 只需配置小程序服务器域名
   - 后端代码无需改动
   - 审核通过后即可使用

---

### 实施步骤

#### 步骤1: 配置小程序编译

```bash
# 在sales-frontend目录
cd sales-frontend

# 安装微信小程序插件(如果还没安装)
npm install -D @tarojs/plugin-platform-weapp

# 修改config/index.js,添加微信小程序配置
```

#### 步骤2: 修改语音识别代码

```javascript
// 原来: 使用浏览器Web Speech API
const recognition = new webkitSpeechRecognition()

// 改为: 使用微信小程序录音+语音识别
const recorderManager = Taro.getRecorderManager()
const plugin = requirePlugin("WechatSI")

recorderManager.onStart(() => {
  console.log('开始录音')
})

recorderManager.onStop((res) => {
  // 上传录音文件到后端进行语音识别
  Taro.uploadFile({
    url: 'https://your-domain.com/api/voice/recognize',
    filePath: res.tempFilePath,
    name: 'audio',
    success: (uploadRes) => {
      // 处理识别结果
    }
  })
})
```

#### 步骤3: 后端添加语音识别接口

```java
@PostMapping("/voice/recognize")
public Result<String> recognizeVoice(@RequestParam("audio") MultipartFile audio) {
    // 使用阿里云百炼语音识别API
    // 返回识别后的文本
}
```

#### 步骤4: 配置小程序服务器域名

在微信公众平台配置:
```
request合法域名: https://82.156.165.194
uploadFile合法域名: https://82.156.165.194
```

#### 步骤5: 编译并发布

```bash
# 编译为微信小程序
npm run build:weapp

# 上传代码
# 使用微信开发者工具打开dist目录
# 点击"上传"按钮
```

---

### 备选方案: 优化H5网页(快速方案)

如果暂时不想做小程序,可以先优化H5网页:

#### 优化1: 添加HTTPS支持

```bash
# 在服务器安装SSL证书
sudo apt-get install certbot python3-certbot-nginx

# 申请免费证书(Let's Encrypt)
sudo certbot --nginx -d your-domain.com

# 自动续期
sudo certbot renew --dry-run
```

#### 优化2: 使用阿里云语音识别API

```javascript
// 不再依赖浏览器语音识别
// 改为: 录音后上传到后端,后端调用阿里云API识别

const submitVoiceRecording = async (audioBlob) => {
  const formData = new FormData()
  formData.append('audio', audioBlob, 'recording.wav')
  
  const result = await request.post('/voice/recognize', formData)
  return result.text // 返回识别文本
}
```

#### 优化3: 提供文字输入备选

```vue
<!-- 当检测到不支持语音时,自动切换到文字输入 -->
<template>
  <div v-if="!isSpeechSupported" class="text-only-mode">
    <van-field
      v-model="textInput"
      placeholder="请输入客户信息..."
      @keyup.enter="submitText"
    />
    <van-button @click="submitText">提交</van-button>
  </div>
</template>
```

---

## 📊 部署检查清单

### 服务器端
- [ ] 代码已更新到最新版本(`git pull`)
- [ ] 所有容器运行正常(`docker-compose ps`)
- [ ] 数据库连接正常(查看后端日志)
- [ ] 端口映射正确(80, 81, 8080)
- [ ] 安全组配置正确(80, 81, 8080, 22端口开放)

### 销售前端
- [ ] 访问 `http://82.156.165.194` 能打开登录页
- [ ] 登录后状态保持(刷新页面不丢失)
- [ ] 顶部显示用户名称
- [ ] 退出按钮正常工作
- [ ] 录入客户成功

### 管理后台
- [ ] 访问 `http://82.156.165.194:81` 能打开
- [ ] 客户列表显示所有客户数据
- [ ] 搜索功能正常
- [ ] 客户详情弹窗正常

### 功能测试
- [ ] 客户录入(文字)
- [ ] 客户录入(语音) - 如支持
- [ ] 客户列表查询
- [ ] 客户详情查看
- [ ] AI日报生成(如需要)
- [ ] 营销方案生成(如需要)

---

## 🔧 常用运维命令

### 查看日志
```bash
# 查看所有容器状态
docker-compose ps

# 查看后端日志
docker logs sales-backend --tail=100

# 查看前端日志
docker logs sales-frontend --tail=50

# 实时查看日志
docker logs -f sales-backend
```

### 重启服务
```bash
# 重启单个服务
docker-compose restart backend
docker-compose restart sales-frontend

# 重启所有服务
docker-compose restart
```

### 更新部署
```bash
# 拉取最新代码
git pull origin master

# 重新构建并启动
docker-compose up -d --build

# 查看状态
docker-compose ps
```

### 数据库操作
```bash
# 进入MySQL容器
docker exec -it sales-mysql mysql -uroot -proot123

# 查看数据库
SHOW DATABASES;
USE sales_customer_db;
SHOW TABLES;

# 查询客户数据
SELECT * FROM customer ORDER BY create_time DESC LIMIT 10;
```

---

## 📞 问题排查

### 问题1: 前端访问404
```bash
# 检查容器状态
docker-compose ps

# 检查nginx配置
docker exec sales-frontend cat /etc/nginx/conf.d/default.conf

# 重启前端
docker-compose restart sales-frontend
```

### 问题2: 后端API返回401
```bash
# 检查后端日志
docker logs sales-backend --tail=100

# 检查数据库连接
docker exec sales-mysql mysql -uroot -proot123 -e "SELECT 1"

# 重启后端
docker-compose restart backend
```

### 问题3: 数据库连接失败
```bash
# 查看MySQL日志
docker logs sales-mysql --tail=50

# 检查数据库密码
docker-compose down
docker volume rm xinhuaproject_mysql-data
docker-compose up -d
```

---

## 📝 下一步建议

### 短期优化(1-2天)
1. ✅ 配置HTTPS(使用Let's Encrypt免费证书)
2. ✅ 优化语音识别(后端调用阿里云API)
3. ✅ 添加错误提示和加载状态

### 中期优化(1周)
1. ✅ 转换为微信小程序(推荐!)
2. ✅ 添加客户分类和标签
3. ✅ 添加数据统计和报表

### 长期规划(1个月)
1. ✅ 开发Android/iOS App(可选)
2. ✅ 添加CRM功能(跟进记录、商机管理)
3. ✅ 集成企业微信/钉钉

---

## 🎉 总结

**当前版本已完成的功能:**
- ✅ 销售端客户录入(文字)
- ✅ 销售端登录状态保持
- ✅ 管理端客户列表和详情
- ✅ 客户搜索功能
- ✅ 基础UI和交互

**待优化功能:**
- ⚠️ 语音识别稳定性(建议转小程序)
- ⚠️ HTTPS支持(建议配置SSL证书)
- ⚠️ 附件上传功能(待完善)
- ⚠️ AI功能测试(待验证)

**推荐下一步:**
🎯 **转换为微信小程序** - 最佳方案,开发成本低,用户体验好!

---

## 📞 联系支持

如有问题,请查看:
- GitHub仓库: https://github.com/dtlqwb/xinhuaproject
- 服务器IP: 82.156.165.194
- 部署文档: DEPLOYMENT_GUIDE.md

---

**最后更新:** 2026-04-27  
**版本:** v1.2.0
