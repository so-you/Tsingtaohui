<template>
  <view class="catalog-page">
    <!-- Sticky Search Bar -->
    <view class="search-sticky">
      <view class="search-bar" @tap="focusSearch">
        <text class="search-icon">🔍</text>
        <input
          ref="searchInput"
          v-model="keyword"
          class="search-input"
          type="text"
          :placeholder="$t('home.search')"
          confirm-type="search"
          @confirm="loadProducts"
        />
        <view v-if="keyword" class="search-clear" @tap.stop="clearSearch">
          <text class="clear-icon">✕</text>
        </view>
        <view class="search-btn" @tap.stop="loadProducts">
          <text class="search-btn-text">{{ $t('common.search') }}</text>
        </view>
      </view>
    </view>

    <!-- Category Tabs -->
    <scroll-view scroll-x class="category-scroll hide-scrollbar">
      <view class="category-tabs">
        <view
          class="category-tab"
          :class="{ active: selectedCategoryId === undefined }"
          @tap="selectCategory(undefined)"
        >
          <text class="tab-text">{{ $t('product.allCategories') }}</text>
        </view>
        <view
          v-for="category in flatCategories"
          :key="category.id"
          class="category-tab"
          :class="{ active: selectedCategoryId === category.id }"
          @tap="selectCategory(category.id)"
        >
          <text class="tab-text">{{ localName(category) }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- Product List -->
    <AppSkeleton :loading="loading">
      <view class="product-list">
        <view
          v-for="product in products"
          :key="product.id"
          class="product-card"
          @tap="goDetail(product.id)"
        >
          <view class="product-image-wrapper s-item">
            <image
              v-if="product.mainImageUrl"
              class="product-image"
              :src="product.mainImageUrl"
              mode="aspectFill"
              lazy-load
            />
            <text v-else class="image-placeholder">{{ $t('product.image') }}</text>
            <view v-if="product.droneDeliverable" class="product-badge">
              <text class="badge-text">可配送</text>
            </view>
          </view>
          <view class="product-info">
            <text class="product-name s-item">{{ localName(product) }}</text>
            <text class="product-spec">{{ product.specification }}</text>
            <view class="product-tags">
              <view class="tag tag-primary">
                <text class="tag-text">保税仓</text>
              </view>
              <view v-if="product.droneDeliverable" class="tag tag-success">
                <text class="tag-text">无人机</text>
              </view>
            </view>
            <view class="product-bottom">
              <view class="price-row">
                <text class="price-symbol">¥</text>
                <text class="price-value">{{ formatPrice(product.price) }}</text>
              </view>
              <text class="stock-text">库存 {{ product.availableQty }}</text>
            </view>
          </view>
        </view>

        <AppEmpty
          v-if="!loading && products.length === 0"
          :title="$t('product.noProducts')"
          emoji="📦"
        />
      </view>
    </AppSkeleton>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { getCategories, getProducts } from '../../api/catalog'
import AppSkeleton from '../../components/AppSkeleton.vue'
import AppEmpty from '../../components/AppEmpty.vue'
import type { ICategory, IProduct } from '../../types'

const { locale, t } = useI18n()
const loading = ref(false)
const keyword = ref('')
const selectedCategoryId = ref<number | undefined>()
const categories = ref<ICategory[]>([])
const products = ref<IProduct[]>([])
const initialized = ref(false)

const flatCategories = computed(() => {
  const result: ICategory[] = []
  categories.value.forEach((category) => {
    result.push(category)
    category.children?.forEach((child) => result.push(child))
  })
  return result
})

onLoad((query) => {
  const categoryId = Number(query?.categoryId)
  if (Number.isFinite(categoryId) && categoryId > 0) {
    selectedCategoryId.value = categoryId
  }
})

onMounted(async () => {
  try {
    categories.value = await getCategories()
    consumePendingCategory()
  } catch {
    uni.showToast({ title: t('common.error'), icon: 'none' })
  } finally {
    initialized.value = true
  }
  await loadProducts()
})

onShow(() => {
  if (initialized.value && consumePendingCategory()) {
    loadProducts()
  }
})

function localName(item: ICategory | IProduct) {
  return locale.value === 'en-US' ? item.nameEn || item.nameZh : item.nameZh
}

function selectCategory(categoryId?: number) {
  selectedCategoryId.value = categoryId
  loadProducts()
}

async function loadProducts() {
  loading.value = true
  try {
    const page = await getProducts({
      category_id: selectedCategoryId.value,
      keyword: keyword.value.trim() || undefined,
      page: 1,
      page_size: 20,
    })
    products.value = page.items
  } catch {
    uni.showToast({ title: t('common.error'), icon: 'none' })
  } finally {
    loading.value = false
  }
}

function consumePendingCategory() {
  try {
    const raw = localStorage.getItem('catalog:selectedCategoryId')
    if (!raw) return false
    localStorage.removeItem('catalog:selectedCategoryId')
    const categoryId = Number(raw)
    if (!Number.isFinite(categoryId) || categoryId <= 0) return false
    selectedCategoryId.value = categoryId
    return true
  } catch {
    return false
  }
}

function goDetail(productId: number) {
  uni.navigateTo({ url: `/pages/catalog/detail?id=${productId}` })
}

function clearSearch() {
  keyword.value = ''
  loadProducts()
}

function focusSearch() {
  // noop - input is already visible; tap on input focuses it naturally
}

function formatPrice(price: string | number) {
  const n = typeof price === 'string' ? parseFloat(price) : price
  return Number.isFinite(n) ? n.toFixed(2) : price
}
</script>

<style lang="scss" scoped>
@import '../../styles/theme.scss';

.catalog-page {
  padding: $space-md $space-lg $space-xl;
}

/* Search Bar */
.search-sticky {
  position: sticky;
  top: 0;
  z-index: 10;
  background-color: $bg-page;
  padding-top: $space-xs;
  padding-bottom: $space-sm;
}

.search-bar {
  display: flex;
  align-items: center;
  height: 80rpx;
  padding: 0 8rpx 0 $space-md;
  background-color: $bg-card;
  border-radius: $radius-pill;
  box-shadow: $shadow-sm;
}

.search-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
  color: $text-tertiary;
}

.search-input {
  flex: 1;
  height: 100%;
  font-size: $font-sm;
  color: $text-primary;
}

.search-clear {
  padding: 0 12rpx;
  display: flex;
  align-items: center;
}

.clear-icon {
  font-size: 24rpx;
  color: $text-tertiary;
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

/* Category Tabs */
.category-scroll {
  margin-bottom: $space-md;
}

.category-tabs {
  display: inline-flex;
  gap: $space-sm;
  padding: 4rpx 0;
}

.category-tab {
  padding: 12rpx 24rpx;
  border-radius: $radius-pill;
  background-color: $bg-card;
  border: 1rpx solid $border-color;
  transition: all $transition-fast;
}

.category-tab.active {
  background: $brand-gradient;
  border-color: transparent;
}

.tab-text {
  font-size: $font-sm;
  color: $text-secondary;
  white-space: nowrap;
}

.category-tab.active .tab-text {
  color: #ffffff;
  font-weight: $font-weight-medium;
}

/* Product List */
.product-list {
  display: flex;
  flex-direction: column;
  gap: $space-sm;
}

.product-card {
  display: flex;
  padding: $space-sm;
  background-color: $bg-card;
  border-radius: $radius-md;
  box-shadow: $shadow-card;
  transition: transform $transition-fast;
}

.product-card:active {
  transform: scale(0.98);
}

.product-image-wrapper {
  position: relative;
  width: 200rpx;
  height: 200rpx;
  border-radius: $radius-sm;
  background-color: $bg-input;
  overflow: hidden;
  flex-shrink: 0;
  margin-right: $space-sm;
}

.product-image {
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

.product-info {
  min-width: 0;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: $font-md;
  font-weight: $font-weight-semibold;
  color: $text-primary;
  line-height: 40rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-spec {
  margin-top: 4rpx;
  font-size: $font-xs;
  color: $text-tertiary;
}

.product-tags {
  display: flex;
  gap: 8rpx;
  margin-top: $space-xs;
  flex-wrap: wrap;
}

.tag {
  display: inline-flex;
  align-items: center;
  padding: 2rpx 10rpx;
  border-radius: $radius-sm;
  font-size: $font-xs;
  font-weight: $font-weight-medium;
}

.tag-primary {
  background-color: rgba(238, 77, 45, 0.08);
  color: $brand-primary;
}

.tag-success {
  background-color: rgba(38, 170, 153, 0.08);
  color: $secondary-green;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
  padding-top: $space-xs;
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
  font-size: $font-lg;
  color: $text-price;
  font-weight: $font-weight-bold;
}

.stock-text {
  font-size: $font-xs;
  color: $text-tertiary;
}
</style>
