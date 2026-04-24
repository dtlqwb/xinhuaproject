<template>
  <div class="voice-input-bar">
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

let recognition: any = null

// 初始化语音识别
onMounted(() => {
  if ('SpeechRecognition' in window || 'webkitSpeechRecognition' in window) {
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
      showToast('语音识别失败，请重试')
      isRecording.value = false
    }
  } else {
    showToast('您的浏览器不支持语音识别')
  }
})

// 开始录音
const startRecording = () => {
  if (!recognition) {
    showToast('语音识别不可用')
    return
  }
  
  try {
    recognition.start()
    isRecording.value = true
    displayText.value = ''
  } catch (error) {
    console.error('启动录音失败:', error)
  }
}

// 停止录音并提交
const stopRecording = async () => {
  if (!recognition) return
  
  try {
    recognition.stop()
    isRecording.value = false
    
    if (!displayText.value.trim()) {
      showToast('未检测到语音内容')
      return
    }
    
    // 提交客户信息
    await submitCustomer(displayText.value)
  } catch (error) {
    console.error('停止录音失败:', error)
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
