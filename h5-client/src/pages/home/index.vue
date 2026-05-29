<template>
  <view class="home-page">
    <!-- Sticky Header with Brand -->
    <view class="home-header">
      <view class="brand-row">
        <view class="brand-badge">
          <text class="brand-icon">🚁</text>
          <text class="brand-name">{{ $t('home.brandName') }}</text>
        </view>
        <view class="brand-tag">
          <text class="tag-text">{{ $t('home.brandTag') }}</text>
        </view>
      </view>
      <text class="subtitle">{{ $t('home.subtitle') }}</text>
    </view>

    <!-- Search Bar -->
    <view class="search-bar" @tap="handleSearch">
      <text class="search-icon">🔍</text>
      <text class="search-placeholder">{{ $t('home.search') }}</text>
      <view class="search-btn">
        <text class="search-btn-text">{{ $t('home.searchBtn') }}</text>
      </view>
    </view>

    <!-- Banner (decorative) -->
    <view class="banner-card">
      <view class="banner-content">
        <text class="banner-title">{{ $t('home.bannerTitle') }}</text>
        <text class="banner-desc">{{ $t('home.bannerDesc') }}</text>
      </view>
      <text class="banner-emoji">🚁</text>
    </view>

    <!-- Categories -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">{{ $t('home.categories') }}</text>
        <text class="section-more" @tap="handleSearch">{{ $t('home.viewAll') }}</text>
      </view>

      <AppSkeleton :loading="loading">
        <view class="category-grid">
          <view
            v-for="category in categories"
            :key="category.id"
            class="category-item"
            @tap="goCategory(category.id)"
          >
            <view class="category-icon" :style="{ backgroundColor: categoryColor(category.id) }">
              <text class="category-emoji">{{ categoryEmoji(category.nameZh) }}</text>
            </view>
            <text class="category-name">{{ localName(category) }}</text>
          </view>
        </view>
      </AppSkeleton>

      <AppEmpty
        v-if="!loading && categories.length === 0"
        :title="$t('product.noCategories')"
        emoji="📂"
      />
    </view>

    <!-- Recommended Products -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">{{ $t('home.recommend') }}</text>
        <text class="section-more" @tap="handleSearch">{{ $t('home.viewMore') }}</text>
      </view>

      <AppSkeleton :loading="loading">
        <view class="product-grid">
          <view
            v-for="product in products"
            :key="product.id"
            class="product-card"
            @tap="goProduct(product.id)"
          >
            <view class="product-image-wrapper">
              <image
                v-if="product.mainImageUrl"
                class="product-image"
                :src="product.mainImageUrl"
                mode="aspectFill"
                lazy-load
              />
              <text v-else class="image-placeholder">{{ $t('product.image') }}</text>
              <view v-if="product.droneDeliverable" class="product-badge">
                <text class="badge-text">{{ $t('home.deliverable') }}</text>
              </view>
            </view>
            <view class="product-body">
              <text class="product-name">{{ localName(product) }}</text>
              <text class="product-spec">{{ product.specification }}</text>
              <view class="product-footer">
                <view class="price-row">
                  <text class="price-symbol">¥</text>
                  <text class="price-value">{{ formatPrice(product.price) }}</text>
                </view>
                <view class="sales-tag">
                  <text class="sales-text">{{ $t('home.hotSale') }}</text>
                </view>
              </view>
            </view>
          </view>
        </view>
      </AppSkeleton>

      <AppEmpty
        v-if="!loading && products.length === 0"
        :title="$t('product.noProducts')"
        emoji="📦"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getCategories, getProducts } from '../../api/catalog'
import AppSkeleton from '../../components/AppSkeleton.vue'
import AppEmpty from '../../components/AppEmpty.vue'
import type { ICategory, IProduct } from '../../types'

const { locale, t } = useI18n()
const loading = ref(false)
const categories = ref<ICategory[]>([])
const products = ref<IProduct[]>([])

const CATEGORY_COLORS: Record<number, string> = {
  1: '#FFF3E0',
  2: '#E8F5E9',
  3: '#FCE4EC',
  4: '#E3F2FD',
  5: '#F3E5F5',
  6: '#E0F7FA',
  7: '#FFF8E1',
  8: '#E8EAF6',
}

const CATEGORY_EMOJIS: Record<string, string> = {
  '酒水饮料': '🍺',
  '方便食品': '🍜',
  '休闲零食': '🍪',
  '日用百货': '🧴',
  '防护用品': '😷',
  '新鲜果蔬': '🍎',
  '粮油调味': '🍚',
  '数码配件': '🔌',
}

onMounted(() => {
  loadHomeData()
})

async function loadHomeData() {
  loading.value = true
  try {
    const [categoryList, productPage] = await Promise.all([
      getCategories(),
      getProducts({ page: 1, page_size: 6 }),
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

function categoryColor(id: number) {
  return CATEGORY_COLORS[id] || '#f0f0f0'
}

function categoryEmoji(name: string) {
  return CATEGORY_EMOJIS[name] || '📦'
}

function formatPrice(price: string | number) {
  const n = typeof price === 'string' ? parseFloat(price) : price
  return Number.isFinite(n) ? n.toFixed(2) : price
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
@import '../../styles/theme.scss';

.home-page {
  padding: $space-md $space-lg $space-xl;
}

/* Header */
.home-header {
  margin-bottom: $space-md;
}

.brand-row {
  display: flex;
  align-items: center;
  gap: $space-sm;
  margin-bottom: $space-xs;
}

.brand-badge {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.brand-icon {
  font-size: 40rpx;
}

.brand-name {
  font-size: $font-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.brand-tag {
  padding: 4rpx 12rpx;
  background: $brand-gradient;
  border-radius: $radius-pill;
}

.tag-text {
  font-size: $font-xs;
  color: #ffffff;
  font-weight: $font-weight-medium;
}

.subtitle {
  display: block;
  font-size: $font-sm;
  color: $text-tertiary;
}

/* Search Bar */
.search-bar {
  display: flex;
  align-items: center;
  height: 80rpx;
  padding: 0 8rpx 0 $space-md;
  background-color: $bg-card;
  border-radius: $radius-pill;
  margin-bottom: $space-md;
  box-shadow: $shadow-sm;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
  color: $text-tertiary;
}

.search-placeholder {
  flex: 1;
  font-size: $font-sm;
  color: $text-placeholder;
}

.search-btn {
  padding: 0 $space-md;
  height: 64rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $brand-gradient;
  border-radius: $radius-pill;
}

.search-btn-text {
  font-size: $font-sm;
  color: #ffffff;
  font-weight: $font-weight-medium;
}

/* Banner */
.banner-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $space-lg;
  background: $brand-gradient;
  border-radius: $radius-lg;
  margin-bottom: $space-lg;
  box-shadow: $shadow-md;
}

.banner-content {
  display: flex;
  flex-direction: column;
}

.banner-title {
  font-size: $font-lg;
  font-weight: $font-weight-bold;
  color: #ffffff;
  margin-bottom: 8rpx;
}

.banner-desc {
  font-size: $font-sm;
  color: rgba(255, 255, 255, 0.85);
}

.banner-emoji {
  font-size: 80rpx;
}

/* Section */
.section {
  margin-bottom: $space-lg;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $space-md;
}

.section-title {
  font-size: $font-lg;
  font-weight: $font-weight-bold;
  color: $text-primary;
}

.section-more {
  font-size: $font-sm;
  color: $text-tertiary;
}

/* Category Grid */
.category-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $space-md $space-sm;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.category-icon {
  width: 108rpx;
  height: 108rpx;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $space-xs;
  transition: transform $transition-fast;
}

.category-item:active .category-icon {
  transform: scale(0.95);
}

.category-emoji {
  font-size: 48rpx;
}

.category-name {
  font-size: $font-xs;
  color: $text-secondary;
  text-align: center;
}

/* Product Grid */
.product-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: $space-sm;
}

.product-card {
  background-color: $bg-card;
  border-radius: $radius-md;
  overflow: hidden;
  box-shadow: $shadow-card;
  transition: transform $transition-fast;
}

.product-card:active {
  transform: scale(0.98);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 1:1 aspect ratio */
  background-color: $bg-input;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.image-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: $font-sm;
  color: $text-placeholder;
}

.product-badge {
  position: absolute;
  top: $space-xs;
  left: $space-xs;
  padding: 4rpx 10rpx;
  background: rgba(38, 170, 153, 0.9);
  border-radius: $radius-sm;
}

.badge-text {
  font-size: $font-xs;
  color: #ffffff;
  font-weight: $font-weight-medium;
}

.product-body {
  padding: $space-sm;
}

.product-name {
  display: block;
  font-size: $font-sm;
  font-weight: $font-weight-medium;
  color: $text-primary;
  line-height: 36rpx;
  min-height: 72rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-spec {
  display: block;
  margin-top: 4rpx;
  font-size: $font-xs;
  color: $text-tertiary;
}

.product-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: $space-sm;
}

.price-row {
  display: flex;
  align-items: baseline;
}

.price-symbol {
  font-size: $font-xs;
  color: $text-price;
  font-weight: $font-weight-bold;
}

.price-value {
  font-size: $font-md;
  color: $text-price;
  font-weight: $font-weight-bold;
}

.sales-tag {
  padding: 2rpx 8rpx;
  background-color: rgba(238, 77, 45, 0.08);
  border-radius: $radius-sm;
}

.sales-text {
  font-size: $font-xs;
  color: $brand-primary;
  font-weight: $font-weight-medium;
}
</style>
