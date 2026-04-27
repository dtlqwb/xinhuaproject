<template>
  <div class="customers-page">
    <van-nav-bar title="客户管理" left-arrow @click-left="$router.back()" />
    
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <van-field
        v-model="searchText"
        placeholder="搜索客户姓名/公司/手机"
        clearable
        @input="handleSearch"
      >
        <template #left-icon>
          <van-icon name="search" />
        </template>
      </van-field>
    </div>
    
    <!-- 客户列表 -->
    <div class="customer-list" v-loading="loading">
      <van-cell-group v-if="filteredCustomers.length > 0">
        <van-cell
          v-for="customer in filteredCustomers"
          :key="customer.id"
          :title="customer.name || '未命名'"
          :label="`${customer.company || ''} ${customer.phone || ''}`"
          is-link
          @click="showDetail(customer)"
        >
          <template #right-icon>
            <van-tag :type="getStatusType(customer.status)">
              {{ getStatusText(customer.status) }}
            </van-tag>
          </template>
        </van-cell>
      </van-cell-group>
      
      <van-empty v-else description="暂无客户数据" />
    </div>
    
    <!-- 客户详情弹窗 -->
    <van-popup v-model:show="detailVisible" position="bottom" round>
      <div v-if="currentCustomer" class="detail-content">
        <div class="detail-header">
          <h3>{{ currentCustomer.name || '未命名客户' }}</h3>
          <van-icon name="cross" @click="detailVisible = false" />
        </div>
        <van-cell-group>
          <van-cell title="公司" :value="currentCustomer.company || '-'" />
          <van-cell title="职位" :value="currentCustomer.position || '-'" />
          <van-cell title="手机" :value="currentCustomer.phone || '-'" />
          <van-cell title="行业" :value="currentCustomer.industry || '-'" />
          <van-cell title="来源" :value="getSourceText(currentCustomer.sourceType)" />
          <van-cell title="状态" :value="getStatusText(currentCustomer.status)" />
          <van-cell title="需求" :value="currentCustomer.requirement || '-'" :label="currentCustomer.content" />
          <van-cell title="录入时间" :value="formatTime(currentCustomer.createTime)" />
        </van-cell-group>
        
        <div v-if="currentCustomer.attachmentCount > 0" class="attachment-info">
          <van-icon name="paper-plane" />
          <span>附件: {{ currentCustomer.attachmentCount }} 个</span>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import request from '@/utils/request'

interface Customer {
  id: number
  name?: string
  phone?: string
  company?: string
  position?: string
  industry?: string
  sourceType?: string
  content?: string
  requirement?: string
  status?: string
  salesId?: number
  attachmentCount?: number
  createTime?: string
}

const loading = ref(false)
const searchText = ref('')
const customers = ref<Customer[]>([])
const detailVisible = ref(false)
const currentCustomer = ref<Customer | null>(null)

// 加载客户列表
const loadCustomers = async () => {
  loading.value = true
  try {
    const response = await request.get<any>('/admin/customers')
    customers.value = response || []
  } catch (error: any) {
    console.error('加载客户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 过滤客户
const filteredCustomers = computed(() => {
  if (!searchText.value) return customers.value
  
  const keyword = searchText.value.toLowerCase()
  return customers.value.filter(c => 
    (c.name && c.name.toLowerCase().includes(keyword)) ||
    (c.company && c.company.toLowerCase().includes(keyword)) ||
    (c.phone && c.phone.includes(keyword))
  )
})

// 搜索
const handleSearch = () => {
  // 前端过滤即可
}

// 显示详情
const showDetail = (customer: Customer) => {
  currentCustomer.value = customer
  detailVisible.value = true
}

// 状态文本
const getStatusText = (status?: string) => {
  const map: Record<string, string> = {
    'NEW': '新录入',
    'FOLLOW_UP': '跟进中',
    'CONVERTED': '已转化',
    'LOST': '已流失'
  }
  return map[status || ''] || '新录入'
}

// 状态类型
const getStatusType = (status?: string) => {
  const map: Record<string, any> = {
    'NEW': 'primary',
    'FOLLOW_UP': 'warning',
    'CONVERTED': 'success',
    'LOST': 'danger'
  }
  return map[status || ''] || 'primary'
}

// 来源文本
const getSourceText = (sourceType?: string) => {
  const map: Record<string, string> = {
    'VOICE': '语音录入',
    'TEXT': '文字录入',
    'IMPORT': '导入'
  }
  return map[sourceType || ''] || '未知'
}

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 初始化
loadCustomers()
</script>

<style scoped>
.customers-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  background: white;
  padding: 8px;
}

.customer-list {
  padding: 8px;
}

.detail-content {
  padding: 20px 16px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
}

.attachment-info {
  margin-top: 16px;
  padding: 12px;
  background: #f0f0ff;
  border-radius: 8px;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
<script setup lang="ts"></script>
<style scoped>.page { min-height: 100vh; background: #f5f5f5; }</style>
