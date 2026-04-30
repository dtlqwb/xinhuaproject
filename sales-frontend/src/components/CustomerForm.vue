<template>
  <div class="customer-form-bar">
    <div class="input-area">
      <!-- 文本输入框 -->
      <van-field
        v-model="inputText"
        type="textarea"
        rows="4"
        autosize
        placeholder="请输入客户拜访记录，例如：&#10;今天拜访了阿里巴巴的张伟，他是技术总监，电话13800138000，对CRM系统很感兴趣，预算50万"
        maxlength="500"
        show-word-limit
      />
      
      <!-- 快捷标签 -->
      <div class="quick-tags">
        <span class="tag-label">常用场景:</span>
        <van-tag 
          v-for="tag in quickTags" 
          :key="tag"
          plain
          type="primary"
          size="medium"
          @click="addTag(tag)"
          style="margin-right: 8px; margin-bottom: 8px; cursor: pointer;"
        >
          {{ tag }}
        </van-tag>
      </div>
      
      <!-- 提交按钮 -->
      <div class="submit-section">
        <van-button 
          round 
          block 
          type="primary" 
          :loading="submitting"
          loading-text="AI解析中..."
          @click="handleSubmit"
        >
          {{ submitting ? 'AI智能识别中...' : '提交' }}
        </van-button>
      </div>
      
      <!-- 提示说明 -->
      <div class="tip">
        💡 AI会自动识别客户姓名、公司、职位、电话和需求
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { showToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { addCustomer } from '@/api/customer'

const emit = defineEmits<{
  (e: 'customer-added'): void
}>()

const customerStore = useUserStore()
const inputText = ref('')
const submitting = ref(false)

// 快捷标签
const quickTags = ['CRM需求', '云服务', 'AI客服', '营销自动化', 'SEO优化', '招聘系统']

// 添加标签到文本
const addTag = (tag: string) => {
  if (inputText.value) {
    inputText.value += `，需要${tag}`
  } else {
    inputText.value = `客户需要${tag}`
  }
}

// 提交表单
const handleSubmit = async () => {
  if (!customerStore.userInfo) {
    showToast('请先登录')
    return
  }
  
  if (!inputText.value.trim()) {
    showToast('请输入客户拜访记录')
    return
  }
  
  try {
    submitting.value = true
    
    // 创建FormData，将自然语言文本作为content字段
    const formDataObj = new FormData()
    formDataObj.append('content', inputText.value)
    formDataObj.append('salesId', String(customerStore.userInfo.id))
    formDataObj.append('salesName', customerStore.userInfo.name)
    formDataObj.append('status', 'pending')
    
    await addCustomer(formDataObj)
    
    showToast('✓ 提交成功')
    
    // 重置输入
    inputText.value = ''
    
    // 通知父组件刷新列表
    emit('customer-added')
  } catch (error: any) {
    console.error('提交失败:', error)
    showToast(error.message || '提交失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.customer-form-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  max-height: 70vh;
  overflow-y: auto;
  z-index: 100;
}

.input-area {
  padding: 12px 16px;
}

.quick-tags {
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.tag-label {
  font-size: 12px;
  color: #999;
  display: block;
  margin-bottom: 8px;
}

.submit-section {
  padding: 12px 0;
}

.tip {
  font-size: 12px;
  color: #999;
  text-align: center;
  padding: 8px 0;
}
</style>
