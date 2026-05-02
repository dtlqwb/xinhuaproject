<template>
  <div class="login-page">
    <div class="login-header">
      <h1>智销助手</h1>
      <p>销售人员登录</p>
    </div>
    
    <van-form @submit="onSubmit" class="login-form">
      <van-cell-group inset>
        <van-field
          v-model="phone"
          name="phone"
          label="手机号"
          placeholder="请输入手机号"
          :rules="[{ required: true, message: '请输入手机号' }]"
        />
        <van-field
          v-model="password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码"
          :rules="[{ required: true, message: '请输入密码' }]"
        />
      </van-cell-group>
      
      <div class="login-button">
        <van-button round block type="primary" native-type="submit" :loading="loading">
          登录
        </van-button>
      </div>
    </van-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import { login } from '@/api/sales'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const phone = ref('13800138000')
const password = ref('123456')
const loading = ref(false)

const onSubmit = async (values: any) => {
  loading.value = true
  
  try {
    console.log('开始登录:', { phone: phone.value })
    
    const result = await login({
      phone: phone.value,
      password: password.value
    })
    
    console.log('登录成功:', result)
    
    // 清除旧的用户信息,确保使用新数据
    localStorage.removeItem('userInfo')
    localStorage.removeItem('token')
    
    userStore.setUserInfo(result)
    showToast('登录成功')
    
    // 使用window.location直接跳转,避免路由守卫问题
    setTimeout(() => {
      console.log('准备跳转到主页')
      window.location.href = '/'
    }, 500)
  } catch (error: any) {
    console.error('登录失败:', error)
    showToast(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 60px 20px;
}

.login-header {
  text-align: center;
  color: white;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 32px;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 16px;
  opacity: 0.9;
}

.login-form {
  background: white;
  border-radius: 16px;
  padding: 20px 0;
}

.login-button {
  padding: 20px 16px;
}
</style>
