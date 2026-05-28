<template>
  <view class="page">
    <view class="header"><text class="title">{{ t('review.title') }}</text></view>
    <view class="scan-area">
      <input class="scan-input" :placeholder="t('review.scanProduct')" focus confirm-type="done" @confirm="handleScan" />
    </view>
    <view v-if="store.reviewTasks.length === 0" class="empty"><text>{{ t('review.noTasks') }}</text></view>
    <view v-for="task in store.reviewTasks" :key="task.orderNo" class="task-card">
      <view class="task-row"><text class="label">{{ t('review.orderNo') }}</text><text>{{ task.orderNo }}</text></view>
      <view class="task-row">
        <text class="label">{{ t('review.scannedQty') }}/{{ t('review.expectedQty') }}</text>
        <text>{{ task.scannedQty }}/{{ task.expectedQty }}</text>
      </view>
      <view v-if="task.scannedQty >= task.expectedQty" class="pack-btn" @tap="handlePack(task)">
        <text class="pack-btn-text">{{ t('review.pack') }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useWarehouseStore } from '../../stores/warehouse'
import type { IReviewTask } from '../../types'

const { t } = useI18n()
const store = useWarehouseStore()

async function handleScan(e: any) {
  const skuCode = e.detail.value
  if (!skuCode || store.reviewTasks.length === 0) return
  try {
    await store.scanReviewProduct(store.reviewTasks[0].taskId, skuCode)
    uni.showToast({ title: 'OK', icon: 'success' })
  } catch (err: any) {
    uni.showToast({ title: err.message || 'Scan failed', icon: 'none' })
  }
}

async function handlePack(task: IReviewTask) {
  try {
    const packageNo = await store.packReviewOrder(task.taskId)
    uni.showToast({ title: `Packed: ${packageNo}`, icon: 'success' })
  } catch (err: any) {
    uni.showToast({ title: err.message || 'Pack failed', icon: 'none' })
  }
}

onMounted(() => {
  store.fetchReviewTasks()
})
</script>

<style scoped>
.page { padding: 12px; background: #F7F8FA; min-height: 100vh; }
.header { margin-bottom: 12px; }
.title { font-size: 18px; font-weight: 600; }
.scan-area { margin-bottom: 16px; }
.scan-input { height: 44px; border: 2px solid #F59E0B; border-radius: 8px; padding: 0 12px; font-size: 16px; background: #fff; }
.empty { text-align: center; padding: 40px; color: #6B7280; }
.task-card { background: #fff; border-radius: 8px; padding: 12px; margin-bottom: 10px; }
.task-row { display: flex; justify-content: space-between; padding: 4px 0; font-size: 14px; }
.label { color: #6B7280; }
.pack-btn { margin-top: 8px; background: #1677FF; border-radius: 8px; padding: 10px; text-align: center; }
.pack-btn-text { color: #fff; font-weight: 600; }
</style>
