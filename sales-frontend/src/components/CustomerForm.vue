<template>
  <div class="customer-form-bar">
    <van-form @submit="onSubmit" ref="formRef">
      <!-- 客户姓名 -->
      <van-field
        v-model="formData.name"
        name="name"
        label="客户姓名"
        placeholder="请输入客户姓名"
        :rules="[{ required: true, message: '请输入客户姓名' }]"
      />
      
      <!-- 公司名称 -->
      <van-field
        v-model="formData.company"
        name="company"
        label="公司名称"
        placeholder="请输入公司名称(选填)"
      />
      
      <!-- 职位 -->
      <van-field
        v-model="formData.position"
        name="position"
        label="职位"
        placeholder="请输入职位(选填)"
      />
      
      <!-- 手机号 -->
      <van-field
        v-model="formData.phone"
        name="phone"
        label="手机号"
        type="tel"
        placeholder="请输入手机号(选填)"
        :rules="[{ pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }]"
      />
      
      <!-- 客户需求 -->
      <van-field
        v-model="formData.requirement"
        name="requirement"
        rows="3"
        autosize
        type="textarea"
        label="客户需求"
        placeholder="请详细描述客户需求..."
        :rules="[{ required: true, message: '请输入客户需求' }]"
      />
      
      <!-- 快捷标签 -->
      <div class="quick-tags">
        <div class="tag-label">快捷标签:</div>
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
          native-type="submit"
          :loading="submitting"
          loading-text="提交中..."
        >
          提交客户信息
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { showToast } from 'vant'
import { useUserStore } from '@/stores/user'
import { addCustomer } from '@/api/customer'

const emit = defineEmits<{
  (e: 'customer-added'): void
}>()

const customerStore = useUserStore()
const formRef = ref()
const submitting = ref(false)

const formData = reactive({
  name: '',
  company: '',
  position: '',
  phone: '',
  requirement: ''
})

// 快捷标签
const quickTags = ['CRM需求', '云服务', 'AI客服', '营销自动化', 'SEO优化', '招聘系统']

// 添加标签到需求
const addTag = (tag: string) => {
  if (formData.requirement) {
    formData.requirement += `、${tag}`
  } else {
    formData.requirement = tag
  }
}

// 提交表单
const onSubmit = async () => {
  if (!customerStore.userInfo) {
    showToast('请先登录')
    return
  }
  
  try {
    submitting.value = true
    
    // 创建FormData
    const formDataObj = new FormData()
    formDataObj.append('name', formData.name)
    formDataObj.append('company', formData.company || '')
    formDataObj.append('position', formData.position || '')
    formDataObj.append('phone', formData.phone || '')
    formDataObj.append('requirement', formData.requirement)
    formDataObj.append('salesId', String(customerStore.userInfo.id))
    formDataObj.append('salesName', customerStore.userInfo.name)
    formDataObj.append('status', 'pending')
    
    await addCustomer(formDataObj)
    
    showToast('提交成功')
    
    // 重置表单
    formData.name = ''
    formData.company = ''
    formData.position = ''
    formData.phone = ''
    formData.requirement = ''
    
    // 通知父组件
    emit('customer-added')
  } catch (error: any) {
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

.quick-tags {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
}

.tag-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
}

.submit-section {
  padding: 12px 16px 16px;
  background: white;
}
</style>
