<template>
  <div class="customer-card" @click="showDetail">
    <div class="card-header">
      <span class="icon">🎤</span>
      <div class="info">
        <div class="name">{{ customer.name || '未命名客户' }}</div>
        <div class="company">{{ customer.company || '未知公司' }}</div>
      </div>
    </div>
    
    <div v-if="customer.attachmentCount && customer.attachmentCount > 0" class="attachments">
      <span class="attach-icon">📎</span>
      <span class="attach-count">{{ customer.attachmentCount }}个附件</span>
    </div>
    
    <div v-if="customer.phone" class="phone">
      {{ customer.phone }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { showDialog } from 'vant'
import type { Customer } from '@/api/customer'

defineProps<{
  customer: Customer
}>()

const showDetail = () => {
  const customer = defineProps<{ customer: Customer }>().customer
  
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
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.icon {
  font-size: 24px;
}

.info {
  flex: 1;
}

.name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.company {
  font-size: 14px;
  color: #666;
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

.phone {
  margin-top: 8px;
  font-size: 13px;
  color: #999;
}
</style>
