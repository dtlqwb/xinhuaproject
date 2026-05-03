import axios from 'axios'

// 统一使用相对路径,由Nginx代理API请求
// 生产环境: /api → Nginx proxy_pass → backend:8080
// 开发环境: /api → Vite proxy → localhost:8080
const API_BASE_URL = '/api'

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000
})

request.interceptors.request.use(config => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  response => {
    const res = response.data as any
    if (res.code !== 200) {
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data as any
  },
  (error: any) => {
    let message = '请求失败'
    
    // 详细的网络错误诊断
    if (error.code === 'ERR_NETWORK') {
      console.error('网络连接失败详情:', {
        message: error.message,
        config: error.config,
        code: error.code
      })
      message = '网络连接失败，请检查：\n1. 后端服务是否启动（端口8080）\n2. 网络连接是否正常\n3. 防火墙设置'
    } else if (error.response?.status === 401) {
      message = '未授权，请重新登录'
      localStorage.removeItem('admin_token')
    } else if (error.response) {
      // 服务器返回了错误状态码
      console.error('HTTP错误:', error.response.status, error.response.data)
      message = error.response.data?.message || `请求失败 (${error.response.status})`
    } else if (error.request) {
      // 请求已发出但没有收到响应
      console.error('请求超时或无响应:', error.config?.url)
      message = '请求超时，请稍后重试'
    }
    
    return Promise.reject(new Error(message))
  }
)

export default request
