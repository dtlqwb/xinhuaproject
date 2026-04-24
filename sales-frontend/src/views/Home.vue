<template>
  <div class="home-page">
    <!-- 顶部栏 -->
    <div class="header">
      <div class="title">今日客户录入</div>
      <div class="count" @click="showCountTip">
        今日 {{ todayCount }} 个
      </div>
    </div>
    
    <!-- 客户列表区域 -->
    <div class="customer-list" ref="customerListRef">
      <CustomerCard
        v-for="customer in customerStore.todayCustomers"
        :key="customer.id"
        :customer="customer"
      />
      
      <div v-if="customerStore.todayCustomers.length === 0" class="empty-tip">
        <van-empty description="暂无客户，请在下方录入" />
      </div>
    </div>
    
    <!-- 底部固定输入区 -->
    <VoiceInputBar @customer-added="handleCustomerAdded" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast, showNotify } from 'vant'
import { useUserStore } from '@/stores/user'
import { getCustomers } from '@/api/customer'
import { getTodayCount } from '@/api/sales'
import CustomerCard from '@/components/CustomerCard.vue'
import VoiceInputBar from '@/components/VoiceInputBar.vue'

const customerStore = useUserStore()
const todayCount = ref(0)
const customerListRef = ref<HTMLElement>()

// 加载今日客户
const loadCustomers = async () => {
  if (!customerStore.userInfo) return
  
  try {
    const customers = await getCustomers(customerStore.userInfo.id)
    customerStore.loadTodayCustomers(customers)
    
    // 获取今日数量
    const data = await getTodayCount(customerStore.userInfo.id)
    todayCount.value = data.count
  } catch (error: any) {
    showToast('加载失败')
  }
}

// 处理新增客户
const handleCustomerAdded = () => {
  loadCustomers()
  
  // 滚动到顶部
  setTimeout(() => {
    if (customerListRef.value) {
      customerListRef.value.scrollTop = 0
    }
  }, 100)
}

// 显示数量提示
const showCountTip = () => {
  showNotify({ 
    type: 'primary', 
    message: `今日已录入 ${todayCount.value} 个客户` 
  })
}

onMounted(() => {
  loadCustomers()
})
</script>

<style scoped>
.home-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.count {
  font-size: 14px;
  color: #667eea;
  padding: 6px 12px;
  background: #f0f0ff;
  border-radius: 16px;
  cursor: pointer;
}

.customer-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  padding-bottom: 100px;
}

.empty-tip {
  margin-top: 100px;
}
</style>
