<template>
  <div class="users-page">
    <!-- 顶部导航 -->
    <van-nav-bar title="用户管理" left-arrow @click-left="$router.back()">
      <template #right>
        <van-icon name="plus" size="18" @click="showAddDialog = true" v-if="isSuperAdmin()" />
      </template>
    </van-nav-bar>

    <!-- 用户列表 -->
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
      <van-list v-model:loading="loading" :finished="finished" @load="loadUsers">
        <EmptyState v-if="!loading && finished && users.length === 0" description="暂无用户数据" />

        <van-cell v-for="user in users" :key="user.id" class="user-item">
          <template #title>
            <div class="user-info">
              <div class="user-name">{{ user.realName || user.username }}</div>
              <van-tag :type="getRoleType(user.role)" size="small">{{ getRoleText(user.role) }}</van-tag>
            </div>
          </template>
          <template #label>
            <div class="user-detail">
              <div>用户名: {{ user.username }}</div>
              <div v-if="user.phone">手机: {{ user.phone }}</div>
              <div v-if="user.email">邮箱: {{ user.email }}</div>
            </div>
          </template>
          <template #extra v-if="isSuperAdmin()">
            <div class="user-actions">
              <van-switch 
                v-model="user.status" 
                active-value="1" 
                inactive-value="0"
                size="20px"
                @change="toggleStatus(user)" 
              />
              <van-button size="mini" type="primary" @click="editUser(user)">编辑</van-button>
              <van-button size="mini" type="danger" @click="deleteUser(user.id)" v-if="user.role !== 'super_admin'">删除</van-button>
            </div>
          </template>
        </van-cell>
      </van-list>
    </van-pull-refresh>

    <!-- 新增/编辑用户弹窗 -->
    <van-dialog 
      v-model:show="showAddDialog" 
      :title="editingUser ? '编辑用户' : '新增用户'" 
      show-cancel-button 
      @confirm="saveUser"
    >
      <van-form>
        <van-field v-model="formData.username" label="用户名" placeholder="请输入用户名" required :disabled="!!editingUser" />
        <van-field v-model="formData.realName" label="姓名" placeholder="请输入姓名" />
        <van-field v-model="formData.phone" label="手机号" placeholder="请输入手机号" type="tel" />
        <van-field v-model="formData.email" label="邮箱" placeholder="请输入邮箱" type="email" />
        <van-field 
          v-model="formData.role" 
          label="角色" 
          is-link 
          readonly 
          @click="showRolePicker = true"
        />
        <van-field 
          v-if="!editingUser" 
          v-model="formData.password" 
          label="密码" 
          placeholder="请输入密码" 
          type="password" 
          required 
        />
        <van-field v-if="editingUser" label="密码" value="******" disabled />
      </van-form>
    </van-dialog>

    <!-- 角色选择器 -->
    <van-popup v-model:show="showRolePicker" position="bottom">
      <van-picker
        :columns="roleOptions"
        @confirm="onRoleConfirm"
        @cancel="showRolePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { showToast, showConfirmDialog } from 'vant'
import request from '@/utils/request'
import EmptyState from '@/components/EmptyState/EmptyState.vue'
import { isSuperAdmin, getRoleText, getRoleType } from '@/utils/permission'

interface AdminUser {
  id?: number
  username: string
  password?: string
  phone?: string
  email?: string
  realName?: string
  role: string
  status: number | string
}

const users = ref<AdminUser[]>([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const showAddDialog = ref(false)
const showRolePicker = ref(false)
const editingUser = ref<AdminUser | null>(null)

const formData = ref<AdminUser>({
  username: '',
  password: '',
  phone: '',
  email: '',
  realName: '',
  role: 'admin',
  status: 1
})

const roleOptions = [
  { text: '超级管理员', value: 'super_admin' },
  { text: '普通管理员', value: 'admin' }
]

// 加载用户列表
const loadUsers = async () => {
  try {
    const response = await request.get('/admin/users')
    users.value = response.data || []
    finished.value = true
  } catch (error) {
    showToast('加载失败')
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

// 下拉刷新
const onRefresh = () => {
  finished.value = false
  loading.value = true
  loadUsers()
}

// 切换用户状态
const toggleStatus = async (user: AdminUser) => {
  if (!user.id) return
  
  try {
    await request.post(`/admin/users/${user.id}/status`, null, {
      params: { status: user.status }
    })
    showToast(user.status === 1 ? '已启用' : '已禁用')
  } catch (error) {
    // 恢复原状态
    user.status = user.status === 1 ? 0 : 1
    showToast('操作失败')
  }
}

// 编辑用户
const editUser = (user: AdminUser) => {
  editingUser.value = user
  formData.value = {
    username: user.username,
    password: '',
    phone: user.phone || '',
    email: user.email || '',
    realName: user.realName || '',
    role: user.role,
    status: user.status
  }
  showAddDialog.value = true
}

// 保存用户
const saveUser = async () => {
  if (!formData.value.username) {
    showToast('请输入用户名')
    return
  }
  
  if (!editingUser.value && !formData.value.password) {
    showToast('请输入密码')
    return
  }
  
  try {
    if (editingUser.value && editingUser.value.id) {
      // 更新用户
      await request.put(`/admin/users/${editingUser.value.id}`, formData.value)
      showToast('更新成功')
    } else {
      // 创建用户
      await request.post('/admin/users', formData.value)
      showToast('创建成功')
    }
    
    showAddDialog.value = false
    resetForm()
    loadUsers()
  } catch (error: any) {
    showToast(error.response?.data?.message || '操作失败')
  }
}

// 删除用户
const deleteUser = async (id: number) => {
  try {
    await showConfirmDialog({
      title: '确认删除',
      message: '确定要删除该用户吗？此操作不可恢复！'
    })
    
    await request.delete(`/admin/users/${id}`)
    showToast('删除成功')
    loadUsers()
  } catch (error: any) {
    if (error !== 'cancel') {
      showToast(error.response?.data?.message || '删除失败')
    }
  }
}

// 角色选择确认
const onRoleConfirm = ({ selectedOptions }: any) => {
  formData.value.role = selectedOptions[0].value
  showRolePicker.value = false
}

// 重置表单
const resetForm = () => {
  editingUser.value = null
  formData.value = {
    username: '',
    password: '',
    phone: '',
    email: '',
    realName: '',
    role: 'admin',
    status: 1
  }
}

onMounted(() => {
  loadUsers()
})
</script>

<style scoped lang="scss">
.users-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 50px;
}

.user-item {
  margin-bottom: 8px;
  background: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  
  .user-name {
    font-size: 16px;
    font-weight: 500;
  }
}

.user-detail {
  font-size: 13px;
  color: #969799;
  line-height: 1.6;
}

.user-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
