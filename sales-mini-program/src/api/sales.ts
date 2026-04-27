import { request } from '@/utils/request'

export interface LoginParams {
  phone: string
  password: string
}

export interface LoginResponse {
  id: number
  name: string
  phone: string
  token: string
}

/**
 * 销售端登录
 */
export const login = (params: LoginParams): Promise<LoginResponse> => {
  return request({
    url: '/sales/login',
    method: 'POST',
    data: params
  })
}

/**
 * 获取今日录入数量
 */
export const getTodayCount = (salesId: number): Promise<{ count: number }> => {
  return request({
    url: `/sales/today-count?salesId=${salesId}`,
    method: 'GET'
  })
}
