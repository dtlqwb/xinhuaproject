# 智销助手 - 微信小程序版

## 📦 项目状态

✅ **Taro项目脚手架已创建**  
⏳ **待完成**: 页面代码迁移、语音识别改造

---

## 🚀 快速开始

### 1. 安装依赖

```bash
cd sales-mini-program
npm install
```

### 2. 开发模式

```bash
# 编译为微信小程序(开发模式,自动监听文件变化)
npm run dev:weapp

# 或编译为H5(在浏览器调试)
npm run dev:h5
```

### 3. 用微信开发者工具打开

1. 下载并安装 [微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)
2. 打开微信开发者工具
3. 导入项目: 选择 `sales-mini-program/dist` 目录
4. 填写AppID (可先用测试号)
5. 开始调试

---

## 📁 项目结构

```
sales-mini-program/
├── config/                 # 编译配置
│   ├── dev.js
│   ├── index.js
│   └── prod.js
├── src/
│   ├── pages/             # 页面(待创建)
│   │   ├── login/         # 登录页
│   │   └── index/         # 首页
│   ├── components/        # 组件(待创建)
│   ├── api/              # API接口(待创建)
│   ├── stores/           # 状态管理(待创建)
│   ├── app.ts            # 全局入口 ✅
│   └── app.config.ts     # 全局配置 ✅
├── package.json          # 依赖配置 ✅
└── dist/                 # 编译输出(自动生成)
```

---

## 🔧 下一步任务

### 任务1: 迁移登录页面

参考: `sales-frontend/src/views/Login.vue`

创建文件: `src/pages/login/index.vue`

**核心改动:**
```vue
<!-- 原来 -->
import { useRouter } from 'vue-router'
const router = useRouter()
router.push('/')

<!-- 改为 -->
import Taro from '@tarojs/taro'
Taro.switchTab({ url: '/pages/index/index' })
```

### 任务2: 迁移首页

参考: `sales-frontend/src/views/Home.vue`

创建文件: `src/pages/index/index.vue`

**核心改动:**
```vue
<!-- 原来 -->
import { onMounted } from 'vue'
onMounted(() => { loadCustomers() })

<!-- 改为 -->
import Taro from '@tarojs/taro'
Taro.useLoad(() => { loadCustomers() })
```

### 任务3: 改造API请求

参考: `sales-frontend/src/utils/request.ts`

创建文件: `src/utils/request.ts`

**核心改动:**
```typescript
// 原来: axios
import axios from 'axios'
const request = axios.create({ baseURL: '/api' })

// 改为: Taro.request
import Taro from '@tarojs/taro'
export const request = <T>(url: string, options: any) => {
  return Taro.request({
    url: `https://82.156.165.194${url}`,
    method: options.method || 'GET',
    data: options.data || {}
  })
}
```

### 任务4: 改造状态管理

参考: `sales-frontend/src/stores/user.ts`

创建文件: `src/stores/user.ts`

**核心改动:**
```typescript
// 原来: localStorage
localStorage.setItem('token', info.token)

// 改为: Taro.storage
Taro.setStorageSync('token', info.token)
```

### 任务5: 改造语音识别 ⭐核心

参考: `sales-frontend/src/components/VoiceInputBar.vue`

创建文件: `src/components/voice-input-bar/index.vue`

**核心改动:**
```typescript
// 原来: 浏览器Web Speech API
const recognition = new webkitSpeechRecognition()

// 改为: 小程序录音 + 后端识别
import Taro from '@tarojs/taro'

const recorderManager = Taro.getRecorderManager()

// 开始录音
recorderManager.start({ format: 'mp3', sampleRate: 16000 })

// 停止录音并上传
recorderManager.onStop((res) => {
  Taro.uploadFile({
    url: 'https://82.156.165.194/api/voice/recognize',
    filePath: res.tempFilePath,
    name: 'audio'
  })
})
```

---

## 📝 详细改造指南

请查看: [MINI_PROGRAM_MIGRATION_GUIDE.md](../MINI_PROGRAM_MIGRATION_GUIDE.md)

该文档包含:
- ✅ 完整的代码示例
- ✅ 关键差异对照表
- ✅ 语音识别改造方案
- ✅ 发布流程说明

---

## 🎯 当前进度

| 任务 | 状态 | 预计时间 |
|-----|------|---------|
| Taro项目脚手架 | ✅ 完成 | - |
| 登录页面迁移 | ⏳ 待开始 | 2小时 |
| 首页迁移 | ⏳ 待开始 | 3小时 |
| API请求改造 | ⏳ 待开始 | 2小时 |
| 状态管理改造 | ⏳ 待开始 | 2小时 |
| 语音识别改造 | ⏳ 待开始 | 4小时 |
| 测试调试 | ⏳ 待开始 | 2天 |
| **总计** | - | **5-8天** |

---

## 💡 开发建议

### 1. 先做H5版本调试

```bash
npm run dev:h5
```

在浏览器中调试,调试完成后再编译小程序。

### 2. 分步迁移

建议顺序:
1. API请求 + 状态管理 (基础依赖)
2. 登录页面 (最简单)
3. 首页 (核心功能)
4. 语音录入 (最复杂)

### 3. 真机测试

```bash
# 编译为微信小程序
npm run build:weapp

# 用微信开发者工具打开dist目录
# 点击"预览"生成二维码
# 手机微信扫码测试
```

---

## 📞 需要帮助?

1. 查看完整迁移指南: `MINI_PROGRAM_MIGRATION_GUIDE.md`
2. 查看H5版参考代码: `sales-frontend/src/`
3. 联系开发团队

---

**最后更新:** 2026-04-27  
**版本:** v1.0.0 (脚手架)
