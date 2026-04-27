<template>
  <view class="home-page">
    <!-- 顶部栏 -->
    <view class="header">
      <view class="left-section">
        <text class="title">今日客户录入</text>
        <text class="user-name" v-if="userStore.userInfo">
          👤 {{ userStore.userInfo.name }}
        </text>
      </view>
      <view class="right-section">
        <view class="count" @click="showCountTip">
          今日 {{ todayCount }} 个
        </view>
        <button class="logout-btn" @click="handleLogout">
          退出
        </button>
      </view>
    </view>
    
    <!-- 客户列表区域 -->
    <scroll-view class="customer-list" scroll-y>
      <view 
        v-for="customer in userStore.todayCustomers" 
        :key="customer.id"
        class="customer-card"
      >
        <view class="card-header">
          <text class="name">{{ customer.name || '未命名' }}</text>
          <text class="company">{{ customer.company || '' }}</text>
        </view>
        <view class="card-body">
          <text class="phone">{{ customer.phone || '-' }}</text>
          <text class="position">{{ customer.position || '-' }}</text>
        </view>
      </view>
      
      <view v-if="userStore.todayCustomers.length === 0" class="empty-tip">
        <text>暂无客户，请在下方录入</text>
      </view>
    </scroll-view>
    
    <!-- 底部固定输入区 -->
    <view class="input-bar">
      <textarea 
        class="input-textarea" 
        v-model="inputText" 
        placeholder="请输入客户信息..."
        auto-height
        maxlength="500"
      />
      <button class="submit-btn" @click="handleSubmit" :loading="submitting">
        {{ submitting ? '提交中...' : '提交' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import Taro from '@tarojs/taro'
import { useUserStore } from '@/stores/user'
import { getCustomers, addCustomer } from '@/api/customer'
import { getTodayCount } from '@/api/sales'

const userStore = useUserStore()
const todayCount = ref(0)
const inputText = ref('')
const submitting = ref(false)

// 加载今日客户
const loadCustomers = async () => {
  if (!userStore.userInfo) return
  
  try {
    const customers = await getCustomers(userStore.userInfo.id)
    userStore.loadTodayCustomers(customers)
    
    // 获取今日数量
    const data = await getTodayCount(userStore.userInfo.id)
    todayCount.value = data.count
  } catch (error: any) {
    Taro.showToast({ title: '加载失败', icon: 'none' })
  }
}

// 处理提交
const handleSubmit = async () => {
  if (!inputText.value.trim()) {
    Taro.showToast({ title: '请输入客户信息', icon: 'none' })
    return
  }
  
  if (!userStore.userInfo) {
    Taro.showToast({ title: '请先登录', icon: 'none' })
    return
  }
  
  submitting.value = true
  
  try {
    // TODO: 这里应该调用AI解析接口,将文本解析为结构化数据
    // 暂时直接保存原始文本
    await addCustomer({
      salesId: userStore.userInfo.id,
      content: inputText.value,
      name: '',
      phone: '',
      company: '',
      position: '',
      industry: '',
      sourceType: 'manual',
      status: 'new',
      requirement: ''
    })
    
    Taro.showToast({ title: '提交成功', icon: 'success' })
    inputText.value = ''
    
    // 重新加载客户列表
    await loadCustomers()
  } catch (error: any) {
    Taro.showToast({ title: error.message || '提交失败', icon: 'none' })
  } finally {
    submitting.value = false
  }
}

// 显示数量提示
const showCountTip = () => {
  Taro.showToast({ 
    title: `今日已录入 ${todayCount.value} 个客户`,
    icon: 'none',
    duration: 2000
  })
}

// 退出登录
const handleLogout = () => {
  Taro.showModal({
    title: '确认退出',
    content: '确定要退出登录吗？',
    success: (res) => {
      if (res.confirm) {
        userStore.clearUserInfo()
        Taro.showToast({ title: '已退出登录', icon: 'success' })
        Taro.reLaunch({ url: '/pages/login/index' })
      }
    }
  })
}

onMounted(() => {
  loadCustomers()
})
</script>

<style lang="scss">
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
  padding: 32rpx;
  background: white;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .left-section {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }
  
  .right-section {
    display: flex;
    align-items: center;
    gap: 24rpx;
  }
  
  .user-name {
    font-size: 24rpx;
    color: #999;
  }
  
  .logout-btn {
    padding: 12rpx 24rpx;
    font-size: 24rpx;
    color: #ff6b6b;
    background: #fff0f0;
    border: 2rpx solid #ffd4d4;
    border-radius: 32rpx;
    line-height: 1;
  }
  
  .title {
    font-size: 36rpx;
    font-weight: bold;
    color: #333;
  }
  
  .count {
    font-size: 28rpx;
    color: #667eea;
    padding: 12rpx 24rpx;
    background: #f0f0ff;
    border-radius: 32rpx;
  }
}

.customer-list {
  flex: 1;
  padding: 32rpx;
  padding-bottom: 200rpx;
  
  .customer-card {
    background: white;
    border-radius: 16rpx;
    padding: 24rpx;
    margin-bottom: 24rpx;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16rpx;
      
      .name {
        font-size: 32rpx;
        font-weight: bold;
        color: #333;
      }
      
      .company {
        font-size: 28rpx;
        color: #666;
      }
    }
    
    .card-body {
      display: flex;
      justify-content: space-between;
      
      .phone {
        font-size: 28rpx;
        color: #667eea;
      }
      
      .position {
        font-size: 28rpx;
        color: #999;
      }
    }
  }
  
  .empty-tip {
    text-align: center;
    padding: 200rpx 0;
    color: #999;
    font-size: 28rpx;
  }
}

.input-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 24rpx 32rpx;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.05);
  
  .input-textarea {
    width: 100%;
    min-height: 80rpx;
    max-height: 200rpx;
    padding: 16rpx 24rpx;
    font-size: 28rpx;
    border: 2rpx solid #e0e0e0;
    border-radius: 16rpx;
    background: #f8f8f8;
    margin-bottom: 16rpx;
  }
  
  .submit-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    font-size: 32rpx;
    border-radius: 44rpx;
    border: none;
    
    &[loading] {
      opacity: 0.7;
    }
  }
}
</style>
