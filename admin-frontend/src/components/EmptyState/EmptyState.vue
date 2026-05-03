<template>
  <div class="empty-state">
    <van-empty :description="description" :image="image">
      <template v-if="showButton">
        <van-button
          round
          type="primary"
          class="bottom-button"
          @click="handleClick"
        >
          {{ buttonText }}
        </van-button>
      </template>
    </van-empty>
  </div>
</template>

<script setup lang="ts">
interface Props {
  description?: string
  image?: string
  showButton?: boolean
  buttonText?: string
}

interface Emits {
  (e: 'click'): void
}

const props = withDefaults(defineProps<Props>(), {
  description: '暂无数据',
  image: 'default',
  showButton: false,
  buttonText: '操作'
})

const emit = defineEmits<Emits>()

const handleClick = () => {
  emit('click')
}
</script>

<style scoped>
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 400px;
  padding: 20px;
}

.bottom-button {
  margin-top: 24px;
  width: 160px;
}
</style>
