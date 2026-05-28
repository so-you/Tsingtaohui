<template>
  <view class="catalog-page">
    <view class="search-row">
      <input
        v-model="keyword"
        class="search-input"
        type="text"
        :placeholder="$t('home.search')"
        :placeholder-style="'color: #9CA3AF'"
        confirm-type="search"
        @confirm="loadProducts"
      />
      <button class="search-button" @tap="loadProducts">{{ $t('common.search') }}</button>
    </view>

    <scroll-view scroll-x class="category-scroll">
      <view class="category-tabs">
        <text
          class="category-tab"
          :class="{ active: selectedCategoryId === undefined }"
          @tap="selectCategory(undefined)"
        >
          {{ $t('product.allCategories') }}
        </text>
        <text
          v-for="category in flatCategories"
          :key="category.id"
          class="category-tab"
          :class="{ active: selectedCategoryId === category.id }"
          @tap="selectCategory(category.id)"
        >
          {{ localName(category) }}
        </text>
      </view>
    </scroll-view>

    <view class="product-list">
      <view
        v-for="product in products"
        :key="product.id"
        class="product-card"
        @tap="goDetail(product.id)"
      >
        <view class="product-image">
          <image v-if="product.mainImageUrl" :src="product.mainImageUrl" mode="aspectFill" />
          <text v-else class="image-placeholder">{{ $t('product.image') }}</text>
        </view>
        <view class="product-info">
          <text class="product-name">{{ localName(product) }}</text>
          <text class="product-spec">{{ product.skuCode }}</text>
          <view class="product-stats">
            <text>{{ $t('product.weight', { weight: product.weightKg || '-' }) }}</text>
            <text>{{ $t('product.volume', { volume: product.volumeM3 || '-' }) }}</text>
          </view>
          <view class="product-bottom">
            <text class="product-price">{{ $t('product.price', { price: product.price }) }}</text>
            <text class="stock-text">{{ $t('product.stock', { qty: product.availableQty }) }}</text>
          </view>
          <text class="deliverable-tag" :class="{ disabled: !product.droneDeliverable }">
            {{ product.droneDeliverable ? $t('product.droneDeliverable') : $t('product.notDroneDeliverable') }}
          </text>
        </view>
      </view>
      <view v-if="!loading && products.length === 0" class="empty-state">
        <text class="empty-text">{{ $t('product.noProducts') }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { getCategories, getProducts } from '../../api/catalog'
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
</script>

<style lang="scss" scoped>
.catalog-page {
  padding: 24rpx 32rpx;
}

.search-row {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.search-input {
  flex: 1;
  height: 76rpx;
  padding: 0 24rpx;
  border-radius: 38rpx;
  background-color: #f3f4f6;
  font-size: 28rpx;
}

.search-button {
  width: 132rpx;
  height: 76rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 38rpx;
  background-color: #1677ff;
  color: #ffffff;
  font-size: 28rpx;
}

.category-scroll {
  white-space: nowrap;
  margin-bottom: 24rpx;
}

.category-tabs {
  display: inline-flex;
  gap: 16rpx;
}

.category-tab {
  padding: 12rpx 22rpx;
  border-radius: 8rpx;
  background-color: #ffffff;
  color: #6b7280;
  font-size: 26rpx;
}

.category-tab.active {
  color: #1677ff;
  background-color: #eff6ff;
  font-weight: 600;
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.product-card {
  display: flex;
  padding: 20rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
}

.product-image {
  width: 180rpx;
  height: 180rpx;
  border-radius: 12rpx;
  background-color: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  margin-right: 20rpx;
  flex-shrink: 0;
}

.product-image image {
  width: 100%;
  height: 100%;
}

.image-placeholder {
  font-size: 24rpx;
  color: #9ca3af;
}

.product-info {
  min-width: 0;
  flex: 1;
}

.product-name {
  display: block;
  font-size: 30rpx;
  font-weight: 600;
  color: #1f2937;
  line-height: 38rpx;
}

.product-spec,
.product-stats,
.stock-text {
  display: block;
  margin-top: 8rpx;
  font-size: 24rpx;
  color: #6b7280;
}

.product-stats {
  display: flex;
  flex-direction: column;
  gap: 4rpx;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12rpx;
}

.product-price {
  font-size: 32rpx;
  font-weight: 700;
  color: #dc2626;
}

.deliverable-tag {
  display: inline-flex;
  margin-top: 10rpx;
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

.empty-state {
  display: flex;
  justify-content: center;
  padding: 96rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #9ca3af;
}
</style>
