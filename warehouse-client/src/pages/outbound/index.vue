<template>
  <view class="warehouse-page">
    <view class="warehouse-header">
      <view class="warehouse-title-group">
        <text class="warehouse-title">{{ t('outbound.title') }}</text>
        <text class="warehouse-subtitle">{{ t('outbound.subtitle') }}</text>
      </view>
    </view>

    <EmptyState
      v-if="store.outboundTasks.length === 0"
      :title="t('outbound.noTasks')"
      :description="t('outbound.noTasksDesc')"
      icon="outbound"
    />

    <view v-else class="outbound-list">
      <TaskCard v-for="task in store.outboundTasks" :key="task.taskId" :task="task" type="outbound">
        <view v-if="task.customsBlocked" class="outbound-block">
          <text>{{ t('outbound.customsBlocked') }}</text>
        </view>
        <view v-else class="outbound-ok">
          <text>{{ t('outbound.customsOk') }}</text>
        </view>
        <view
          class="outbound-button"
          :class="{ 'outbound-button--disabled': task.customsBlocked || task.status === 'OUTBOUNDED' }"
          @tap="handleConfirm(task)"
        >
          <text>{{ t('outbound.confirmOutbound') }}</text>
        </view>
      </TaskCard>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import EmptyState from '../../components/EmptyState.vue'
import TaskCard from '../../components/TaskCard.vue'
import { useWarehouseStore } from '../../stores/warehouse'
import type { IOutboundTask } from '../../types'

const { t } = useI18n()
const store = useWarehouseStore()

function errorMessage(error: any, fallbackKey: string) {
  const message = error?.message || fallbackKey
  return message.includes('.') ? t(message) : message
}

async function handleConfirm(task: IOutboundTask) {
  if (task.customsBlocked || task.status === 'OUTBOUNDED') return
  try {
    await store.confirmOutboundTask(task.taskId)
    uni.showToast({ title: t('outbound.outboundSuccess'), icon: 'success' })
  } catch (error: any) {
    uni.showToast({ title: errorMessage(error, 'common.error'), icon: 'none' })
  }
}

onShow(() => {
  store.fetchOutboundTasks()
})
</script>

<style scoped lang="scss">
@use '../../styles/theme-warehouse.scss' as *;

.outbound-list {
  display: flex;
  flex-direction: column;
  gap: $space-sm;
}

.outbound-block,
.outbound-ok {
  margin-top: $space-md;
  padding: $space-sm;
  border-radius: $radius-md;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
  line-height: 1.4;
}

.outbound-block {
  background: $warehouse-error-bg;
  color: $warehouse-error;
}

.outbound-ok {
  background: $warehouse-success-bg;
  color: $warehouse-success;
}

.outbound-button {
  display: flex;
  min-height: 76rpx;
  align-items: center;
  justify-content: center;
  margin-top: $space-sm;
  border-radius: $radius-md;
  background: $warehouse-gradient;
  color: #ffffff;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
}

.outbound-button--disabled {
  background: #e2e8f0;
  color: $text-tertiary;
}
</style>
