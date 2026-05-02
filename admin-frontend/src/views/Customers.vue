<template>
  <div class="customers-page">
    <van-nav-bar title="客户管理" left-arrow @click-left="$router.back()">
      <template #right>
        <van-icon name="down" size="20" @click="exportCustomers" />
      </template>
    </van-nav-bar>
    
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <van-field
        v-model="searchText"
        placeholder="搜索客户姓名/公司/手机"
        clearable
        @input="handleSearch"
      >
        <template #left-icon>
          <van-icon name="search" />
        </template>
      </van-field>
    </div>
    
    <!-- 客户列表 -->
    <div class="customer-list" v-loading="loading">
      <van-cell-group v-if="filteredCustomers.length > 0">
        <van-cell
          v-for="customer in filteredCustomers"
          :key="customer.id"
          :title="customer.name || '未命名'"
          :label="`${customer.company || ''} ${customer.phone || ''}`"
          is-link
          @click="showDetail(customer)"
        >
          <template #right-icon>
            <div class="action-buttons">
              <van-tag :type="getStatusType(customer.status)" class="status-tag">
                {{ getStatusText(customer.status) }}
              </van-tag>
              <van-icon 
                name="edit" 
                size="18" 
                color="#667eea" 
                @click.stop="showEditDialog(customer)" 
              />
              <van-icon 
                name="bulb-o" 
                size="18" 
                color="#ff9800" 
                @click.stop="generatePlan(customer)" 
              />
              <van-icon 
                name="delete" 
                size="18" 
                color="#ee0a24" 
                @click.stop="confirmDelete(customer)" 
              />
            </div>
          </template>
        </van-cell>
      </van-cell-group>
      
      <van-empty v-else description="暂无客户数据" />
    </div>
    
    <!-- 客户详情弹窗 -->
    <van-popup v-model:show="detailVisible" position="bottom" round :style="{ height: '60%' }">
      <div v-if="currentCustomer" class="detail-content">
        <div class="detail-header">
          <h3>{{ currentCustomer.name || '未命名客户' }}</h3>
          <van-icon name="cross" @click="detailVisible = false" />
        </div>
        <van-cell-group>
          <van-cell title="公司" :value="currentCustomer.company || '-'" />
          <van-cell title="职位" :value="currentCustomer.position || '-'" />
          <van-cell title="手机" :value="currentCustomer.phone || '-'" />
          <van-cell title="行业" :value="currentCustomer.industry || '-'" />
          <van-cell title="来源" :value="getSourceText(currentCustomer.sourceType)" />
          <van-cell title="状态" :value="getStatusText(currentCustomer.status)" />
          <van-cell title="需求" :value="currentCustomer.requirement || '-'" :label="currentCustomer.content" />
          <van-cell title="录入时间" :value="formatTime(currentCustomer.createTime)" />
        </van-cell-group>
        
        <div v-if="currentCustomer.attachmentCount > 0" class="attachment-info">
          <van-icon name="paper-plane" />
          <span>附件: {{ currentCustomer.attachmentCount }} 个</span>
        </div>
      </div>
    </van-popup>
    
    <!-- 编辑客户弹窗 -->
    <van-popup v-model:show="editVisible" position="bottom" round :style="{ height: '75%' }">
      <div class="edit-content">
        <div class="edit-header">
          <h3>编辑客户信息</h3>
          <van-icon name="cross" @click="editVisible = false" />
        </div>
        <van-form @submit="handleSave">
          <van-cell-group inset>
            <van-field v-model="editForm.name" name="name" label="姓名" placeholder="请输入姓名" />
            <van-field v-model="editForm.company" name="company" label="公司" placeholder="请输入公司" />
            <van-field v-model="editForm.position" name="position" label="职位" placeholder="请输入职位" />
            <van-field v-model="editForm.phone" name="phone" label="手机" placeholder="请输入手机号" type="tel" />
            <van-field v-model="editForm.industry" name="industry" label="行业" placeholder="请输入行业" />
            <van-field 
              v-model="editForm.requirement" 
              name="requirement" 
              label="需求" 
              type="textarea" 
              rows="3" 
              placeholder="请输入客户需求" 
            />
            <van-field name="status" label="状态">
              <template #input>
                <van-radio-group v-model="editForm.status" direction="horizontal">
                  <van-radio name="NEW">新录入</van-radio>
                  <van-radio name="FOLLOW_UP">跟进中</van-radio>
                  <van-radio name="CONVERTED">已转化</van-radio>
                  <van-radio name="LOST">已流失</van-radio>
                </van-radio-group>
              </template>
            </van-field>
          </van-cell-group>
          <div class="edit-actions">
            <van-button round block type="primary" native-type="submit" :loading="saving">
              保存
            </van-button>
            <van-button round block plain type="default" style="margin-top: 8px" @click="editVisible = false">
              取消
            </van-button>
          </div>
        </van-form>
      </div>
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive } from 'vue'
import { showToast, showSuccessToast, showConfirmDialog } from 'vant'
import request from '@/utils/request'

interface Customer {
  id: number
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

const loading = ref(false)
const searchText = ref('')
const customers = ref<Customer[]>([])
const detailVisible = ref(false)
const currentCustomer = ref<Customer | null>(null)

// 编辑相关
const editVisible = ref(false)
const saving = ref(false)
const editForm = reactive({
  id: 0,
  name: '',
  company: '',
  position: '',
  phone: '',
  industry: '',
  requirement: '',
  status: 'NEW'
})

// 加载客户列表
const loadCustomers = async () => {
  loading.value = true
  try {
    const response = await request.get<any>('/admin/customers')
    customers.value = response || []
  } catch (error: any) {
    console.error('加载客户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 过滤客户
const filteredCustomers = computed(() => {
  if (!searchText.value) return customers.value
  
  const keyword = searchText.value.toLowerCase()
  return customers.value.filter(c => 
    (c.name && c.name.toLowerCase().includes(keyword)) ||
    (c.company && c.company.toLowerCase().includes(keyword)) ||
    (c.phone && c.phone.includes(keyword))
  )
})

// 搜索
const handleSearch = () => {
  // 前端过滤即可
}

// 导出Excel
const exportCustomers = () => {
  const API_BASE_URL = import.meta.env.PROD 
    ? 'http://82.156.165.194:8080' 
    : 'http://localhost:8080'
  
  const token = localStorage.getItem('admin_token')
  const url = `${API_BASE_URL}/api/admin/export/customers`
  
  // 创建隐藏的a标签下载
  const link = document.createElement('a')
  link.href = url
  link.download = `客户明细_${new Date().getTime()}.xlsx`
  link.target = '_blank'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

// 显示详情
const showDetail = (customer: Customer) => {
  currentCustomer.value = customer
  detailVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (customer: Customer) => {
  editForm.id = customer.id
  editForm.name = customer.name || ''
  editForm.company = customer.company || ''
  editForm.position = customer.position || ''
  editForm.phone = customer.phone || ''
  editForm.industry = customer.industry || ''
  editForm.requirement = customer.requirement || ''
  editForm.status = customer.status || 'NEW'
  editVisible.value = true
}

// 保存编辑
const handleSave = async () => {
  try {
    saving.value = true
    
    await request.put(`/admin/customer/${editForm.id}`, {
      name: editForm.name,
      company: editForm.company,
      position: editForm.position,
      phone: editForm.phone,
      industry: editForm.industry,
      requirement: editForm.requirement,
      status: editForm.status
    })
    
    showSuccessToast('保存成功')
    editVisible.value = false
    loadCustomers() // 刷新列表
  } catch (error: any) {
    console.error('保存失败:', error)
    showToast(error.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 生成营销方案
const generatePlan = async (customer: Customer) => {
  showConfirmDialog({
    title: '生成营销方案',
    message: `是否为客户 "${customer.name || '未命名'}" 生成AI营销方案？`,
    confirmButtonText: '生成',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      showToast({ type: 'loading', message: 'AI正在生成中...' })
      
      const result = await request.post('/admin/plans/generate', null, {
        params: { customerId: customer.id }
      })
      
      showSuccessToast('营销方案生成成功')
      // 可以跳转到营销方案页面查看详情
    } catch (error: any) {
      console.error('生成失败:', error)
      showToast(error.message || '生成失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 确认删除
const confirmDelete = (customer: Customer) => {
  showConfirmDialog({
    title: '确认删除',
    message: `确定要删除客户 "${customer.name || '未命名'}" 吗？`,
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    confirmButtonColor: '#ee0a24'
  }).then(async () => {
    try {
      await request.delete(`/admin/customer/${customer.id}`)
      showSuccessToast('删除成功')
      loadCustomers() // 刷新列表
    } catch (error: any) {
      console.error('删除失败:', error)
      showToast(error.message || '删除失败')
    }
  }).catch(() => {
    // 用户取消
  })
}

// 状态文本
const getStatusText = (status?: string) => {
  const map: Record<string, string> = {
    'NEW': '新录入',
    'FOLLOW_UP': '跟进中',
    'CONVERTED': '已转化',
    'LOST': '已流失'
  }
  return map[status || ''] || '新录入'
}

// 状态类型
const getStatusType = (status?: string) => {
  const map: Record<string, any> = {
    'NEW': 'primary',
    'FOLLOW_UP': 'warning',
    'CONVERTED': 'success',
    'LOST': 'danger'
  }
  return map[status || ''] || 'primary'
}

// 来源文本
const getSourceText = (sourceType?: string) => {
  const map: Record<string, string> = {
    'VOICE': '语音录入',
    'TEXT': '文字录入',
    'IMPORT': '导入'
  }
  return map[sourceType || ''] || '未知'
}

// 格式化时间
const formatTime = (time?: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 初始化
loadCustomers()
</script>

<style scoped>
.customers-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  background: white;
  padding: 8px;
}

.customer-list {
  padding: 8px;
}

.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
}

.status-tag {
  margin-right: 8px;
}

.detail-content {
  padding: 20px 16px;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.detail-header h3 {
  margin: 0;
  font-size: 18px;
}

.edit-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.edit-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.edit-header h3 {
  margin: 0;
  font-size: 18px;
}

.edit-actions {
  padding: 16px;
  padding-bottom: calc(16px + env(safe-area-inset-bottom));
}

.attachment-info {
  margin-top: 16px;
  padding: 12px;
  background: #f0f0ff;
  border-radius: 8px;
  color: #667eea;
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
