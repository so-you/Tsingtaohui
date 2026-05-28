<template>
  <view class="cart-page">
    <view class="page-header">
      <text class="page-title">{{ $t('cart.title') }}</text>
      <text v-if="cartItems.length" class="page-subtitle">{{ $t('cart.itemsCount', { count: cartStore.totalQuantity }) }}</text>
    </view>

    <template v-if="cartItems.length">
      <view class="cart-list">
        <view v-for="item in cartItems" :key="item.productId" class="cart-item">
          <view class="item-image">
            <image v-if="item.mainImageUrl" :src="item.mainImageUrl" mode="aspectFill" />
            <text v-else class="image-placeholder">{{ $t('product.image') }}</text>
          </view>
          <view class="item-main">
            <text class="item-name">{{ localCartName(item) }}</text>
            <text class="item-sku">{{ item.skuCode }}</text>
            <view class="item-bottom">
              <text class="item-price">{{ $t('product.price', { price: item.price }) }}</text>
              <view class="qty-stepper">
                <view class="qty-btn" @tap="changeQuantity(item.productId, item.quantity - 1)">−</view>
                <text class="qty-value">{{ item.quantity }}</text>
                <view class="qty-btn" @tap="changeQuantity(item.productId, item.quantity + 1)">+</view>
              </view>
            </view>
          </view>
          <view class="remove-btn" @tap="removeItem(item.productId)">
            <text>×</text>
          </view>
        </view>
      </view>

      <view class="section">
        <view class="section-title-row">
          <text class="section-title">{{ $t('cart.estimate') }}</text>
          <text v-if="estimate" class="trade-mode" :class="{ matching: estimate.tradeMode === 'MATCHING_ORDER' }">
            {{ tradeModeLabel(estimate.tradeMode) }}
          </text>
        </view>
        <view class="summary-row">
          <text>{{ $t('cart.totalPrice') }}</text>
          <text class="summary-price">{{ $t('product.price', { price: estimate?.totalPrice || cartTotal }) }}</text>
        </view>
        <view class="summary-row">
          <text>{{ $t('cart.totalWeight') }}</text>
          <text>{{ estimate?.totalWeightKg || '-' }} kg</text>
        </view>
        <view class="summary-row">
          <text>{{ $t('cart.totalVolume') }}</text>
          <text>{{ estimate?.totalVolumeM3 || '-' }} m³</text>
        </view>
        <view v-if="estimate?.reasons?.length" class="reason-list">
          <text v-for="reason in estimate.reasons" :key="reason" class="reason-text">{{ reasonLabel(reason) }}</text>
        </view>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('cart.receiverInfo') }}</text>
        <view class="form-field">
          <text class="field-label">{{ $t('cart.consigneeName') }}</text>
          <input v-model="form.consigneeName" class="field-input" :placeholder="$t('cart.consigneePlaceholder')" />
        </view>
        <view class="form-field">
          <text class="field-label">{{ $t('cart.cabinNo') }}</text>
          <input v-model="form.cabinNo" class="field-input" :placeholder="$t('cart.cabinPlaceholder')" />
        </view>
        <view class="form-field">
          <text class="field-label">{{ $t('cart.contactInfo') }}</text>
          <input v-model="form.contactInfo" class="field-input" :placeholder="$t('cart.contactPlaceholder')" />
        </view>
        <view class="form-field">
          <text class="field-label">{{ $t('cart.remark') }}</text>
          <textarea v-model="form.remark" class="field-textarea" :placeholder="$t('cart.remarkPlaceholder')" />
        </view>
      </view>

      <view class="bottom-bar">
        <view class="bottom-total">
          <text class="bottom-label">{{ $t('cart.payable') }}</text>
          <text class="bottom-price">{{ $t('product.price', { price: estimate?.totalPrice || cartTotal }) }}</text>
        </view>
        <button class="submit-btn" :disabled="submitting" @tap="submitOrder">
          {{ submitting ? $t('common.loading') : $t('cart.submit') }}
        </button>
      </view>
    </template>

    <AppEmpty
      v-else
      :title="$t('cart.empty')"
      :description="$t('cart.emptyDesc')"
      emoji="🛒"
    />
  </view>
</template>

<script setup lang="ts">
import { computed, reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import AppEmpty from '../../components/AppEmpty.vue'
import { createOrder, estimateCart } from '../../api/order'
import { useCartStore } from '../../stores/cart'
import { useUserStore } from '../../stores/user'
import type { ICartItem, IOrderEstimate, TTradeMode } from '../../types'

const { locale, t } = useI18n()
const cartStore = useCartStore()
const userStore = useUserStore()
const estimate = ref<IOrderEstimate | null>(null)
const submitting = ref(false)

const form = reactive({
  consigneeName: '',
  cabinNo: '',
  contactInfo: '',
  remark: '',
})

const cartItems = computed(() => cartStore.items)
const cartTotal = computed(() => {
  const amount = cartStore.items.reduce((sum, item) => sum + Number(item.price || 0) * item.quantity, 0)
  return amount.toFixed(2)
})

onShow(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchProfile().then(prefillForm).catch(() => undefined)
  }
  refreshEstimate()
})

function prefillForm() {
  const user = userStore.userInfo
  if (!form.consigneeName) {
    form.consigneeName = user?.displayName || user?.username || ''
  }
  if (!form.contactInfo) {
    form.contactInfo = user?.contactPhone || user?.email || ''
  }
}

function localCartName(item: ICartItem) {
  return locale.value === 'en-US' ? item.nameEn || item.nameZh : item.nameZh
}

function estimatePayload() {
  return cartStore.items.map((item) => ({
    productId: item.productId,
    quantity: item.quantity,
  }))
}

async function refreshEstimate() {
  if (!cartStore.items.length || !userStore.isLoggedIn) {
    estimate.value = null
    return
  }
  try {
    estimate.value = await estimateCart(estimatePayload())
  } catch {
    estimate.value = null
  }
}

function changeQuantity(productId: number, quantity: number) {
  if (quantity < 1) {
    removeItem(productId)
  } else {
    cartStore.updateQuantity(productId, quantity)
  }
  refreshEstimate()
}

function removeItem(productId: number) {
  cartStore.removeItem(productId)
  refreshEstimate()
}

function tradeModeLabel(mode: TTradeMode) {
  return mode === 'AUTO_TRADE' ? t('cart.autoTrade') : t('cart.matchingOrder')
}

function reasonLabel(reason: string) {
  if (reason.startsWith('insufficient_stock')) return t('cart.reasonStock')
  if (reason.startsWith('not_drone_deliverable')) return t('cart.reasonDrone')
  if (reason.includes('weight')) return t('cart.reasonWeight')
  if (reason.includes('volume')) return t('cart.reasonVolume')
  return reason
}

function ensureLogin() {
  if (userStore.isLoggedIn) return true
  uni.navigateTo({ url: '/pages/auth/login' })
  return false
}

function ensureShip() {
  const user = userStore.userInfo
  const shipNo = user?.shipNo || user?.ships?.find((item) => item.isDefault)?.shipNo
  const shipNationality = user?.shipNationality || user?.ships?.find((item) => item.isDefault)?.shipNationality
  if (shipNo && shipNationality) return true
  uni.showModal({
    title: t('ship.title'),
    content: t('cart.shipRequired'),
    confirmText: t('ship.save'),
    cancelText: t('common.cancel'),
    success: (res) => {
      if (res.confirm) {
        uni.navigateTo({ url: '/pages/mine/ship' })
      }
    },
  })
  return false
}

async function submitOrder() {
  if (!ensureLogin() || !ensureShip()) return
  if (!form.consigneeName.trim()) {
    uni.showToast({ title: t('cart.consigneeRequired'), icon: 'none' })
    return
  }
  if (!form.cabinNo.trim()) {
    uni.showToast({ title: t('cart.cabinRequired'), icon: 'none' })
    return
  }

  submitting.value = true
  try {
    const user = userStore.userInfo
    const ship = user?.ships?.find((item) => item.isDefault) || user?.ships?.[0]
    const order = await createOrder({
      items: estimatePayload(),
      consigneeName: form.consigneeName.trim(),
      cabinNo: form.cabinNo.trim(),
      contactInfo: form.contactInfo.trim() || undefined,
      remark: form.remark.trim() || undefined,
      shipNo: user?.shipNo || ship?.shipNo,
      shipName: user?.shipName || ship?.shipName,
      shipNationality: user?.shipNationality || user?.nationality || ship?.shipNationality,
      imo: user?.imo || ship?.imo,
      mmsi: user?.mmsi || ship?.mmsi,
    })
    cartStore.clear()
    uni.showToast({ title: t('cart.submitSuccess'), icon: 'success' })
    uni.redirectTo({ url: `/pages/order/detail?id=${order.id}` })
  } catch {
    uni.showToast({ title: t('common.error'), icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style lang="scss" scoped>
@import '../../styles/theme.scss';

.cart-page {
  min-height: 100vh;
  padding: $space-md $space-lg 150rpx;
  background-color: $bg-page;
}

.page-header {
  margin-bottom: $space-md;
}

.page-title {
  display: block;
  font-size: $font-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.page-subtitle {
  display: block;
  margin-top: $space-xs;
  font-size: $font-sm;
  color: $text-secondary;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: $space-sm;
}

.cart-item,
.section {
  background-color: $bg-card;
  border-radius: $radius-md;
  box-shadow: $shadow-sm;
}

.cart-item {
  position: relative;
  display: flex;
  gap: $space-sm;
  padding: $space-sm;
}

.item-image {
  width: 150rpx;
  height: 150rpx;
  border-radius: $radius-sm;
  overflow: hidden;
  background-color: $bg-surface;
  display: flex;
  align-items: center;
  justify-content: center;
}

.item-image image {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  font-size: $font-xs;
  color: $text-tertiary;
}

.item-main {
  flex: 1;
  min-width: 0;
  padding-right: 40rpx;
}

.item-name {
  display: block;
  font-size: $font-base;
  font-weight: $font-weight-semibold;
  line-height: 38rpx;
  color: $text-primary;
}

.item-sku {
  display: block;
  margin-top: $space-xs;
  font-size: $font-xs;
  color: $text-tertiary;
}

.item-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: $space-md;
}

.item-price,
.summary-price,
.bottom-price {
  color: $text-price;
  font-weight: $font-weight-bold;
}

.qty-stepper {
  display: flex;
  align-items: center;
  border: 1rpx solid $border-color;
  border-radius: $radius-sm;
  overflow: hidden;
}

.qty-btn,
.qty-value {
  min-width: 56rpx;
  height: 52rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-sm;
}

.qty-btn {
  background-color: $bg-surface;
  color: $text-primary;
}

.remove-btn {
  position: absolute;
  right: $space-sm;
  top: $space-sm;
  width: 36rpx;
  height: 36rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  color: $text-tertiary;
}

.section {
  margin-top: $space-md;
  padding: $space-md;
}

.section-title-row,
.summary-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.section-title {
  display: block;
  margin-bottom: $space-sm;
  font-size: $font-md;
  font-weight: $font-weight-semibold;
  color: $text-primary;
}

.trade-mode {
  padding: 6rpx 12rpx;
  border-radius: $radius-pill;
  font-size: $font-xs;
  color: #ffffff;
  background-color: $secondary-green;
}

.trade-mode.matching {
  background-color: $secondary-orange;
}

.summary-row {
  padding: 10rpx 0;
  font-size: $font-sm;
  color: $text-secondary;
}

.reason-list {
  margin-top: $space-xs;
}

.reason-text {
  display: block;
  font-size: $font-xs;
  line-height: 34rpx;
  color: $secondary-orange;
}

.form-field {
  margin-top: $space-sm;
}

.field-label {
  display: block;
  margin-bottom: $space-xs;
  font-size: $font-sm;
  color: $text-secondary;
}

.field-input,
.field-textarea {
  width: 100%;
  box-sizing: border-box;
  border-radius: $radius-sm;
  background-color: $bg-input;
  color: $text-primary;
  font-size: $font-base;
}

.field-input {
  height: 76rpx;
  padding: 0 $space-sm;
}

.field-textarea {
  height: 130rpx;
  padding: $space-sm;
}

.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $space-sm;
  padding: $space-sm $space-lg 34rpx;
  background-color: $bg-card;
  box-shadow: 0 -4rpx 20rpx rgba(0, 0, 0, 0.06);
}

.bottom-total {
  display: flex;
  flex-direction: column;
}

.bottom-label {
  font-size: $font-xs;
  color: $text-tertiary;
}

.submit-btn {
  width: 260rpx;
  height: 84rpx;
  border-radius: $radius-pill;
  background: $brand-gradient;
  color: #ffffff;
  font-size: $font-base;
  font-weight: $font-weight-semibold;
}

.submit-btn[disabled] {
  opacity: 0.65;
}
</style>
