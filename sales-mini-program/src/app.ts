<script setup lang="ts">
import { onLaunch, onShow, onHide } from '@tarojs/taro'
import pinia from './stores'

// 初始化 Pinia
pinia

onLaunch(() => {
  console.log('App Launch')
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss">
page {
  background-color: #f5f5f5;
  font-size: 28px;
}
</style>
