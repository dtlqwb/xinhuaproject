<template>
  <div class="dashboard">
    <van-nav-bar title="管理后台">
      <template #right>
        <van-icon name="cross" size="20" @click="handleLogout" />
      </template>
    </van-nav-bar>
    
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
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '@/utils/request'
import { useTable } from '@/hooks/useTable'

interface DashboardStats {
  totalCustomers: number
  todayCustomers: number
  totalReports: number
  pendingPlans: number
}

const router = useRouter()
const active = ref(0)
const stats = ref<DashboardStats>({
  totalCustomers: 0,
  todayCustomers: 0,
  totalReports: 0,
  pendingPlans: 0
})

const loading = ref(false)

onMounted(() => {
  loadStats()
})

const loadStats = async () => {
  loading.value = true
  try {
    const data = await request.get('/admin/dashboard')
    if (!data || typeof data !== 'object') {
      showToast('数据格式错误')
      return
    }
    stats.value = data
  } catch (error: any) {
    console.error('加载统计失败', error)
    const errorMsg = error.message || '加载失败'
    showToast(errorMsg)
  } finally {
    loading.value = false
  }
}

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('admin_token')
  showToast('已退出登录')
  router.push('/login')
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
