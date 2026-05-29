<template>
  <view class="warehouse-page inventory-page">
    <view class="warehouse-header">
      <view class="warehouse-title-group">
        <text class="warehouse-title">{{ t('inventory.title') }}</text>
        <text class="warehouse-subtitle">{{ t('inventory.subtitle') }}</text>
      </view>
    </view>

    <view class="inventory-search warehouse-card">
      <input
        v-model="keyword"
        class="inventory-search__input"
        :placeholder="t('inventory.search')"
        confirm-type="search"
        @confirm="searchInventory"
      />
      <view class="inventory-search__button" @tap="searchInventory">
        <text>{{ t('inventory.searchAction') }}</text>
      </view>
      <view class="inventory-search__clear" @tap="clearSearch">
        <text>{{ t('inventory.clear') }}</text>
      </view>
    </view>

    <EmptyState
      v-if="store.inventoryItems.length === 0"
      :title="t('inventory.noResults')"
      :description="t('inventory.noResultsDesc')"
      icon="inventory"
    />

    <view v-else class="inventory-list">
      <view
        v-for="item in store.inventoryItems"
        :key="item.id"
        class="inventory-card warehouse-card"
        :class="`inventory-card--${item.status.toLowerCase()}`"
      >
        <view class="inventory-card__head">
          <view>
            <text class="inventory-card__sku">{{ item.skuCode }}</text>
            <text class="inventory-card__name">{{ locale === 'zh-CN' ? item.productName : item.productNameEn }}</text>
          </view>
          <StatusTag :status="item.status" />
        </view>
        <view class="warehouse-meta-row">
          <text class="warehouse-meta-label">{{ t('inventory.location') }}</text>
          <text class="warehouse-meta-value">{{ item.location }}</text>
        </view>
        <view class="warehouse-meta-row">
          <text class="warehouse-meta-label">{{ t('inventory.batch') }}</text>
          <text class="warehouse-meta-value">{{ item.batch }}</text>
        </view>
        <view class="inventory-card__quantities">
          <view>
            <text class="inventory-card__qty">{{ item.availableQty }}</text>
            <text class="inventory-card__qty-label">{{ t('inventory.available') }}</text>
          </view>
          <view>
            <text class="inventory-card__qty inventory-card__qty--locked">{{ item.lockedQty }}</text>
            <text class="inventory-card__qty-label">{{ t('inventory.locked') }}</text>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import EmptyState from '../../components/EmptyState.vue'
import StatusTag from '../../components/StatusTag.vue'
import { useWarehouseStore } from '../../stores/warehouse'

const { t, locale } = useI18n()
const store = useWarehouseStore()
const keyword = ref('')

function searchInventory() {
  store.fetchInventory(keyword.value)
}

function clearSearch() {
  keyword.value = ''
  store.fetchInventory()
}

onShow(() => {
  store.fetchInventory(keyword.value)
})
</script>

<style scoped lang="scss">
@use '../../styles/theme-warehouse.scss' as *;

.inventory-search {
  display: grid;
  grid-template-columns: 1fr auto auto;
  gap: $space-xs;
  padding: $space-sm;
}

.inventory-search__input {
  min-width: 0;
  height: 76rpx;
  padding: 0 $space-sm;
  border-radius: $radius-sm;
  background: $bg-input;
  color: $text-primary;
  font-size: $font-sm;
}

.inventory-search__button,
.inventory-search__clear {
  display: flex;
  height: 76rpx;
  align-items: center;
  justify-content: center;
  padding: 0 $space-sm;
  border-radius: $radius-sm;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
}

.inventory-search__button {
  background: $warehouse-primary;
  color: #ffffff;
}

.inventory-search__clear {
  background: $warehouse-primary-bg;
  color: $warehouse-primary;
}

.inventory-list {
  display: flex;
  flex-direction: column;
  gap: $space-sm;
  margin-top: $space-md;
}

.inventory-card {
  overflow: hidden;
  padding: $space-md;
  border-left: 8rpx solid $warehouse-success;
}

.inventory-card--low {
  border-left-color: $warehouse-warning;
}

.inventory-card--zero {
  border-left-color: $warehouse-error;
}

.inventory-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: $space-sm;
  margin-bottom: $space-sm;
}

.inventory-card__sku,
.inventory-card__name {
  display: block;
}

.inventory-card__sku {
  color: $text-primary;
  font-size: $font-md;
  font-weight: $font-weight-bold;
}

.inventory-card__name {
  margin-top: 4rpx;
  color: $text-secondary;
  font-size: $font-sm;
}

.inventory-card__quantities {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: $space-sm;
  margin-top: $space-sm;
  padding-top: $space-sm;
  border-top: 1rpx solid $divider-color;
}

.inventory-card__qty,
.inventory-card__qty-label {
  display: block;
  text-align: center;
}

.inventory-card__qty {
  color: $warehouse-success;
  font-size: $font-xl;
  font-weight: $font-weight-bold;
}

.inventory-card__qty--locked {
  color: $warehouse-warning;
}

.inventory-card__qty-label {
  margin-top: 4rpx;
  color: $text-tertiary;
  font-size: $font-xs;
}
</style>
