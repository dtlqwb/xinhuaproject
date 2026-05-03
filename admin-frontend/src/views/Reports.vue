<template>
  <div class="reports-page">
    <van-nav-bar title="日报管理">
      <template #right>
        <van-icon name="setting-o" size="20" @click="showGenerateDialog = true" />
      </template>
    </van-nav-bar>

    <!-- 筛选栏 -->
    <div class="filter-bar">
      <van-search v-model="searchKeyword" placeholder="搜索销售姓名" @search="loadReports" />
    </div>

    <!-- 日报列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="loadReports">
      <van-list
        v-model:loading="loading"
        :finished="finished"
        finished-text="没有更多了"
        @load="onLoad"
      >
        <!-- 空状态提示 -->
        <van-empty
          v-if="!loading && reports.length === 0"
          description="暂无日报数据"
        />
        
        <van-cell
          v-for="report in reports"
          :key="report.id"
          :title="`${report.salesName} - ${report.reportDate}`"
          :label="getStatusText(report.status)"
          :value="report.todayCustomers + '个客户'"
          is-link
          @click="showReportDetail(report)"
        >
          <template #icon>
            <van-tag :type="getStatusType(report.status)" style="margin-right: 8px">
              {{ getStatusText(report.status) }}
            </van-tag>
          </template>
        </van-cell>
      </van-list>
    </van-pull-refresh>

    <!-- 日报详情弹窗 -->
    <van-popup v-model:show="showDetail" round position="bottom" :style="{ height: '80%' }">
      <div v-if="currentReport" class="detail-content">
        <van-nav-bar title="日报详情" right-text="关闭" @click-right="showDetail = false" />
        
        <div class="detail-body">
          <van-cell-group inset>
            <van-cell title="销售姓名" :value="currentReport.salesName" />
            <van-cell title="日报日期" :value="currentReport.reportDate" />
            <van-cell title="状态" :value="getStatusText(currentReport.status)" />
            <van-cell title="今日新增" :value="currentReport.todayCustomers + '个'" />
            <van-cell title="今日跟进" :value="(currentReport.followUpCustomers || 0) + '个'" />
            <van-cell title="今日签约" :value="(currentReport.signedCustomers || 0) + '个'" />
          </van-cell-group>

          <div class="section">
            <h3>工作内容</h3>
            <p>{{ currentReport.workContent || '暂无' }}</p>
          </div>

          <div class="section">
            <h3>存在问题</h3>
            <p>{{ currentReport.problems || '暂无' }}</p>
          </div>

          <div class="section">
            <h3>明日计划</h3>
            <p>{{ currentReport.tomorrowPlan || '暂无' }}</p>
          </div>

          <!-- 审核信息 -->
          <div v-if="currentReport.status === 'reviewed'" class="section review-section">
            <h3>审核信息</h3>
            <van-cell-group inset>
              <van-cell title="审核人" :value="currentReport.reviewerName" />
              <van-cell title="审核时间" :value="formatDateTime(currentReport.reviewTime)" />
              <van-cell title="审核意见" :value="currentReport.reviewComment || '无'" />
            </van-cell-group>
          </div>

          <!-- 操作按钮 -->
          <div class="actions">
            <van-button
              v-if="currentReport.status === 'draft'"
              type="primary"
              block
              @click="submitReport(currentReport.id)"
            >
              提交日报
            </van-button>
            <van-button
              v-if="currentReport.status === 'submitted'"
              type="success"
              block
              @click="showReviewDialog = true"
            >
              审核日报
            </van-button>
          </div>
        </div>
      </div>
    </van-popup>

    <!-- 审核对话框 -->
    <van-dialog
      v-model:show="showReviewDialog"
      title="审核日报"
      show-cancel-button
      @confirm="reviewReport"
    >
      <van-field
        v-model="reviewComment"
        rows="3"
        autosize
        type="textarea"
        placeholder="请输入审核意见"
      />
    </van-dialog>

    <!-- 生成日报对话框 -->
    <van-dialog
      v-model:show="showGenerateDialog"
      title="生成日报"
      show-cancel-button
      @confirm="generateDailyReport"
    >
      <van-field
        v-model="reportDate"
        type="date"
        placeholder="选择日期"
        label="日报日期"
      />
      <div style="padding: 16px; color: #999; font-size: 12px;">
        系统将汇总当天所有销售人员录入的客户信息,AI自动生成日报总结
      </div>
    </van-dialog>
  
    <!-- 底部导航 -->
    <van-tabbar v-model="activeTab" route>
      <van-tabbar-item icon="home-o" to="/dashboard">首页</van-tabbar-item>
      <van-tabbar-item icon="friends-o" to="/customers">客户</van-tabbar-item>
      <van-tabbar-item icon="notes-o" to="/reports">日报</van-tabbar-item>
      <van-tabbar-item icon="todo-list-o" to="/plans">方案</van-tabbar-item>
    </van-tabbar>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast } from 'vant'
import request from '@/utils/request'

const activeTab = ref(2) // 日报页面索引为2

interface Report {
  id: number
  salesId: number
  salesName: string
  reportDate: string
  todayCustomers: number
  followUpCustomers?: number
  signedCustomers?: number
  workContent?: string
  problems?: string
  tomorrowPlan?: string
  status: string
  reviewerId?: number
  reviewerName?: string
  reviewComment?: string
  reviewTime?: string
  createTime: string
}

const reports = ref<Report[]>([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const searchKeyword = ref('')
const showDetail = ref(false)
const showGenerateDialog = ref(false)
const showReviewDialog = ref(false)
const currentReport = ref<Report | null>(null)
const reviewComment = ref('')
const reportDate = ref(() => {
  // 使用本地时区日期,避免UTC时区偏移问题
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
})()

onMounted(() => {
  loadReports()
})

const loadReports = async () => {
  try {
    const data = await request.get('/admin/reports')
    reports.value = data || []
    finished.value = true
  } catch (error: any) {
    console.error('加载日报失败:', error)
    showToast(error.message || '加载失败')
  }
}

const onLoad = () => {
  // 分页加载逻辑(如果需要)
  // 由于后端暂未实现分页,直接标记为finished
  loading.value = false
  finished.value = true
}

const showReportDetail = (report: Report) => {
  currentReport.value = report
  showDetail.value = true
}

// AI生成日报
const generateDailyReport = async () => {
  if (!reportDate.value) {
    showToast('请选择日期')
    return
  }
  
  showToast({
    message: 'AI正在生成日报，请稍候...',
    duration: 0,
    forbidClick: true,
    loadingType: 'spinner'
  })
  
  try {
    const result = await request.post('/admin/reports/generate', null, {
      params: { date: reportDate.value }
    })
    
    showToast('日报生成成功')
    showGenerateDialog.value = false
    loadReports()
  } catch (error: any) {
    showToast(error.message || '生成失败')
  }
}

const submitReport = async (id: number) => {
  try {
    await request.post(`/admin/reports/${id}/submit`)
    showToast('提交成功')
    showDetail.value = false
    loadReports()
  } catch (error: any) {
    showToast(error.message || '提交失败')
  }
}

const reviewReport = async () => {
  if (!currentReport.value) return
  
  try {
    await request.post(`/admin/reports/${currentReport.value.id}/review`, null, {
      params: {
        reviewerId: 1, // 当前管理员ID
        reviewerName: '管理员',
        comment: reviewComment.value
      }
    })
    showToast('审核成功')
    showReviewDialog.value = false
    showDetail.value = false
    reviewComment.value = ''
    loadReports()
  } catch (error: any) {
    showToast(error.message || '审核失败')
  }
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    draft: '草稿',
    submitted: '已提交',
    reviewed: '已审核'
  }
  return map[status] || status
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    draft: 'default',
    submitted: 'primary',
    reviewed: 'success'
  }
  return map[status] || 'default'
}

const formatDateTime = (dateTime?: string) => {
  if (!dateTime) return '-'
  return dateTime.replace('T', ' ').substring(0, 16)
}
</script>

<style scoped>
.reports-page {
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

.review-section {
  background: #f0f9ff;
}

.actions {
  margin-top: 24px;
  padding: 0 16px 16px;
}

.actions .van-button {
  margin-top: 12px;
}
</style>
