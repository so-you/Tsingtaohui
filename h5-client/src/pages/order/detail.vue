<template>
  <view class="detail-page">
    <AppSkeleton :loading="loading">
      <template v-if="order">
        <view class="status-hero">
          <text class="status-text">{{ statusLabel(order.orderStatus) }}</text>
          <text class="order-no">{{ order.orderNo }}</text>
        </view>

        <view class="section">
          <text class="section-title">{{ $t('order.receiver') }}</text>
          <view class="info-row">
            <text class="label">{{ $t('cart.consigneeName') }}</text>
            <text class="value">{{ order.consigneeName }}</text>
          </view>
          <view class="info-row">
            <text class="label">{{ $t('cart.cabinNo') }}</text>
            <text class="value">{{ order.cabinNo }}</text>
          </view>
          <view class="info-row">
            <text class="label">{{ $t('ship.shipNo') }}</text>
            <text class="value">{{ order.shipNo }}</text>
          </view>
          <view class="info-row">
            <text class="label">{{ $t('ship.shipNationality') }}</text>
            <text class="value">{{ order.shipNationality }}</text>
          </view>
        </view>

        <view class="section">
          <text class="section-title">{{ $t('order.items') }}</text>
          <view v-for="item in order.items" :key="item.id || item.productId" class="order-item">
            <view class="item-main">
              <text class="item-name">{{ localItemName(item) }}</text>
              <text class="item-sku">{{ item.skuCode }}</text>
            </view>
            <view class="item-side">
              <text class="item-price">{{ $t('product.price', { price: item.lineAmount }) }}</text>
              <text class="item-count">x{{ item.quantity }}</text>
            </view>
          </view>
        </view>

        <view class="section">
          <text class="section-title">{{ $t('order.summary') }}</text>
          <view class="info-row">
            <text class="label">{{ $t('cart.tradeMode') }}</text>
            <text class="value">{{ tradeModeLabel(order.tradeMode) }}</text>
          </view>
          <view class="info-row">
            <text class="label">{{ $t('cart.totalWeight') }}</text>
            <text class="value">{{ order.totalWeightKg }} kg</text>
          </view>
          <view class="info-row">
            <text class="label">{{ $t('cart.totalVolume') }}</text>
            <text class="value">{{ order.totalVolumeM3 }} m³</text>
          </view>
          <view class="info-row total-row">
            <text class="label">{{ $t('cart.totalPrice') }}</text>
            <text class="total-price">{{ $t('product.price', { price: order.totalPrice }) }}</text>
          </view>
        </view>
      </template>

      <AppEmpty v-else-if="!loading" :title="$t('order.notFound')" emoji="📋" />
    </AppSkeleton>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import AppSkeleton from '../../components/AppSkeleton.vue'
import AppEmpty from '../../components/AppEmpty.vue'
import { getOrderDetail } from '../../api/order'
import type { IOrder, IOrderItem, TOrderStatus, TTradeMode } from '../../types'

const { locale, t } = useI18n()
const loading = ref(false)
const order = ref<IOrder | null>(null)

onLoad((query) => {
  const orderId = Number(query?.id)
  if (Number.isFinite(orderId) && orderId > 0) {
    loadOrder(orderId)
  }
})

async function loadOrder(orderId: number) {
  loading.value = true
  try {
    order.value = await getOrderDetail(orderId)
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
</script>

<style lang="scss" scoped>
@import '../../styles/theme.scss';

.detail-page {
  min-height: 100vh;
  padding: $space-md $space-lg $space-xl;
  background-color: $bg-page;
}

.status-hero {
  padding: $space-lg;
  border-radius: $radius-lg;
  background: $brand-gradient;
  color: #ffffff;
  box-shadow: $shadow-md;
}

.status-text {
  display: block;
  font-size: $font-xl;
  font-weight: $font-weight-bold;
}

.order-no {
  display: block;
  margin-top: $space-sm;
  font-size: $font-sm;
  opacity: 0.9;
}

.section {
  margin-top: $space-md;
  padding: $space-md;
  border-radius: $radius-md;
  background-color: $bg-card;
  box-shadow: $shadow-sm;
}

.section-title {
  display: block;
  margin-bottom: $space-sm;
  font-size: $font-md;
  font-weight: $font-weight-semibold;
  color: $text-primary;
}

.info-row,
.order-item {
  display: flex;
  justify-content: space-between;
  gap: $space-md;
  padding: 12rpx 0;
}

.label {
  font-size: $font-sm;
  color: $text-secondary;
}

.value {
  flex: 1;
  text-align: right;
  font-size: $font-sm;
  color: $text-primary;
}

.order-item + .order-item {
  border-top: 1rpx solid $divider-color;
}

.item-main {
  flex: 1;
  min-width: 0;
}

.item-name {
  display: block;
  font-size: $font-base;
  font-weight: $font-weight-semibold;
  color: $text-primary;
}

.item-sku,
.item-count {
  display: block;
  margin-top: $space-xs;
  font-size: $font-xs;
  color: $text-tertiary;
}

.item-side {
  text-align: right;
}

.item-price,
.total-price {
  font-size: $font-base;
  font-weight: $font-weight-bold;
  color: $text-price;
}

.total-row {
  border-top: 1rpx solid $divider-color;
  margin-top: $space-xs;
  padding-top: $space-md;
}
</style>
