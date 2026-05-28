<template>
  <view class="home-page">
    <view class="home-header">
      <text class="greeting">{{ $t('home.title') }}</text>
      <text class="subtitle">{{ $t('home.subtitle') }}</text>
    </view>

    <view class="search-bar" @tap="handleSearch">
      <text class="search-placeholder">{{ $t('home.search') }}</text>
    </view>

    <view class="section">
      <text class="section-title">{{ $t('home.categories') }}</text>
      <view class="category-grid">
        <view
          v-for="category in categories"
          :key="category.id"
          class="category-item"
          @tap="goCategory(category.id)"
        >
          <view class="category-icon" />
          <text class="category-name">{{ localName(category) }}</text>
        </view>
        <view v-if="!loading && categories.length === 0" class="empty-inline">
          <text class="empty-text">{{ $t('product.noCategories') }}</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">{{ $t('home.recommend') }}</text>
      <view class="product-grid">
        <view
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @tap="goProduct(product.id)"
        >
          <view class="product-image">
            <image v-if="product.mainImageUrl" :src="product.mainImageUrl" mode="aspectFill" />
            <text v-else class="image-placeholder">{{ $t('product.image') }}</text>
          </view>
          <text class="product-name">{{ localName(product) }}</text>
          <text class="product-price">{{ $t('product.price', { price: product.price }) }}</text>
          <view class="product-meta">
            <text class="deliverable-tag" :class="{ disabled: !product.droneDeliverable }">
              {{ product.droneDeliverable ? $t('product.droneDeliverable') : $t('product.notDroneDeliverable') }}
            </text>
          </view>
        </view>
        <view v-if="!loading && products.length === 0" class="empty-inline">
          <text class="empty-text">{{ $t('product.noProducts') }}</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getCategories, getProducts } from '../../api/catalog'
import type { ICategory, IProduct } from '../../types'

const { locale, t } = useI18n()
const loading = ref(false)
const categories = ref<ICategory[]>([])
const products = ref<IProduct[]>([])

onMounted(() => {
  loadHomeData()
})

async function loadHomeData() {
  loading.value = true
  try {
    const [categoryList, productPage] = await Promise.all([
      getCategories(),
      getProducts({ page: 1, page_size: 4 }),
    ])
    categories.value = categoryList.slice(0, 8)
    products.value = productPage.items
  } catch {
    uni.showToast({ title: t('common.error'), icon: 'none' })
  } finally {
    loading.value = false
  }
}

function localName(item: ICategory | IProduct) {
  return locale.value === 'en-US' ? item.nameEn || item.nameZh : item.nameZh
}

function handleSearch() {
  uni.switchTab({ url: '/pages/catalog/index' })
}

function goCategory(categoryId: number) {
  try {
    localStorage.setItem('catalog:selectedCategoryId', String(categoryId))
  } catch {
    // ignore storage errors
  }
  uni.switchTab({ url: '/pages/catalog/index' })
}

function goProduct(productId: number) {
  uni.navigateTo({ url: `/pages/catalog/detail?id=${productId}` })
}
</script>

<style lang="scss" scoped>
.home-page {
  padding: 24rpx 32rpx;
}

.home-header {
  margin-bottom: 32rpx;
}

.greeting {
  font-size: 40rpx;
  font-weight: 700;
  color: #1f2937;
}

.subtitle {
  display: block;
  margin-top: 8rpx;
  font-size: 26rpx;
  color: #6b7280;
}

.search-bar {
  display: flex;
  align-items: center;
  height: 80rpx;
  padding: 0 32rpx;
  background-color: #f3f4f6;
  border-radius: 40rpx;
  margin-bottom: 40rpx;
}

.search-placeholder {
  font-size: 28rpx;
  color: #9ca3af;
}

.section {
  margin-bottom: 40rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 24rpx;
  display: block;
}

.category-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 140rpx;
}

.category-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  background-color: #e5e7eb;
  margin-bottom: 12rpx;
}

.category-name {
  font-size: 24rpx;
  color: #6b7280;
}

.product-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 0;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 24rpx;
}

.product-card {
  min-width: 0;
  padding: 20rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
}

.product-image {
  height: 180rpx;
  margin-bottom: 16rpx;
  border-radius: 12rpx;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.product-image image {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  font-size: 24rpx;
  color: #9ca3af;
}

.product-name {
  display: block;
  min-height: 72rpx;
  font-size: 28rpx;
  font-weight: 600;
  color: #1f2937;
  line-height: 36rpx;
}

.product-price {
  display: block;
  margin-top: 10rpx;
  font-size: 32rpx;
  font-weight: 700;
  color: #dc2626;
}

.product-meta {
  margin-top: 12rpx;
}

.deliverable-tag {
  display: inline-flex;
  padding: 4rpx 10rpx;
  border-radius: 8rpx;
  background-color: #ecfdf5;
  color: #16a34a;
  font-size: 22rpx;
}

.deliverable-tag.disabled {
  background-color: #f3f4f6;
  color: #9ca3af;
}

.empty-inline {
  grid-column: 1 / -1;
  display: flex;
  justify-content: center;
  padding: 48rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #9ca3af;
}
</style>
