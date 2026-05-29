<template>
  <view class="warehouse-page">
    <view class="warehouse-header">
      <view class="warehouse-title-group">
        <text class="warehouse-title">{{ t('picking.title') }}</text>
        <text class="warehouse-subtitle">{{ t('picking.subtitle') }}</text>
      </view>
    </view>

    <ScanInput
      :title="t('picking.scanToConfirm')"
      :hint="t('dashboard.scanMode')"
      :mode-label="t('dashboard.scanMode')"
      :placeholder="t('picking.scanToConfirm')"
      border-color="#16A34A"
      :feedback="scanFeedback"
      @scan="handleScan"
    />

    <view class="warehouse-section">
      <EmptyState
        v-if="store.pickingTasks.length === 0"
        :title="t('picking.noTasks')"
        :description="t('picking.noTasksDesc')"
        icon="task"
      />
      <view v-else>
        <TaskCard
          v-for="task in store.pickingTasks"
          :key="task.taskId"
          :task="task"
          type="picking"
        />
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
    const result = await store.scanPicking(code)
    scanFeedback.value = createScanHistoryItem(code, result, t(result.messageKey))
    uni.showToast({ title: t(result.messageKey), icon: 'success' })
  } catch (error: any) {
    const message = errorMessage(error, 'picking.scanMismatch')
    scanFeedback.value = {
      code,
      status: error?.code === 'duplicate' || error?.code === 'overflow' ? error.code : 'failed',
      message,
      scannedAt: new Date().toISOString(),
    }
    uni.showToast({ title: message, icon: 'none' })
  }
}

onShow(() => {
  store.fetchPickingTasks()
})
</script>
