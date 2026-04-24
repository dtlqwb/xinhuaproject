import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { UserInfo } from '@/api/sales'
import type { Customer } from '@/api/customer'

export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const todayCustomers = ref<Customer[]>([])
  
  const setUserInfo = (info: UserInfo) => {
    userInfo.value = info
    localStorage.setItem('token', info.token)
    localStorage.setItem('userInfo', JSON.stringify(info))
  }
  
  const clearUserInfo = () => {
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
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
