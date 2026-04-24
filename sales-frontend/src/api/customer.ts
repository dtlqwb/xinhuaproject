import request from '@/utils/request'

export interface Customer {
  id?: number
  name?: string
  phone?: string
  company?: string
  position?: string
  industry?: string
  sourceType?: string
  content?: string
  requirement?: string
  status?: string
  salesId?: number
  attachmentCount?: number
  createTime?: string
}

// 添加客户
export const addCustomer = (data: FormData) => {
  return request.post<Customer>('/customer/add', data, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 查询客户列表
export const getCustomers = (salesId: number) => {
  return request.get<Customer[]>('/customer/list', {
    params: { salesId }
  })
}

// 查询客户详情
export const getCustomerDetail = (id: number) => {
  return request.get<Customer>(`/customer/${id}`)
}

// 更新客户
export const updateCustomer = (data: Customer) => {
  return request.put<boolean>('/customer/update', data)
}

// 删除客户
export const deleteCustomer = (id: number) => {
  return request.delete<boolean>(`/customer/${id}`)
}
