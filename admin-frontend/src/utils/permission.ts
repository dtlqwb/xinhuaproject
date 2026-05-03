/**
 * 获取当前用户角色
 */
export const getUserRole = (): string => {
  return localStorage.getItem('admin_role') || ''
}

/**
 * 是否为超级管理员
 */
export const isSuperAdmin = (): boolean => {
  return getUserRole() === 'super_admin'
}

/**
 * 是否可以管理用户（仅超级管理员）
 */
export const canManageUsers = (): boolean => {
  return isSuperAdmin()
}

/**
 * 获取角色显示文本
 */
export const getRoleText = (role: string): string => {
  const roleMap: Record<string, string> = {
    super_admin: '超级管理员',
    admin: '普通管理员',
    sales: '销售人员'
  }
  return roleMap[role] || role
}

/**
 * 获取角色标签类型
 */
export const getRoleType = (role: string): string => {
  const typeMap: Record<string, string> = {
    super_admin: 'danger',
    admin: 'primary',
    sales: 'success'
  }
  return typeMap[role] || 'default'
}
