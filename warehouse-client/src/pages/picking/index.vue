<template>
  <view class="page">
    <view class="header">
      <text class="title">{{ t('picking.title') }}</text>
    </view>

    <view class="scan-area">
      <input
        class="scan-input"
        :placeholder="t('picking.scanToConfirm')"
        focus
        confirm-type="done"
        @confirm="handleScan"
      />
    </view>

    <view v-if="store.pickingTasks.length === 0" class="empty">
      <text>{{ t('picking.noTasks') }}</text>
    </view>

    <view v-for="task in store.pickingTasks" :key="task.orderNo" class="task-card">
      <view class="task-row"><text class="label">{{ t('picking.orderNo') }}</text><text>{{ task.orderNo }}</text></view>
      <view class="task-row"><text class="label">{{ t('picking.product') }}</text><text>{{ task.productName }}</text></view>
      <view class="task-row"><text class="label">{{ t('picking.quantity') }}</text><text>{{ task.quantity }}</text></view>
      <view class="task-row"><text class="label">{{ t('picking.location') }}</text><text>{{ task.location }}</text></view>
      <view class="task-row"><text class="label">{{ t('picking.batch') }}</text><text>{{ task.batch }}</text></view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useWarehouseStore } from '../../stores/warehouse'

const { t } = useI18n()
const store = useWarehouseStore()

async function handleScan(e: any) {
  const skuCode = e.detail.value
  if (!skuCode || store.pickingTasks.length === 0) return
  try {
    await store.scanPicking(store.pickingTasks[0].taskId, skuCode)
    uni.showToast({ title: 'OK', icon: 'success' })
  } catch (err: any) {
    uni.showToast({ title: err.message || 'Scan failed', icon: 'none' })
  }
}

onMounted(() => {
  store.fetchPickingTasks()
})
</script>

<style scoped>
.page { padding: 12px; background: #F7F8FA; min-height: 100vh; }
.header { margin-bottom: 12px; }
.title { font-size: 18px; font-weight: 600; }
.scan-area { margin-bottom: 16px; }
.scan-input { height: 44px; border: 2px solid #16A34A; border-radius: 8px; padding: 0 12px; font-size: 16px; background: #fff; }
.empty { text-align: center; padding: 40px; color: #6B7280; }
.task-card { background: #fff; border-radius: 8px; padding: 12px; margin-bottom: 10px; }
.task-row { display: flex; justify-content: space-between; padding: 4px 0; font-size: 14px; }
.label { color: #6B7280; }
</style>
