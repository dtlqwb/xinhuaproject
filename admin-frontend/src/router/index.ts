import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/views/Login.vue'
import Dashboard from '@/views/Dashboard.vue'
import Customers from '@/views/Customers.vue'
import Reports from '@/views/Reports.vue'
import Plans from '@/views/Plans.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: Login },
    { path: '/', redirect: '/dashboard', meta: { requiresAuth: true } },
    { path: '/dashboard', component: Dashboard, meta: { requiresAuth: true } },
    { path: '/customers', component: Customers, meta: { requiresAuth: true } },
    { path: '/reports', component: Reports, meta: { requiresAuth: true } },
    { path: '/plans', component: Plans, meta: { requiresAuth: true } }
  ]
})

router.beforeEach((to: any, from: any, next: any) => {
  const token = localStorage.getItem('admin_token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
