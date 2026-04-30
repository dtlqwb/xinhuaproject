<template>
  <div class="customer-card" @click="showDetail">
    <div class="card-header">
      <div class="avatar">👤</div>
      <div class="info">
        <div class="name-row">
          <span class="name">{{ customer.name || '未命名客户' }}</span>
          <span v-if="customer.position" class="position-tag">{{ customer.position }}</span>
        </div>
        <div v-if="customer.company" class="company">{{ customer.company }}</div>
      </div>
    </div>
    
    <div v-if="customer.phone" class="contact-info">
      <span class="icon">📱</span>
      <span class="phone">{{ customer.phone }}</span>
    </div>
    
    <div v-if="customer.requirement" class="requirement">
      <div class="label">💡 需求</div>
      <div class="content">{{ customer.requirement }}</div>
    </div>
    
    <div v-if="customer.attachmentCount && customer.attachmentCount > 0" class="attachments">
      <span class="attach-icon">📎</span>
      <span class="attach-count">{{ customer.attachmentCount }}个附件</span>
    </div>
    
    <div class="time-info">
      {{ formatTime(customer.createTime) }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { showDialog } from 'vant'
import type { Customer } from '@/api/customer'

defineProps<{
  customer: Customer
}>()

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return ''
  const date = new Date(time)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

const showDetail = () => {
  const props = defineProps<{ customer: Customer }>()
  const customer = props.customer
  
  let content = `姓名：${customer.name || '未填写'}\n`
  content += `公司：${customer.company || '未填写'}\n`
  content += `职位：${customer.position || '未填写'}\n`
  content += `电话：${customer.phone || '未填写'}\n`
  
  if (customer.requirement) {
    content += `\n需求：${customer.requirement}`
  }
  
  if (customer.content) {
    content += `\n\n原始内容：${customer.content}`
  }
  
  showDialog({
    title: '客户详情',
    message: content,
    confirmButtonText: '关闭'
  })
}
</script>

<style scoped>
.customer-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: all 0.3s ease;
  animation: slideIn 0.3s ease;
  border-left: 3px solid #667eea;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.customer-card:active {
  transform: scale(0.98);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.info {
  flex: 1;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.position-tag {
  font-size: 11px;
  padding: 2px 8px;
  background: #f0f0ff;
  color: #667eea;
  border-radius: 10px;
}

.company {
  font-size: 13px;
  color: #666;
}

.contact-info {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 0;
  border-top: 1px solid #f5f5f5;
}

.icon {
  font-size: 14px;
}

.phone {
  font-size: 13px;
  color: #666;
}

.requirement {
  margin-top: 8px;
  padding: 10px;
  background: #fafafa;
  border-radius: 8px;
}

.label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.content {
  font-size: 13px;
  color: #333;
  line-height: 1.5;
}

.attachments {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.attach-icon {
  font-size: 14px;
}

.attach-count {
  font-size: 12px;
  color: #667eea;
}

.time-info {
  margin-top: 8px;
  font-size: 11px;
  color: #ccc;
  text-align: right;
}
</style>
