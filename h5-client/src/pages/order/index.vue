<template>
  <view class="order-page">
    <!-- Header -->
    <view class="order-header">
      <text class="page-title">{{ $t('order.title') }}</text>
    </view>

    <!-- Tabs -->
    <scroll-view scroll-x class="tabs-scroll hide-scrollbar">
      <view class="tabs">
        <view
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-item"
          :class="{ active: activeTab === tab.key }"
          @tap="activeTab = tab.key"
        >
          <text class="tab-text">{{ tab.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- Order List / Empty -->
    <view class="order-list">
      <AppEmpty
        v-if="!loading"
        :title="$t('order.noOrders')"
        emoji="📋"
        :description="$t('home.subtitle')"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import AppEmpty from '../../components/AppEmpty.vue'

const { t } = useI18n()
const activeTab = ref('all')
const loading = ref(false)

const tabs = computed(() => [
  { key: 'all', label: t('order.all') },
  { key: 'active', label: t('order.active') },
  { key: 'toReceive', label: t('order.toReceive') },
  { key: 'completed', label: t('order.completed') },
  { key: 'exception', label: t('order.exception') },
])
</script>

<style lang="scss" scoped>
@import '../../styles/theme.scss';

.order-page {
  padding: $space-md $space-lg $space-xl;
}

.order-header {
  margin-bottom: $space-md;
}

.page-title {
  font-size: $font-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.tabs-scroll {
  margin-bottom: $space-md;
}

.tabs {
  display: inline-flex;
  gap: $space-sm;
  padding: 4rpx 0;
}

.tab-item {
  padding: 12rpx 28rpx;
  border-radius: $radius-pill;
  background-color: $bg-card;
  border: 1rpx solid $border-color;
  transition: all $transition-fast;
}

.tab-item.active {
  background: $brand-gradient;
  border-color: transparent;
}

.tab-text {
  font-size: $font-sm;
  color: $text-secondary;
  white-space: nowrap;
}

.tab-item.active .tab-text {
  color: #ffffff;
  font-weight: $font-weight-medium;
}

.order-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $space-xl 0;
}
</style>
