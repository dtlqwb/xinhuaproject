import { createRouter, createWebHistory } from 'vue-router'
import { showToast } from 'vant'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'
import Customers from '@/views/Customers.vue'
import Reports from '@/views/Reports.vue'
import Plans from '@/views/Plans.vue'
import Users from '@/views/Users.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    { path: '/', redirect: '/dashboard', meta: { requiresAuth: true } },
    { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
    { path: '/customers', component: Customers, meta: { requiresAuth: true } },
    { path: '/reports', component: Reports, meta: { requiresAuth: true } },
    { path: '/plans', component: Plans, meta: { requiresAuth: true } },
    { path: '/users', component: Users, meta: { requiresAuth: true, role: 'super_admin' } }
  ]
})

router.beforeEach((to: any, from: any, next: any) => {
  const token = localStorage.getItem('admin_token')
  const role = localStorage.getItem('admin_role')
  
  // 需要登录的页面
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  // 验证角色权限
  if (to.meta.role && to.meta.role !== role) {
    showToast('权限不足，无法访问该页面')
    next('/dashboard') // 权限不足，跳转到首页
    return
  }
  
  next()
})

export default router
