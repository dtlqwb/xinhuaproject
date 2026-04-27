import { defineStore } from 'pinia'
import { ref } from 'vue'
import Taro from '@tarojs/taro'

export interface UserInfo {
  id: number
  name: string
  phone: string
  token: string
}

export interface Customer {
  id?: number
  name?: string
  phone?: string
  company?: string
  position?: string
  industry?: string
  sourceType?: string
  status?: string
  requirement?: string
  content?: string
  createTime?: string
}

export const useUserStore = defineStore('user', () => {
  // 从Taro.storage恢复用户信息
  const savedUserInfo = Taro.getStorageSync('userInfo')
  const userInfo = ref<UserInfo | null>(savedUserInfo || null)
  const todayCustomers = ref<Customer[]>([])
  
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    Taro.setStorageSync('token', info.token)
    Taro.setStorageSync('userInfo', info)
  }
  
  const clearUserInfo = () => {
    userInfo.value = null
    Taro.removeStorageSync('token')
    Taro.removeStorageSync('userInfo')
  }
  
  const addCustomer = (customer: Customer) => {
    todayCustomers.value.unshift(customer)
  }
  
  const loadTodayCustomers = (customers: Customer[]) => {
    todayCustomers.value = customers
  }
  
  return {
    userInfo,
    todayCustomers,
    setUserInfo,
    clearUserInfo,
    addCustomer,
    loadTodayCustomers
  }
})
