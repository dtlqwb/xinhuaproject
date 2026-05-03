<template>
  <div class="login-page">
    <div class="login-box">
      <h1>管理后台</h1>
      <van-form @submit="onSubmit">
        <van-field v-model="username" label="用户名" placeholder="请输入用户名" />
        <van-field v-model="password" type="password" label="密码" placeholder="请输入密码" />
        <van-button round block type="primary" native-type="submit" :loading="loading">登录</van-button>
      </van-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'
import request from '@/utils/request'

const router = useRouter()
const username = ref('admin')
const password = ref('admin123')
const loading = ref(false)

const onSubmit = async () => {
  loading.value = true
  try {
    const result = await request.post('/admin/login', {
      username: username.value,
      password: password.value
    })
    // 保存 token、role 和用户信息
    localStorage.setItem('admin_token', result.token)
    localStorage.setItem('admin_role', result.role || 'admin')
    localStorage.setItem('admin_user', JSON.stringify(result))
    showToast('登录成功')
    router.push('/dashboard')
  } catch (error: any) {
    showToast(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  background: white;
  padding: 40px;
  border-radius: 16px;
  width: 90%;
  max-width: 400px;
}
.login-box h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
</style>
