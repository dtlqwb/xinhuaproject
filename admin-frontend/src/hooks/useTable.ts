import { ref, onMounted } from 'vue'
import { showToast } from 'vant'

/**
 * 通用列表数据加载Hook
 */
export function useTable<T>(
  loadDataFn: () => Promise<T[]>,
  options?: {
    immediate?: boolean
    onError?: (error: any) => void
    onSuccess?: (data: T[]) => void
  }
) {
  const {
    immediate = true,
    onError,
    onSuccess
  } = options || {}

  const loading = ref(false)
  const data = ref<T[]>([])
  const error = ref<any>(null)

  /**
   * 加载数据
   */
  const loadData = async () => {
    loading.value = true
    error.value = null
    
    try {
      const result = await loadDataFn()
      data.value = result || []
      onSuccess?.(data.value)
    } catch (err: any) {
      error.value = err
      console.error('数据加载失败:', err)
      showToast(err.message || '加载失败')
      onError?.(err)
    } finally {
      loading.value = false
    }
  }

  /**
   * 刷新数据
   */
  const refresh = async () => {
    await loadData()
  }

  // 立即加载
  if (immediate) {
    onMounted(() => {
      loadData()
    })
  }

  return {
    loading,
    data,
    error,
    loadData,
    refresh
  }
}
