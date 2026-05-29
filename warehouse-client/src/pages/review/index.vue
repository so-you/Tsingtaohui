<template>
  <view class="warehouse-page">
    <view class="warehouse-header">
      <view class="warehouse-title-group">
        <text class="warehouse-title">{{ t('review.title') }}</text>
        <text class="warehouse-subtitle">{{ t('review.subtitle') }}</text>
      </view>
    </view>

    <ScanInput
      :title="t('review.scanProduct')"
      :hint="t('dashboard.scanMode')"
      :mode-label="t('dashboard.scanMode')"
      :placeholder="t('review.scanProduct')"
      border-color="#F59E0B"
      :feedback="scanFeedback"
      @scan="handleScan"
    />

    <view class="warehouse-section">
      <EmptyState
        v-if="store.reviewTasks.length === 0"
        :title="t('review.noTasks')"
        :description="t('review.noTasksDesc')"
        icon="scan"
      />
      <view v-else>
        <TaskCard
          v-for="task in store.reviewTasks"
          :key="task.taskId"
          :task="task"
          type="review"
        >
          <view
            class="review-pack-button"
            :class="{ 'review-pack-button--disabled': task.scannedQty < task.expectedQty || task.status === 'PACKED' }"
            @tap="handlePack(task)"
          >
            <text>{{ task.packageNo || t('review.pack') }}</text>
          </view>
        </TaskCard>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import EmptyState from '../../components/EmptyState.vue'
import ScanInput from '../../components/ScanInput.vue'
import TaskCard from '../../components/TaskCard.vue'
import { useWarehouseStore } from '../../stores/warehouse'
import type { IReviewTask } from '../../types'
import type { IScanHistoryItem } from '../../utils/scanner'
import { createScanHistoryItem } from '../../utils/scanner'

const { t } = useI18n()
const store = useWarehouseStore()
const scanFeedback = ref<IScanHistoryItem | null>(null)

function errorMessage(error: any, fallbackKey: string) {
  const message = error?.message || fallbackKey
  return message.includes('.') ? t(message) : message
}

async function handleScan(code: string) {
  try {
    const result = await store.scanReviewProduct(code)
    scanFeedback.value = createScanHistoryItem(code, result, t(result.messageKey))
    uni.showToast({ title: t(result.messageKey), icon: 'success' })
  } catch (error: any) {
    const message = errorMessage(error, 'review.scanMismatch')
    scanFeedback.value = {
      code,
      status: error?.code === 'duplicate' || error?.code === 'overflow' ? error.code : 'failed',
      message,
      scannedAt: new Date().toISOString(),
    }
    uni.showToast({ title: message, icon: 'none' })
  }
}

async function handlePack(task: IReviewTask) {
  if (task.scannedQty < task.expectedQty || task.status === 'PACKED') return
  try {
    const packageNo = await store.packReviewOrder(task.taskId)
    uni.showToast({ title: `${t('review.packageGenerated')}: ${packageNo}`, icon: 'success' })
  } catch (error: any) {
    uni.showToast({ title: errorMessage(error, 'review.packBlocked'), icon: 'none' })
  }
}

onShow(() => {
  store.fetchReviewTasks()
})
</script>

<style scoped lang="scss">
@use '../../styles/theme-warehouse.scss' as *;

.review-pack-button {
  display: flex;
  min-height: 76rpx;
  align-items: center;
  justify-content: center;
  margin-top: $space-md;
  border-radius: $radius-md;
  background: $warehouse-gradient;
  color: #ffffff;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
}

.review-pack-button--disabled {
  background: #e2e8f0;
  color: $text-tertiary;
}
</style>
