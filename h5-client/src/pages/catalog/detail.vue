<template>
  <view class="detail-page">
    <view v-if="product" class="product-detail">
      <view class="hero-image">
        <image v-if="product.mainImageUrl" :src="product.mainImageUrl" mode="aspectFill" />
        <text v-else class="image-placeholder">{{ $t('product.image') }}</text>
      </view>

      <view class="summary">
        <text class="product-name">{{ localName(product) }}</text>
        <text class="product-price">{{ $t('product.price', { price: product.price }) }}</text>
        <text class="product-sku">{{ $t('product.sku', { sku: product.skuCode }) }}</text>
        <view class="deliverable-row">
          <text class="deliverable-tag" :class="{ disabled: !product.droneDeliverable }">
            {{ product.droneDeliverable ? $t('product.droneDeliverable') : $t('product.notDroneDeliverable') }}
          </text>
          <text class="stock-text">{{ $t('product.stock', { qty: product.availableQty }) }}</text>
        </view>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('product.specifications') }}</text>
        <view class="spec-row">
          <text class="spec-label">{{ $t('product.weight', { weight: product.weightKg || '-' }) }}</text>
          <text class="spec-label">{{ $t('product.volume', { volume: product.volumeM3 || '-' }) }}</text>
        </view>
        <text v-if="product.specification" class="spec-text">{{ product.specification }}</text>
        <text v-if="product.source" class="spec-text">{{ $t('product.source', { source: product.source }) }}</text>
      </view>

      <view class="section">
        <text class="section-title">{{ $t('product.description') }}</text>
        <text class="description-text">{{ description || $t('product.noDescription') }}</text>
      </view>

      <view class="bottom-actions">
        <button class="btn-outline" @tap="addToCart">{{ $t('product.addToCart') }}</button>
        <button class="btn-primary" @tap="buyNow">{{ $t('product.buyNow') }}</button>
      </view>
    </view>

    <view v-else-if="!loading" class="empty-state">
      <text class="empty-text">{{ $t('common.error') }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { getProductDetail } from '../../api/catalog'
import { useCartStore } from '../../stores/cart'
import type { IProduct } from '../../types'

const { locale, t } = useI18n()
const cartStore = useCartStore()
const loading = ref(false)
const product = ref<IProduct | null>(null)

const description = computed(() => {
  const item = product.value
  if (!item) return ''
  return locale.value === 'en-US'
    ? item.descriptionEn || item.descriptionZh || ''
    : item.descriptionZh || item.descriptionEn || ''
})

onLoad((query) => {
  const productId = Number(query?.id)
  if (Number.isFinite(productId) && productId > 0) {
    loadProduct(productId)
  }
})

function localName(item: IProduct) {
  return locale.value === 'en-US' ? item.nameEn || item.nameZh : item.nameZh
}

async function loadProduct(productId: number) {
  loading.value = true
  try {
    product.value = await getProductDetail(productId)
  } catch {
    uni.showToast({ title: t('common.error'), icon: 'none' })
  } finally {
    loading.value = false
  }
}

function addToCart() {
  if (!product.value) return
  cartStore.addProduct(product.value, 1)
  uni.showToast({ title: t('product.added'), icon: 'success' })
}

function buyNow() {
  if (!product.value) return
  cartStore.addProduct(product.value, 1)
  uni.navigateTo({ url: '/pages/cart/index' })
}
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  padding-bottom: 144rpx;
  background-color: #f7f8fa;
}

.product-detail {
  padding-bottom: 32rpx;
}

.hero-image {
  width: 100%;
  height: 560rpx;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-image image {
  width: 100%;
  height: 100%;
}

.image-placeholder,
.empty-text {
  font-size: 28rpx;
  color: #9ca3af;
}

.summary,
.section {
  margin: 20rpx 24rpx 0;
  padding: 28rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
}

.product-name {
  display: block;
  font-size: 36rpx;
  font-weight: 700;
  line-height: 46rpx;
  color: #111827;
}

.product-price {
  display: block;
  margin-top: 18rpx;
  font-size: 42rpx;
  font-weight: 700;
  color: #dc2626;
}

.product-sku {
  display: block;
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #6b7280;
}

.deliverable-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 18rpx;
}

.deliverable-tag {
  display: inline-flex;
  padding: 6rpx 12rpx;
  border-radius: 8rpx;
  background-color: #ecfdf5;
  color: #16a34a;
  font-size: 24rpx;
}

.deliverable-tag.disabled {
  background-color: #f3f4f6;
  color: #9ca3af;
}

.stock-text,
.spec-text,
.description-text {
  font-size: 26rpx;
  line-height: 40rpx;
  color: #6b7280;
}

.section-title {
  display: block;
  margin-bottom: 18rpx;
  font-size: 30rpx;
  font-weight: 600;
  color: #1f2937;
}

.spec-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 12rpx;
}

.spec-label {
  flex: 1;
  font-size: 26rpx;
  color: #4b5563;
}

.spec-text {
  display: block;
  margin-top: 8rpx;
}

.description-text {
  display: block;
}

.bottom-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  gap: 20rpx;
  padding: 20rpx 24rpx 32rpx;
  background-color: #ffffff;
  box-shadow: 0 -6rpx 24rpx rgba(15, 23, 42, 0.08);
}

.btn-outline,
.btn-primary {
  flex: 1;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12rpx;
  font-size: 30rpx;
  font-weight: 600;
}

.btn-outline {
  background-color: #ffffff;
  color: #1677ff;
  border: 2rpx solid #1677ff;
}

.btn-primary {
  background-color: #1677ff;
  color: #ffffff;
}

.empty-state {
  display: flex;
  justify-content: center;
  padding-top: 180rpx;
}
</style>
