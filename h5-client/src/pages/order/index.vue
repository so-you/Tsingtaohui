<template>
  <view class="order-page">
    <text class="page-title">{{ $t('order.title') }}</text>

    <view class="tabs">
      <text
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @tap="activeTab = tab.key"
      >
        {{ tab.label }}
      </text>
    </view>

    <view class="order-list">
      <text class="empty-text">{{ $t('order.noOrders') }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
const activeTab = ref('all')

const tabs = computed(() => [
  { key: 'all', label: t('order.all') },
  { key: 'active', label: t('order.active') },
  { key: 'toReceive', label: t('order.toReceive') },
  { key: 'completed', label: t('order.completed') },
  { key: 'exception', label: t('order.exception') },
])
</script>

<style lang="scss" scoped>
.order-page {
  padding: 24rpx 32rpx;
}

.page-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #1f2937;
  display: block;
  margin-bottom: 32rpx;
}

.tabs {
  display: flex;
  gap: 24rpx;
  margin-bottom: 32rpx;
  overflow-x: auto;
  white-space: nowrap;
}

.tab-item {
  font-size: 28rpx;
  color: #6b7280;
  padding: 12rpx 24rpx;
  border-radius: 8rpx;
  flex-shrink: 0;
}

.tab-item.active {
  color: #1677ff;
  background-color: #eff6ff;
  font-weight: 600;
}

.order-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 96rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #9ca3af;
}
</style>
