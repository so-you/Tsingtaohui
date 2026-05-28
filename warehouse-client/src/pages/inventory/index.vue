<template>
  <view class="page">
    <view class="header"><text class="title">{{ t('inventory.title') }}</text></view>
    <view class="search-bar">
      <input class="search-input" :placeholder="t('inventory.search')" @confirm="handleSearch" />
    </view>
    <view v-if="store.inventoryItems.length === 0" class="empty"><text>{{ t('inventory.noResults') }}</text></view>
    <view v-for="item in store.inventoryItems" :key="item.id" class="inv-card">
      <view class="inv-row"><text class="label">{{ t('inventory.skuCode') }}</text><text>{{ item.skuCode }}</text></view>
      <view class="inv-row"><text class="label">{{ t('inventory.location') }}</text><text>{{ item.location }}</text></view>
      <view class="inv-row"><text class="label">{{ t('inventory.batch') }}</text><text>{{ item.batch }}</text></view>
      <view class="inv-row"><text class="label">{{ t('inventory.available') }}</text><text class="qty">{{ item.available }}</text></view>
      <view class="inv-row"><text class="label">{{ t('inventory.locked') }}</text><text class="qty locked">{{ item.locked }}</text></view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useWarehouseStore } from '../../stores/warehouse'

const { t } = useI18n()
const store = useWarehouseStore()

function handleSearch(_e: any) {
  store.fetchInventory()
}

onMounted(() => {
  store.fetchInventory()
})
</script>

<style scoped>
.page { padding: 12px; background: #F7F8FA; min-height: 100vh; }
.header { margin-bottom: 12px; }
.title { font-size: 18px; font-weight: 600; }
.search-bar { margin-bottom: 12px; }
.search-input { height: 40px; border: 1px solid #E5E7EB; border-radius: 8px; padding: 0 12px; font-size: 14px; background: #fff; }
.empty { text-align: center; padding: 40px; color: #6B7280; }
.inv-card { background: #fff; border-radius: 8px; padding: 12px; margin-bottom: 8px; }
.inv-row { display: flex; justify-content: space-between; padding: 3px 0; font-size: 13px; }
.label { color: #6B7280; }
.qty { font-weight: 600; }
.qty.locked { color: #F59E0B; }
</style>
