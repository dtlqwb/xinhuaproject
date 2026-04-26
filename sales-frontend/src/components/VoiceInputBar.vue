<template>
  <div class="voice-input-bar">
    <!-- HTTPS警告提示 -->
    <div v-if="!isSecureContext" class="https-warning">
      ⚠️ 请使用HTTPS访问以启用语音功能
    </div>
    
    <!-- 附件按钮 -->
    <button class="attach-btn" @click="handleAttachment">
      <span class="icon">+</span>
    </button>
    
    <!-- 语音/文字输入区 -->
    <div 
      v-if="!isKeyboardMode"
      class="voice-bar"
      :class="{ recording: isRecording }"
      @touchstart.prevent="startRecording"
      @touchend.prevent="stopRecording"
      @mousedown="startRecording"
      @mouseup="stopRecording"
    >
      <span v-if="!displayText && !isRecording" class="placeholder">
        🎤 按住说话录入客户信息
      </span>
      <span v-else class="text">{{ displayText || '正在录音...' }}</span>
    </div>
    
    <!-- 文字输入模式 -->
    <div v-else class="text-input-mode">
      <input 
        v-model="textInput"
        type="text"
        placeholder="输入客户信息，如：张三，某某公司，经理，138..."
        @keyup.enter="submitText"
      />
      <button class="send-btn" @click="submitText">发送</button>
    </div>
    
    <!-- 键盘切换按钮 -->
    <button class="keyboard-btn" @click="toggleKeyboard">
      {{ isKeyboardMode ? '🎤' : '⌨️' }}
    </button>
    
    <!-- 附件预览 -->
    <div v-if="attachments.length > 0" class="attachment-preview">
      <span class="attach-count">📎 {{ attachments.length }}个附件</span>
    </div>
    
    <!-- 隐藏的文件输入 -->
    <input 
      ref="fileInputRef"
      type="file"
      multiple
      accept="image/*,.pdf,.doc,.docx,.xls,.xlsx"
      style="display: none"
      @change="handleFileChange"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { addCustomer } from '@/api/customer'
import { useUserStore } from '@/stores/user'

const emit = defineEmits(['customer-added'])

const customerStore = useUserStore()

const isRecording = ref(false)
const displayText = ref('')
const isKeyboardMode = ref(false)
const textInput = ref('')
const attachments = ref<File[]>([])
const fileInputRef = ref<HTMLInputElement>()
const isSecureContext = ref(true) // HTTPS环境检查

let recognition: any = null
let mediaStream: MediaStream | null = null

// 检查HTTPS环境
const checkSecureContext = () => {
  const isSecure = window.isSecureContext || 
                   location.protocol === 'https:' || 
                   location.hostname === 'localhost' ||
                   location.hostname === '127.0.0.1'
  
  // 更新响应式变量
  isSecureContext.value = isSecure
  
  if (!isSecure) {
    console.warn('⚠️ 非安全环境: 麦克风功能需要HTTPS或localhost')
    showToast({
      message: '请使用HTTPS访问以启用语音功能',
      duration: 5000,
      position: 'top'
    })
    return false
  }
  return true
}

// 检查浏览器兼容性
const checkBrowserCompatibility = () => {
  // 检查 navigator.mediaDevices
  if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
    console.error('❌ 浏览器不支持 getUserMedia API')
    showToast({
      message: '您的浏览器不支持录音功能，请使用键盘输入',
      duration: 3000
    })
    isKeyboardMode.value = true
    return false
  }
  
  // 检查 SpeechRecognition
  if (!('SpeechRecognition' in window) && !('webkitSpeechRecognition' in window)) {
    console.error('❌ 浏览器不支持语音识别')
    showToast({
      message: '浏览器不支持语音识别，请使用键盘输入',
      duration: 3000
    })
    isKeyboardMode.value = true
    return false
  }
  
  return true
}

// 请求麦克风权限
const requestMicrophonePermission = async (): Promise<boolean> => {
  try {
    // 先检查是否是安全环境
    if (!checkSecureContext()) {
      return false
    }
    
    // 请求麦克风权限
    mediaStream = await navigator.mediaDevices.getUserMedia({ audio: true })
    console.log('✅ 麦克风权限获取成功')
    
    // 立即停止流，因为我们只需要权限
    mediaStream.getTracks().forEach(track => track.stop())
    mediaStream = null
    
    return true
  } catch (error: any) {
    console.error('❌ 麦克风权限请求失败:', error.name, error.message)
    
    // 针对不同错误类型给出中文提示
    switch (error.name) {
      case 'NotAllowedError':
      case 'PermissionDeniedError':
        showToast({
          message: '麦克风权限被拒绝，请在浏览器设置中允许使用麦克风',
          duration: 4000,
          position: 'top'
        })
        break
      
      case 'NotFoundError':
      case 'DevicesNotFoundError':
        showToast({
          message: '未检测到麦克风设备，请检查硬件连接',
          duration: 4000,
          position: 'top'
        })
        break
      
      case 'NotReadableError':
      case 'TrackStartError':
        showToast({
          message: '麦克风被其他应用占用，请关闭后重试',
          duration: 4000,
          position: 'top'
        })
        break
      
      case 'OverconstrainedError':
        showToast({
          message: '麦克风配置不符合要求',
          duration: 3000
        })
        break
      
      case 'SecurityError':
        showToast({
          message: '安全错误：请使用HTTPS访问',
          duration: 4000,
          position: 'top'
        })
        break
      
      default:
        showToast({
          message: `无法访问麦克风: ${error.message || '未知错误'}`,
          duration: 4000,
          position: 'top'
        })
    }
    
    return false
  }
}

// 初始化语音识别
onMounted(async () => {
  // 1. 检查浏览器兼容性
  if (!checkBrowserCompatibility()) {
    return
  }
  
  // 2. 请求麦克风权限
  const hasPermission = await requestMicrophonePermission()
  if (!hasPermission) {
    console.warn('⚠️ 麦克风权限获取失败，切换到键盘模式')
    isKeyboardMode.value = true
    return
  }
  
  // 3. 初始化语音识别
  try {
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition
    recognition = new SpeechRecognition()
    recognition.lang = 'zh-CN'
    recognition.continuous = true
    recognition.interimResults = true
    
    recognition.onresult = (event: any) => {
      let interimTranscript = ''
      for (let i = event.resultIndex; i < event.results.length; i++) {
        interimTranscript += event.results[i][0].transcript
      }
      displayText.value = interimTranscript
    }
    
    recognition.onerror = (event: any) => {
      console.error('语音识别错误:', event.error)
      isRecording.value = false
      
      // 针对不同错误类型给出提示
      switch (event.error) {
        case 'not-allowed':
          showToast({
            message: '麦克风权限被拒绝，请允许使用麦克风',
            duration: 3000
          })
          break
        case 'network':
          showToast('网络错误，请检查网络连接')
          break
        case 'no-speech':
          showToast('未检测到语音，请重试')
          break
        case 'audio-capture':
          showToast({
            message: '未找到麦克风设备',
            duration: 3000
          })
          break
        case 'service-not-allowed':
          showToast({
            message: '语音识别服务不可用，请使用HTTPS',
            duration: 3000
          })
          break
        default:
          showToast(`语音识别失败: ${event.error}`)
      }
    }
    
    recognition.onend = () => {
      // 录音结束事件，确保状态重置
      if (isRecording.value) {
        isRecording.value = false
      }
    }
    
    console.log('✅ 语音识别初始化成功')
  } catch (error: any) {
    console.error('❌ 语音识别初始化失败:', error)
    showToast('语音识别初始化失败，请使用键盘输入')
    isKeyboardMode.value = true
  }
})

// 开始录音 - 只在用户主动点击时触发
const startRecording = (event?: TouchEvent | MouseEvent) => {
  // 验证是用户主动触发
  if (event) {
    event.preventDefault()
    event.stopPropagation()
  }
  
  if (!recognition) {
    showToast('语音识别不可用，请使用键盘输入')
    isKeyboardMode.value = true
    return
  }
  
  // 防止重复启动
  if (isRecording.value) {
    console.warn('⚠️ 录音已在进行中')
    return
  }
  
  try {
    recognition.start()
    isRecording.value = true
    displayText.value = ''
    console.log('🎤 开始录音')
  } catch (error: any) {
    console.error('❌ 启动录音失败:', error)
    
    // 如果已经在录音，忽略错误
    if (error.name === 'InvalidStateError') {
      console.warn('录音状态异常，已重置')
      isRecording.value = false
    } else {
      showToast('启动录音失败，请重试')
    }
  }
}

// 停止录音并提交 - 确保是用户主动释放
const stopRecording = async (event?: TouchEvent | MouseEvent) => {
  // 验证是用户主动触发
  if (event) {
    event.preventDefault()
    event.stopPropagation()
  }
  
  if (!recognition || !isRecording.value) {
    console.warn('⚠️ 录音未在运行')
    return
  }
  
  try {
    recognition.stop()
    isRecording.value = false
    console.log('⏹️ 停止录音')
    
    // 等待一小段时间让识别结果处理完成
    await new Promise(resolve => setTimeout(resolve, 300))
    
    if (!displayText.value.trim()) {
      showToast('未检测到语音内容，请重试')
      return
    }
    
    // 提交客户信息
    await submitCustomer(displayText.value)
  } catch (error: any) {
    console.error('❌ 停止录音失败:', error)
    isRecording.value = false
    showToast('录音处理失败，请重试')
  }
}

// 切换键盘模式
const toggleKeyboard = () => {
  isKeyboardMode.value = !isKeyboardMode.value
}

// 提交文字输入
const submitText = async () => {
  if (!textInput.value.trim()) {
    showToast('请输入内容')
    return
  }
  
  await submitCustomer(textInput.value)
  textInput.value = ''
}

// 提交客户信息
const submitCustomer = async (content: string) => {
  if (!customerStore.userInfo) {
    showToast('请先登录')
    return
  }
  
  showLoadingToast({
    message: '正在保存...',
    forbidClick: true,
    duration: 0
  })
  
  try {
    const formData = new FormData()
    formData.append('content', content)
    formData.append('salesId', String(customerStore.userInfo.id))
    
    // 添加附件
    attachments.value.forEach((file, index) => {
      formData.append('attachments', file)
    })
    
    const customer = await addCustomer(formData)
    customerStore.addCustomer(customer)
    
    // 清空状态
    displayText.value = ''
    attachments.value = []
    
    closeToast()
    showToast('添加成功')
    emit('customer-added')
  } catch (error: any) {
    closeToast()
    showToast(error.message || '添加失败')
  }
}

// 处理附件上传
const handleAttachment = () => {
  fileInputRef.value?.click()
}

// 处理文件选择
const handleFileChange = (event: Event) => {
  const target = event.target as HTMLInputElement
  const files = target.files
  
  if (files && files.length > 0) {
    attachments.value.push(...Array.from(files))
    showToast(`已选择 ${files.length} 个文件`)
  }
  
  // 清空input，允许重复选择同一文件
  target.value = ''
}
</script>

<style scoped>
.voice-input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  padding: 12px 16px;
  z-index: 100;
}

/* HTTPS警告样式 */
.https-warning {
  position: absolute;
  top: -40px;
  left: 0;
  right: 0;
  background: #ff9800;
  color: white;
  text-align: center;
  padding: 8px;
  font-size: 13px;
  font-weight: bold;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    transform: translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.attach-btn {
  position: absolute;
  left: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  border: none;
  background: #f0f0ff;
  border-radius: 50%;
  font-size: 24px;
  color: #667eea;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.voice-bar {
  margin-left: 50px;
  margin-right: 50px;
  padding: 14px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  color: white;
  text-align: center;
  font-size: 16px;
  user-select: none;
  transition: all 0.3s ease;
  min-height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.voice-bar.recording {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.placeholder {
  opacity: 0.9;
}

.text {
  word-break: break-all;
}

.text-input-mode {
  margin-left: 50px;
  margin-right: 50px;
  display: flex;
  gap: 8px;
}

.text-input-mode input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid #ddd;
  border-radius: 24px;
  font-size: 14px;
  outline: none;
}

.text-input-mode input:focus {
  border-color: #667eea;
}

.send-btn {
  padding: 12px 20px;
  background: #667eea;
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 14px;
  cursor: pointer;
}

.keyboard-btn {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  border: none;
  background: #f5f5f5;
  border-radius: 50%;
  font-size: 18px;
  cursor: pointer;
}

.attachment-preview {
  margin-top: 8px;
  text-align: center;
}

.attach-count {
  font-size: 12px;
  color: #667eea;
  background: #f0f0ff;
  padding: 4px 12px;
  border-radius: 12px;
}
</style>
