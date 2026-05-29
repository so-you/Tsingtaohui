<template>
  <view class="empty-state">
    <view class="empty-state__icon">
      <text>{{ iconText }}</text>
    </view>
    <text class="empty-state__title">{{ title }}</text>
    <text v-if="description" class="empty-state__description">{{ description }}</text>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    title: string
    description?: string
    icon?: 'task' | 'scan' | 'inventory' | 'outbound'
  }>(),
  {
    description: '',
    icon: 'task',
  },
)

const iconText = computed(() => {
  const map = {
    task: 'TASK',
    scan: 'SCAN',
    inventory: 'INV',
    outbound: 'OUT',
  }
  return map[props.icon]
})
</script>

<style scoped lang="scss">
@use '../styles/theme-warehouse.scss' as *;

.empty-state {
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: center;
  padding: 72rpx $space-md;
  text-align: center;
}

.empty-state__icon {
  display: flex;
  width: 112rpx;
  height: 112rpx;
  align-items: center;
  justify-content: center;
  margin-bottom: $space-md;
  border-radius: $radius-lg;
  background: $warehouse-primary-bg;
  color: $warehouse-primary;
  font-size: $font-xs;
  font-weight: $font-weight-bold;
}

.empty-state__title {
  color: $text-secondary;
  font-size: $font-md;
  font-weight: $font-weight-semibold;
}

.empty-state__description {
  display: block;
  max-width: 520rpx;
  margin-top: $space-xs;
  color: $text-tertiary;
  font-size: $font-sm;
  line-height: 1.45;
}
</style>
