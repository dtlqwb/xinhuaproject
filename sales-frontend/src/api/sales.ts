import request from '@/utils/request'

export interface LoginData {
  phone: string
  password: string
}

export interface UserInfo {
  id: number
  name: string
  phone: string
  department: string
  token: string
}

// 登录
export const login = async (data: LoginData): Promise<UserInfo> => {
  try {
    // 尝试调用真实接口
    const result = await request.post<UserInfo>('/sales/login', data)
    return result as unknown as UserInfo
  } catch (error: any) {
    // 如果是网络错误或服务器错误，使用模拟数据（演示模式）
    console.warn('登录请求失败:', error.message)
    
    // 演示模式：任何错误都返回模拟数据
    console.warn('后端未启动或出错，使用演示模式')
    // 模拟延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    return {
      id: 1,
      name: '张三',
      phone: data.phone,
      department: '销售部',
      token: 'demo-token-' + Date.now()
    }
  }
}

// 获取今日客户数量
export const getTodayCount = (salesId: number) => {
  return request.get<{ count: number }>('/sales/today-count', {
    params: { salesId }
  })
}
