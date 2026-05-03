<template>
  <div class="base-card" :class="{ 'base-card--clickable': clickable }" @click="handleClick">
    <div class="base-card__header" v-if="title || $slots.header">
      <slot name="header">
        <div class="base-card__title">{{ title }}</div>
      </slot>
    </div>
    <div class="base-card__content">
      <slot>默认内容</slot>
    </div>
    <div class="base-card__footer" v-if="$slots.footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  title?: string
  clickable?: boolean
}

interface Emits {
  (e: 'click'): void
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  clickable: false
})

const emit = defineEmits<Emits>()

const handleClick = () => {
  if (props.clickable) {
    emit('click')
  }
}
</script>

<style scoped>
.base-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.base-card--clickable {
  cursor: pointer;
}

.base-card--clickable:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
  transform: translateY(-2px);
}

.base-card__header {
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.base-card__title {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
}

.base-card__content {
  color: #646566;
  font-size: 14px;
}

.base-card__footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}
</style>
