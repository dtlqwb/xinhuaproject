# VoiceInputBar 录音功能快速测试指南

**测试目标:** 验证录音功能的4项核心改进  
**测试文件:** `sales-frontend/src/components/VoiceInputBar.vue`  

---

## 🧪 测试环境准备

### 1. 启动开发服务器
```bash
cd sales-frontend
npm run dev
```

### 2. 访问地址
- **HTTP测试:** http://localhost:5173 (视为安全环境)
- **HTTPS测试:** 需要配置SSL证书或使用ngrok

---

## ✅ 测试用例清单

### 测试1: 浏览器兼容性检查

#### 步骤:
1. 打开Chrome浏览器开发者工具 (F12)
2. 切换到 Console 标签
3. 在Console中执行:
   ```javascript
   // 模拟不支持getUserMedia的浏览器
   Object.defineProperty(navigator, 'mediaDevices', {
     value: undefined,
     configurable: true
   })
   location.reload()
   ```
4. 刷新页面

#### 预期结果:
- ✅ 显示Toast: "您的浏览器不支持录音功能,请使用键盘输入"
- ✅ 自动切换到键盘输入模式
- ✅ Console显示: "❌ 浏览器不支持 getUserMedia API"

#### 恢复:
```javascript
location.reload() // 恢复正常
```

---

### 测试2: getUserMedia 错误处理

#### 场景A: 权限拒绝 (NotAllowedError)

**步骤:**
1. 正常访问页面
2. 浏览器弹出麦克风权限请求时,点击"阻止"
3. 尝试按住语音按钮

**预期结果:**
- ✅ Toast提示: "麦克风权限被拒绝,请在浏览器设置中允许使用麦克风"
- ✅ 提示位置在顶部 (position: 'top')
- ✅ 持续时间约4秒
- ✅ Console显示: "❌ 麦克风权限请求失败: NotAllowedError"

---

#### 场景B: 设备未找到 (NotFoundError)

**步骤:**
1. 拔掉麦克风设备(或使用虚拟机无麦克风)
2. 访问页面
3. 尝试录音

**预期结果:**
- ✅ Toast提示: "未检测到麦克风设备,请检查硬件连接"
- ✅ Console显示: "❌ 麦克风权限请求失败: NotFoundError"

---

#### 场景C: 设备被占用 (NotReadableError)

**步骤:**
1. 打开另一个应用使用麦克风(如Zoom、Teams)
2. 访问页面并尝试录音

**预期结果:**
- ✅ Toast提示: "麦克风被其他应用占用,请关闭后重试"
- ✅ Console显示相应错误

---

### 测试3: HTTPS安全检查

#### 场景A: HTTP环境警告

**步骤:**
1. 使用HTTP访问(非localhost):
   ```bash
   # 使用ngrok暴露本地服务
   ngrok http 5173
   ```
2. 通过ngrok提供的HTTP URL访问
3. 观察页面顶部

**预期结果:**
- ✅ 页面顶部显示橙色警告条
- ✅ 文字: "⚠️ 请使用HTTPS访问以启用语音功能"
- ✅ 警告条有向下滑入动画
- ✅ Toast提示相同内容

---

#### 场景B: HTTPS环境正常

**步骤:**
1. 使用HTTPS访问(localhost或配置SSL)
2. 观察页面

**预期结果:**
- ✅ 无警告条显示
- ✅ 可以正常使用语音功能
- ✅ Console无警告信息

---

#### 场景C: localhost视为安全

**步骤:**
1. 访问 http://localhost:5173
2. 尝试录音

**预期结果:**
- ✅ 无警告条
- ✅ 正常工作(localhost被视为安全环境)

---

### 测试4: 用户主动触发验证

#### 场景A: 正常触摸操作

**步骤:**
1. 在移动设备上打开页面(或使用Chrome DevTools的移动模拟器)
2. 按住语音按钮
3. 说话
4. 松开手指

**预期结果:**
- ✅ 按下时: 按钮放大,显示"正在录音..."
- ✅ Console显示: "🎤 开始录音"
- ✅ 松开时: 停止录音,提交内容
- ✅ Console显示: "⏹️ 停止录音"

---

#### 场景B: 防止重复启动

**步骤:**
1. 按住语音按钮开始录音
2. 在不松开的情况下,再次尝试触发(如果可能)

**预期结果:**
- ✅ 不会重复启动
- ✅ Console显示: "⚠️ 录音已在进行中"

---

#### 场景C: 程序化调用被阻止

**步骤:**
1. 在Console中执行:
   ```javascript
   // 尝试程序化触发录音
   const voiceBar = document.querySelector('.voice-bar')
   voiceBar.dispatchEvent(new Event('touchstart'))
   ```

**预期结果:**
- ✅ 由于没有真实用户交互,应该不会启动录音
- ✅ 或者启动但立即停止(取决于浏览器安全策略)

---

## 🔍 详细测试矩阵

| 测试项 | Chrome | Firefox | Safari | Edge | 微信 |
|--------|--------|---------|--------|------|------|
| 兼容性检查 | ✅ | ✅ | ✅ | ✅ | ⚠️ |
| 权限请求 | ✅ | ✅ | ✅ | ✅ | ⚠️ |
| HTTPS警告 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 触摸触发 | ✅ | ✅ | ✅ | ✅ | ✅ |
| 鼠标触发 | ✅ | ✅ | N/A | ✅ | N/A |
| 错误提示 | ✅ | ✅ | ✅ | ✅ | ⚠️ |

**说明:**
- ✅ 完全支持
- ⚠️ 部分支持或需要额外配置
- ❌ 不支持

---

## 📱 移动端专项测试

### iOS Safari测试

**步骤:**
1. 在iPhone上访问 https://your-domain.com
2. 尝试语音录入

**注意事项:**
- iOS Safari要求必须是HTTPS
- 首次使用会弹出权限请求
- SpeechRecognition可能不支持,应切换到键盘模式

**预期结果:**
- ✅ 如果是HTTPS,能正常请求权限
- ✅ 如果不支持SpeechRecognition,自动切换到键盘模式
- ✅ 显示相应提示

---

### Android Chrome测试

**步骤:**
1. 在Android设备上访问
2. 尝试语音录入

**预期结果:**
- ✅ 完全支持所有功能
- ✅ 语音识别正常工作
- ✅ 权限管理正常

---

## 🐛 常见问题排查

### 问题1: 看不到HTTPS警告

**原因:** localhost被视为安全环境

**解决:** 
- 使用ngrok或其他工具创建HTTP URL
- 或部署到测试服务器使用HTTP

---

### 问题2: 权限请求不弹出

**原因:** 
- 浏览器已记住之前的选择
- 或在iframe中被阻止

**解决:**
- 清除浏览器权限设置
- 或在地址栏点击锁图标重置权限

---

### 问题3: 录音无法启动

**检查清单:**
1. Console是否有错误?
2. 是否通过了HTTPS检查?
3. 是否获取了麦克风权限?
4. recognition对象是否正确初始化?

**调试命令:**
```javascript
// 在Console中检查
console.log('isSecureContext:', window.isSecureContext)
console.log('protocol:', location.protocol)
console.log('mediaDevices:', !!navigator.mediaDevices)
console.log('SpeechRecognition:', !!(window.SpeechRecognition || window.webkitSpeechRecognition))
```

---

### 问题4: 语音识别不工作

**可能原因:**
- 网络问题(语音识别需要联网)
- 语言设置问题
- 浏览器不支持

**解决:**
- 检查网络连接
- 确认recognition.lang设置为'zh-CN'
- 查看Console中的错误信息

---

## 📊 测试结果记录表

| 测试日期 | 测试人员 | 浏览器 | 测试结果 | 问题描述 | 状态 |
|---------|---------|--------|---------|---------|------|
| 2026-04-26 | | Chrome 120 | | | □ 通过 / □ 失败 |
| 2026-04-26 | | Firefox 121 | | | □ 通过 / □ 失败 |
| 2026-04-26 | | Safari 17 | | | □ 通过 / □ 失败 |
| 2026-04-26 | | iOS Safari | | | □ 通过 / □ 失败 |
| 2026-04-26 | | Android Chrome | | | □ 通过 / □ 失败 |

---

## 🎯 验收标准

所有测试必须满足以下条件才算通过:

### 功能性
- [ ] 浏览器兼容性检查正常工作
- [ ] 6种错误类型都有正确的中文提示
- [ ] HTTPS警告在非安全环境显示
- [ ] 只在用户主动触摸/点击时触发录音

### 用户体验
- [ ] 所有提示均为中文
- [ ] Toast提示位置合理(top)
- [ ] 提示持续时间合适(3-5秒)
- [ ] 降级策略有效(不支持时切换到键盘)

### 安全性
- [ ] HTTP环境明确警告
- [ ] 权限请求流程清晰
- [ ] 资源正确清理(媒体流)
- [ ] 状态管理正确(防重复)

### 性能
- [ ] 初始化时间 < 2秒
- [ ] 录音响应时间 < 100ms
- [ ] 无内存泄漏
- [ ] 无控制台错误(除了预期的)

---

## 🚀 自动化测试建议

### Jest单元测试示例

```javascript
import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import VoiceInputBar from '@/components/VoiceInputBar.vue'

describe('VoiceInputBar - 兼容性检查', () => {
  it('应该检测不支持的浏览器', async () => {
    // Mock navigator.mediaDevices为undefined
    Object.defineProperty(navigator, 'mediaDevices', {
      value: undefined,
      writable: true
    })
    
    const wrapper = mount(VoiceInputBar)
    
    // 等待onMounted执行
    await wrapper.vm.$nextTick()
    
    // 应该切换到键盘模式
    expect(wrapper.vm.isKeyboardMode).toBe(true)
  })
})

describe('VoiceInputBar - HTTPS检查', () => {
  it('应该在HTTP环境显示警告', async () => {
    // Mock location
    global.window = Object.create(window)
    Object.defineProperty(window, 'location', {
      value: {
        protocol: 'http:',
        hostname: 'example.com'
      }
    })
    
    const wrapper = mount(VoiceInputBar)
    await wrapper.vm.$nextTick()
    
    // 应该显示警告
    expect(wrapper.vm.isSecureContext).toBe(false)
    expect(wrapper.find('.https-warning').exists()).toBe(true)
  })
})
```

---

## 📝 测试报告模板

```markdown
# VoiceInputBar 测试报告

**测试日期:** _____________
**测试人员:** _____________
**测试环境:** _____________

## 测试结果汇总

- 兼容性检查: □ 通过 □ 失败
- 错误处理: □ 通过 □ 失败
- HTTPS检查: □ 通过 □ 失败
- 用户触发: □ 通过 □ 失败

## 发现的问题

1. ________________________________
2. ________________________________
3. ________________________________

## 建议

_________________________________
_________________________________

## 结论

□ 可以发布
□ 需要修复后重新测试
□ 需要重大修改

签名: _____________
```

---

**测试文档版本:** 1.0  
**最后更新:** 2026-04-26  
**相关文档:** [VOICE_INPUT_FIX_REPORT.md](./VOICE_INPUT_FIX_REPORT.md)
