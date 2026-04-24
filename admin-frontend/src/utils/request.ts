import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
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
  error => {
    let message = '请求失败'
    if (error.code === 'ERR_NETWORK') {
      message = '网络连接失败'
    } else if (error.response?.status === 401) {
      message = '未授权，请重新登录'
      localStorage.removeItem('admin_token')
    }
    return Promise.reject(new Error(message))
  }
)

export default request
