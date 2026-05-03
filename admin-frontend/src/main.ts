import { createApp } from 'vue'
import { createPinia } from 'pinia'
import Vant from 'vant'
import 'vant/lib/index.css'
import App from './App.vue'
import router from './router'

// 全局错误捕获
window.addEventListener('error', (event) => {
  console.error('Global error:', event.error)
})

window.addEventListener('unhandledrejection', (event) => {
  console.error('Unhandled rejection:', event.reason)
})

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)
app.use(Vant)

// 挂载时捕获错误
try {
  app.mount('#app')
  console.log('✅ Vue app mounted successfully')
} catch (error) {
  console.error(' Failed to mount Vue app:', error)
  document.getElementById('app')!.innerHTML = `<div style="padding:20px;color:red;">应用加载失败: ${error}</div>`
}
