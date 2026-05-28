<template>
  <view class="order-page">
    <view class="order-header">
      <text class="page-title">{{ $t('order.title') }}</text>
    </view>

    <scroll-view scroll-x class="tabs-scroll hide-scrollbar">
      <view class="tabs">
        <view
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-item"
          :class="{ active: activeTab === tab.key }"
          @tap="selectTab(tab.key)"
        >
          <text class="tab-text">{{ tab.label }}</text>
        </view>
      </view>
    </scroll-view>

    <AppSkeleton :loading="loading">
      <view class="order-list">
        <view
          v-for="order in filteredOrders"
          :key="order.id"
          class="order-card"
          @tap="goDetail(order.id)"
        >
          <view class="order-card-header">
            <text class="order-no">{{ order.orderNo }}</text>
            <text class="status-tag" :class="statusClass(order.orderStatus)">
              {{ statusLabel(order.orderStatus) }}
            </text>
          </view>
          <view class="order-meta">
            <text>{{ order.shipNo }}</text>
            <text>{{ order.cabinNo }}</text>
            <text>{{ tradeModeLabel(order.tradeMode) }}</text>
          </view>
          <view v-if="order.items?.length" class="item-preview">
            <text class="item-name">{{ localItemName(order.items[0]) }}</text>
            <text class="item-count">x{{ order.items[0].quantity }}</text>
          </view>
          <view class="order-bottom">
            <text class="order-time">{{ formatTime(order.createdAt) }}</text>
            <text class="order-price">{{ $t('product.price', { price: order.totalPrice }) }}</text>
          </view>
        </view>

        <AppEmpty
          v-if="!loading && filteredOrders.length === 0"
          :title="$t('order.noOrders')"
          :description="$t('cart.emptyDesc')"
          emoji="📋"
        />
      </view>
    </AppSkeleton>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import AppSkeleton from '../../components/AppSkeleton.vue'
import AppEmpty from '../../components/AppEmpty.vue'
import { getOrders } from '../../api/order'
import { useUserStore } from '../../stores/user'
import type { IOrder, IOrderItem, TOrderStatus, TTradeMode } from '../../types'

type TabKey = 'all' | 'active' | 'toReceive' | 'completed' | 'exception'

const { locale, t } = useI18n()
const userStore = useUserStore()
const activeTab = ref<TabKey>('all')
const loading = ref(false)
const orders = ref<IOrder[]>([])

const tabs = computed(() => [
  { key: 'all' as TabKey, label: t('order.all') },
  { key: 'active' as TabKey, label: t('order.active') },
  { key: 'toReceive' as TabKey, label: t('order.toReceive') },
  { key: 'completed' as TabKey, label: t('order.completed') },
  { key: 'exception' as TabKey, label: t('order.exception') },
])

const filteredOrders = computed(() => {
  if (activeTab.value === 'active') {
    return orders.value.filter((order) => !['COMPLETED', 'CANCELLED', 'EXCEPTION'].includes(order.orderStatus))
  }
  return orders.value
})

onShow(() => {
  if (!userStore.isLoggedIn) {
    orders.value = []
    return
  }
  loadOrders()
})

function selectTab(key: TabKey) {
  activeTab.value = key
  loadOrders()
}

function tabStatus(): TOrderStatus | undefined {
  if (activeTab.value === 'toReceive') return 'PENDING_RECEIPT'
  if (activeTab.value === 'completed') return 'COMPLETED'
  if (activeTab.value === 'exception') return 'EXCEPTION'
  return undefined
}

async function loadOrders() {
  loading.value = true
  try {
    const page = await getOrders({
      status: tabStatus(),
      page: 1,
      page_size: 20,
    })
    orders.value = page.items
  } catch {
    uni.showToast({ title: t('common.error'), icon: 'none' })
  } finally {
    loading.value = false
  }
}

function localItemName(item: IOrderItem) {
  return locale.value === 'en-US' ? item.productNameEn || item.productNameZh : item.productNameZh
}

function tradeModeLabel(mode: TTradeMode) {
  return mode === 'AUTO_TRADE' ? t('cart.autoTrade') : t('cart.matchingOrder')
}

function statusLabel(status: TOrderStatus) {
  return t(`order.status.${status}`)
}

function statusClass(status: TOrderStatus) {
  if (status === 'COMPLETED') return 'success'
  if (status === 'EXCEPTION' || status === 'CANCELLED') return 'danger'
  if (status === 'PENDING_RECEIPT') return 'warning'
  return 'primary'
}

function formatTime(value?: string) {
  return value ? value.replace('T', ' ').slice(0, 16) : ''
}

function goDetail(orderId: number) {
  uni.navigateTo({ url: `/pages/order/detail?id=${orderId}` })
}
</script>

<style lang="scss" scoped>
@import '../../styles/theme.scss';

.order-page {
  min-height: 100vh;
  padding: $space-md $space-lg $space-xl;
  background-color: $bg-page;
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
  gap: $space-sm;
}

.order-card {
  padding: $space-md;
  background-color: $bg-card;
  border-radius: $radius-md;
  box-shadow: $shadow-sm;
}

.order-card-header,
.order-bottom,
.item-preview {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.order-no {
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
  color: $text-primary;
}

.status-tag {
  padding: 6rpx 14rpx;
  border-radius: $radius-pill;
  font-size: $font-xs;
  color: #ffffff;
  background-color: $text-link;
}

.status-tag.success {
  background-color: $secondary-green;
}

.status-tag.warning {
  background-color: $secondary-orange;
}

.status-tag.danger {
  background-color: #d93026;
}

.order-meta {
  display: flex;
  flex-wrap: wrap;
  gap: $space-sm;
  margin-top: $space-sm;
  font-size: $font-xs;
  color: $text-secondary;
}

.item-preview {
  margin-top: $space-md;
  padding: $space-sm;
  border-radius: $radius-sm;
  background-color: $bg-surface;
  font-size: $font-sm;
  color: $text-primary;
}

.item-name {
  flex: 1;
  min-width: 0;
}

.item-count {
  margin-left: $space-sm;
  color: $text-secondary;
}

.order-bottom {
  margin-top: $space-sm;
}

.order-time {
  font-size: $font-xs;
  color: $text-tertiary;
}

.order-price {
  font-size: $font-md;
  font-weight: $font-weight-bold;
  color: $text-price;
}
</style>
