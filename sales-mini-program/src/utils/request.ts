import Taro from '@tarojs/taro'

// API基础URL(根据环境切换)
const BASE_URL = 'https://82.156.165.194/api'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: any
}

/**
 * 封装Taro.request
 */
export const request = <T = any>(options: RequestOptions): Promise<T> => {
  return new Promise((resolve, reject) => {
    Taro.request({
      url: `${BASE_URL}${options.url}`,
      method: options.method || 'GET',
      data: options.data,
      header: {
        'Content-Type': 'application/json',
        ...options.header
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const data = res.data as any
          if (data.code === 200) {
            resolve(data.data)
          } else {
            reject(new Error(data.message || '请求失败'))
          }
        } else {
          reject(new Error(`HTTP错误: ${res.statusCode}`))
        }
      },
      fail: (err) => {
        reject(new Error(err.errMsg || '网络请求失败'))
      }
    })
  })
}
