<template>
  <div class="plans-page">
    <van-nav-bar title="营销方案" left-arrow @click-left="$router.back()">
      <template #right>
        <van-icon name="plus" size="20" @click="showCreateDialog = true" style="margin-right: 12px" />
        <van-icon name="magic-stick" size="20" @click="showGenerateDialog = true" />
      </template>
    </van-nav-bar>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <van-search v-model="searchKeyword" placeholder="搜索客户或销售姓名" @search="loadPlans" />
      <van-dropdown-menu>
        <van-dropdown-item v-model="statusFilter" :options="statusOptions" @change="loadPlans" />
      </van-dropdown-menu>
    </div>

    <!-- 方案列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="loadPlans">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <van-cell
          v-for="plan in plans"
          :key="plan.id"
          :title="plan.planName || `方案-${plan.customerName}`"
          :label="`${plan.salesName} - ${plan.customerName}`"
          is-link
          @click="showPlanDetail(plan)"
        >
          <template #icon>
            <van-tag :type="getStatusType(plan.status)" style="margin-right: 8px">
              {{ getStatusText(plan.status) }}
            </van-tag>
          </template>
          <template #value>
            <span style="font-size: 12px; color: #999">
              {{ plan.executeDate || '未设置' }}
            </span>
          </template>
        </van-cell>
      </van-list>
    </van-pull-refresh>

    <!-- 方案详情弹窗 -->
    <van-popup v-model:show="showDetail" round position="bottom" :style="{ height: '85%' }">
      <div v-if="currentPlan" class="detail-content">
        <van-nav-bar title="方案详情" right-text="关闭" @click-right="showDetail = false" />
        
        <div class="detail-body">
          <van-cell-group inset>
            <van-cell title="方案名称" :value="currentPlan.planName || '-'" />
            <van-cell title="客户姓名" :value="currentPlan.customerName" />
            <van-cell title="销售人员" :value="currentPlan.salesName" />
            <van-cell title="方案类型" :value="getTypeText(currentPlan.planType)" />
            <van-cell title="执行时间" :value="currentPlan.executeDate || '未设置'" />
            <van-cell title="状态" :value="getStatusText(currentPlan.status)" />
          </van-cell-group>

          <div class="section">
            <h3>方案内容</h3>
            <p>{{ currentPlan.planContent || '暂无' }}</p>
          </div>

          <div v-if="currentPlan.aiGeneratedContent" class="section ai-section">
            <h3>AI生成内容</h3>
            <p>{{ currentPlan.aiGeneratedContent }}</p>
          </div>

          <div v-if="currentPlan.executeResult" class="section">
            <h3>执行结果</h3>
            <p>{{ currentPlan.executeResult }}</p>
          </div>

          <div v-if="currentPlan.remark" class="section">
            <h3>备注</h3>
            <p>{{ currentPlan.remark }}</p>
          </div>

          <!-- 操作按钮 -->
          <div class="actions">
            <van-button
              v-if="currentPlan.status === 'draft'"
              type="primary"
              block
              @click="updateStatus(currentPlan.id, 'pending')"
            >
              设为待执行
            </van-button>
            <van-button
              v-if="currentPlan.status === 'pending'"
              type="warning"
              block
              @click="updateStatus(currentPlan.id, 'executing')"
            >
              开始执行
            </van-button>
            <van-button
              v-if="currentPlan.status === 'executing'"
              type="success"
              block
              @click="showCompleteDialog = true"
            >
              标记完成
            </van-button>
          </div>
        </div>
      </div>
    </van-popup>

    <!-- 创建方案对话框 -->
    <van-dialog
      v-model:show="showCreateDialog"
      title="创建营销方案"
      show-cancel-button
      @confirm="createPlan"
    >
      <van-form>
        <van-field
          v-model="newPlan.planName"
          label="方案名称"
          placeholder="请输入方案名称"
        />
        <van-field
          v-model="newPlan.customerId"
          label="客户ID"
          placeholder="请输入客户ID"
          type="number"
        />
        <van-field
          v-model="newPlan.customerName"
          label="客户姓名"
          placeholder="请输入客户姓名"
        />
        <van-field
          v-model="newPlan.salesId"
          label="销售ID"
          placeholder="请输入销售ID"
          type="number"
        />
        <van-field
          v-model="newPlan.salesName"
          label="销售姓名"
          placeholder="请输入销售姓名"
        />
        <van-field
          v-model="newPlan.planType"
          label="方案类型"
          placeholder="product/service/promotion"
        />
        <van-field
          v-model="newPlan.planContent"
          rows="4"
          autosize
          type="textarea"
          label="方案内容"
          placeholder="请输入方案内容"
        />
        <van-field
          v-model="newPlan.executeDate"
          label="执行时间"
          placeholder="YYYY-MM-DD"
        />
      </van-form>
    </van-dialog>

    <!-- 完成方案对话框 -->
    <van-dialog
      v-model:show="showCompleteDialog"
      title="完成方案"
      show-cancel-button
      @confirm="completePlan"
    >
      <van-field
        v-model="completeResult"
        rows="4"
        autosize
        type="textarea"
        placeholder="请输入执行结果"
      />
    </van-dialog>

    <!-- AI生成方案对话框 -->
    <van-dialog
      v-model:show="showGenerateDialog"
      title="AI生成营销方案"
      show-cancel-button
      @confirm="generatePlan"
    >
      <van-field
        v-model="generateCustomerId"
        type="number"
        label="客户ID"
        placeholder="请输入客户ID"
      />
      <div style="padding: 16px; color: #999; font-size: 12px">
        系统将根据客户信息，AI自动生成定制化营销方案
      </div>
    </van-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import request from '@/utils/request'

interface Plan {
  id: number
  planName?: string
  customerId: number
  customerName: string
  salesId: number
  salesName: string
  planType?: string
  planContent?: string
  aiGeneratedContent?: string
  executeDate?: string
  status: string
  executeResult?: string
  completeTime?: string
  remark?: string
  createTime: string
}

const plans = ref<Plan[]>([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const searchKeyword = ref('')
const statusFilter = ref('')
const showDetail = ref(false)
const showCreateDialog = ref(false)
const showCompleteDialog = ref(false)
const showGenerateDialog = ref(false)
const currentPlan = ref<Plan | null>(null)
const completeResult = ref('')
const generateCustomerId = ref('')

const newPlan = ref({
  planName: '',
  customerId: 0,
  customerName: '',
  salesId: 0,
  salesName: '',
  planType: 'product',
  planContent: '',
  executeDate: ''
})

const statusOptions = [
  { text: '全部', value: '' },
  { text: '草稿', value: 'draft' },
  { text: '待执行', value: 'pending' },
  { text: '执行中', value: 'executing' },
  { text: '已完成', value: 'completed' }
]

onMounted(() => {
  loadPlans()
})

const loadPlans = async () => {
  try {
    const data = await request.get('/admin/plans')
    let filtered = data || []
    
    // 状态筛选
    if (statusFilter.value) {
      filtered = filtered.filter((p: Plan) => p.status === statusFilter.value)
    }
    
    // 关键词搜索
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      filtered = filtered.filter((p: Plan) => 
        p.customerName?.toLowerCase().includes(keyword) ||
        p.salesName?.toLowerCase().includes(keyword) ||
        p.planName?.toLowerCase().includes(keyword)
      )
    }
    
    plans.value = filtered
    finished.value = true
  } catch (error: any) {
    console.error('加载方案失败:', error)
    showToast(error.message || '加载失败')
  }
}

const onLoad = () => {
  finished.value = true
}

const showPlanDetail = (plan: Plan) => {
  currentPlan.value = plan
  showDetail.value = true
}

const updateStatus = async (id: number, status: string) => {
  try {
    await request.post(`/admin/plans/${id}/status`, null, {
      params: { status }
    })
    showToast('状态更新成功')
    showDetail.value = false
    loadPlans()
  } catch (error: any) {
    showToast(error.message || '更新失败')
  }
}

const completePlan = async () => {
  if (!currentPlan.value) return
  
  try {
    await request.post(`/admin/plans/${currentPlan.value.id}/complete`, null, {
      params: { result: completeResult.value }
    })
    showToast('方案已完成')
    showCompleteDialog.value = false
    showDetail.value = false
    completeResult.value = ''
    loadPlans()
  } catch (error: any) {
    showToast(error.message || '操作失败')
  }
}

const createPlan = async () => {
  try {
    await request.post('/admin/plans', newPlan.value)
    showToast('创建成功')
    showCreateDialog.value = false
    // 重置表单
    newPlan.value = {
      planName: '',
      customerId: 0,
      customerName: '',
      salesId: 0,
      salesName: '',
      planType: 'product',
      planContent: '',
      executeDate: ''
    }
    loadPlans()
  } catch (error: any) {
    showToast(error.message || '创建失败')
  }
}

// AI生成营销方案
const generatePlan = async () => {
  if (!generateCustomerId.value) {
    showToast('请输入客户ID')
    return
  }
  
  showToast({
    message: 'AI正在生成营销方案，请稍候...',
    duration: 0,
    forbidClick: true,
    loadingType: 'spinner'
  })
  
  try {
    const result = await request.post('/admin/plans/generate', null, {
      params: { customerId: generateCustomerId.value }
    })
    
    showToast('方案生成成功')
    showGenerateDialog.value = false
    generateCustomerId.value = ''
    loadPlans()
  } catch (error: any) {
    showToast(error.message || '生成失败')
  }
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    pending: '待执行',
    executing: '执行中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return map[status] || status
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    draft: 'default',
    pending: 'primary',
    executing: 'warning',
    completed: 'success',
    cancelled: 'danger'
  }
  return map[status] || 'default'
}

const getTypeText = (type?: string) => {
  const map: Record<string, string> = {
    product: '产品方案',
    service: '服务方案',
    promotion: '促销方案'
  }
  return map[type || ''] || type || '-'
}
</script>

<style scoped>
.plans-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.filter-bar {
  background: white;
}

.detail-content {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.detail-body {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.section {
  margin-top: 16px;
  padding: 16px;
  background: white;
  border-radius: 8px;
}

.section h3 {
  margin: 0 0 12px 0;
  font-size: 16px;
  color: #333;
}

.section p {
  margin: 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  white-space: pre-wrap;
}

.ai-section {
  background: #f0f9ff;
  border-left: 4px solid #667eea;
}

.actions {
  margin-top: 24px;
  padding: 0 16px 16px;
}

.actions .van-button {
  margin-top: 12px;
}
</style>
