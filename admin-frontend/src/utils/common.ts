/**
 * 公共工具函数
 */

/**
 * 安全访问嵌套对象属性
 */
export const safeGet = (obj: any, path: string, defaultValue: any = null): any => {
  try {
    return path.split('.').reduce((acc, part) => acc && acc[part], obj) ?? defaultValue
  } catch {
    return defaultValue
  }
}

/**
 * 格式化日期
 */
export const formatDate = (dateStr: string | null | undefined): string => {
  if (!dateStr) return '-'
  try {
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  } catch {
    return '-'
  }
}

/**
 * 格式化日期时间
 */
export const formatDateTime = (dateStr: string | null | undefined): string => {
  if (!dateStr) return '-'
  try {
    const date = new Date(dateStr)
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hour = String(date.getHours()).padStart(2, '0')
    const minute = String(date.getMinutes()).padStart(2, '0')
    return `${year}-${month}-${day} ${hour}:${minute}`
  } catch {
    return '-'
  }
}

/**
 * 状态文本映射
 */
export const statusMap: Record<string, { text: string; type: string }> = {
  pending: { text: '待处理', type: 'warning' },
  completed: { text: '已完成', type: 'success' },
  failed: { text: '已失败', type: 'danger' },
  draft: { text: '草稿', type: 'default' },
  executing: { text: '执行中', type: 'primary' }
}

/**
 * 获取状态文本
 */
export const getStatusText = (status: string | null | undefined): string => {
  return statusMap[status || '']?.text || '未知'
}

/**
 * 获取状态类型
 */
export const getStatusType = (status: string | null | undefined): string => {
  return statusMap[status || '']?.type || 'default'
}

/**
 * 截断文本
 */
export const truncateText = (text: string | null | undefined, maxLength: number = 50): string => {
  if (!text) return '-'
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

/**
 * 深拷贝
 */
export const deepClone = <T>(obj: T): T => {
  return JSON.parse(JSON.stringify(obj))
}

/**
 * 防抖函数
 */
export const debounce = <T extends (...args: any[]) => any>(
  fn: T,
  delay: number = 300
): ((...args: Parameters<T>) => void) => {
  let timer: ReturnType<typeof setTimeout> | null = null
  return (...args: Parameters<T>) => {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => fn(...args), delay)
  }
}

/**
 * 节流函数
 */
export const throttle = <T extends (...args: any[]) => any>(
  fn: T,
  limit: number = 300
): ((...args: Parameters<T>) => void) => {
  let inThrottle: boolean = false
  return (...args: Parameters<T>) => {
    if (!inThrottle) {
      fn(...args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}
