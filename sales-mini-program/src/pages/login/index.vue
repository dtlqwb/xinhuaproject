<template>
  <view class="login-page">
    <view class="login-header">
      <text class="title">智销助手</text>
      <text class="subtitle">销售人员登录</text>
    </view>
    
    <view class="login-form">
      <view class="form-item">
        <text class="label">手机号</text>
        <input 
          class="input" 
          v-model="phone" 
          type="number" 
          placeholder="请输入手机号" 
          maxlength="11"
        />
      </view>
      
      <view class="form-item">
        <text class="label">密码</text>
        <input 
          class="input" 
          v-model="password" 
          type="password" 
          placeholder="请输入密码"
        />
      </view>
      
      <button class="login-btn" @click="onSubmit" :loading="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import Taro from '@tarojs/taro'
import { login } from '@/api/sales'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const phone = ref('13800138000')
const password = ref('123456')
const loading = ref(false)

const onSubmit = async () => {
  // 验证输入
  if (!phone.value) {
    Taro.showToast({ title: '请输入手机号', icon: 'none' })
    return
  }
  
  if (!password.value) {
    Taro.showToast({ title: '请输入密码', icon: 'none' })
    return
  }
  
  loading.value = true
  
  try {
    const result = await login({
      phone: phone.value,
      password: password.value
    })
    
    userStore.setUserInfo(result)
    Taro.showToast({ title: '登录成功', icon: 'success' })
    
    // 跳转到首页(tabBar页面使用switchTab)
    setTimeout(() => {
      Taro.switchTab({ url: '/pages/index/index' })
    }, 1500)
  } catch (error: any) {
    Taro.showToast({ 
      title: error.message || '登录失败', 
      icon: 'none' 
    })
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss">
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 120rpx 40rpx;
}

.login-header {
  text-align: center;
  margin-bottom: 80rpx;
  
  .title {
    display: block;
    font-size: 64rpx;
    color: white;
    font-weight: bold;
    margin-bottom: 20rpx;
  }
  
  .subtitle {
    display: block;
    font-size: 32rpx;
    color: rgba(255, 255, 255, 0.9);
  }
}

.login-form {
  background: white;
  border-radius: 32rpx;
  padding: 40rpx;
  
  .form-item {
    margin-bottom: 40rpx;
    
    .label {
      display: block;
      font-size: 28rpx;
      color: #333;
      margin-bottom: 16rpx;
    }
    
    .input {
      width: 100%;
      height: 88rpx;
      padding: 0 24rpx;
      font-size: 32rpx;
      border: 2rpx solid #e0e0e0;
      border-radius: 16rpx;
      background: #f8f8f8;
    }
  }
  
  .login-btn {
    width: 100%;
    height: 88rpx;
    line-height: 88rpx;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    font-size: 32rpx;
    border-radius: 44rpx;
    border: none;
    margin-top: 40rpx;
    
    &[loading] {
      opacity: 0.7;
    }
  }
}
</style>
