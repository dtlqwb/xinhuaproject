<template>
  <div class="dashboard">
    <van-nav-bar title="管理后台" />
    
    <div class="stats-grid">
      <div class="stat-card" @click="$router.push('/customers')">
        <div class="stat-value">{{ stats.totalCustomers }}</div>
        <div class="stat-label">总客户数</div>
      </div>
      <div class="stat-card" @click="$router.push('/customers')">
        <div class="stat-value">{{ stats.todayCustomers }}</div>
        <div class="stat-label">今日新增</div>
      </div>
      <div class="stat-card" @click="$router.push('/reports')">
        <div class="stat-value">{{ stats.totalReports }}</div>
        <div class="stat-label">日报总数</div>
      </div>
      <div class="stat-card" @click="$router.push('/plans')">
        <div class="stat-value">{{ stats.pendingPlans }}</div>
        <div class="stat-label">待执行方案</div>
      </div>
    </div>

    <van-tabbar v-model="active" route>
      <van-tabbar-item icon="home-o" to="/dashboard">首页</van-tabbar-item>
      <van-tabbar-item icon="friends-o" to="/customers">客户</van-tabbar-item>
      <van-tabbar-item icon="notes-o" to="/reports">日报</van-tabbar-item>
      <van-tabbar-item icon="todo-list-o" to="/plans">方案</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import request from '@/utils/request'

const active = ref(0)
const stats = ref({
  totalCustomers: 0,
  todayCustomers: 0,
  totalReports: 0,
  pendingPlans: 0
})

onMounted(() => {
  loadStats()
})

const loadStats = async () => {
  try {
    const data = await request.get('/admin/dashboard')
    stats.value = data
  } catch (error) {
    console.error('加载统计失败', error)
  }
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 60px;
}
.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  padding: 16px;
}
.stat-card {
  background: white;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #667eea;
  margin-bottom: 8px;
}
.stat-label {
  font-size: 14px;
  color: #666;
}
</style>
