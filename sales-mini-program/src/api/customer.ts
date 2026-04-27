import { request } from '@/utils/request'
import type { Customer } from '@/stores/user'

/**
 * 获取销售人员的客户列表
 */
export const getCustomers = (salesId: number): Promise<Customer[]> => {
  return request({
    url: `/customers?salesId=${salesId}`,
    method: 'GET'
  })
}

/**
 * 新增客户
 */
export const addCustomer = (customer: Omit<Customer, 'id' | 'createTime'>): Promise<Customer> => {
  return request({
    url: '/customers',
    method: 'POST',
    data: customer
  })
}
