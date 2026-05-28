<template>
  <view class="empty-state">
    <view class="empty-illustration">
      <text class="empty-emoji">{{ emoji }}</text>
    </view>
    <text class="empty-title">{{ title }}</text>
    <text v-if="description" class="empty-desc">{{ description }}</text>
    <slot name="action" />
  </view>
</template>

<script setup lang="ts">
defineProps<{
  title: string
  description?: string
  emoji?: string
}>()
</script>

<style lang="scss" scoped>
@import '../styles/theme.scss';

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $space-xl 0;
}

.empty-illustration {
  width: 200rpx;
  height: 200rpx;
  margin-bottom: $space-md;
  background: $bg-surface;
  border-radius: $radius-xl;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-emoji {
  font-size: 96rpx;
  line-height: 1;
}

.empty-title {
  font-size: $font-md;
  font-weight: $font-weight-semibold;
  color: $text-secondary;
  margin-bottom: $space-xs;
}

.empty-desc {
  font-size: $font-sm;
  color: $text-tertiary;
}
</style>
