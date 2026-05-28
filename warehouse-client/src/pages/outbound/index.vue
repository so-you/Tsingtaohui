<template>
  <view class="page">
    <view class="header"><text class="title">{{ t('outbound.title') }}</text></view>
    <view v-if="store.outboundTasks.length === 0" class="empty"><text>{{ t('outbound.noTasks') }}</text></view>
    <view v-for="task in store.outboundTasks" :key="task.orderNo" class="task-card">
      <view class="task-row"><text class="label">{{ t('outbound.orderNo') }}</text><text>{{ task.orderNo }}</text></view>
      <view class="task-row"><text class="label">{{ t('outbound.packageNo') }}</text><text>{{ task.packageNo }}</text></view>
      <view class="task-row"><text class="label">{{ t('outbound.customsStatus') }}</text>
        <text :class="task.customsBlocked ? 'text-error' : 'text-success'">
          {{ task.customsBlocked ? t('outbound.blocked') : t('outbound.customsOk') }}
        </text>
      </view>
      <view v-if="task.customsBlocked" class="blocked-msg">
        <text class="text-error">{{ t('outbound.customsBlocked') }}</text>
      </view>
      <view v-else class="confirm-btn" @tap="handleConfirm(task)">
        <text class="confirm-btn-text">{{ t('outbound.confirmOutbound') }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useWarehouseStore } from '../../stores/warehouse'
import type { IOutboundTask } from '../../types'

const { t } = useI18n()
const store = useWarehouseStore()

async function handleConfirm(task: IOutboundTask) {
  try {
    await store.confirmOutboundTask(task.taskId)
    uni.showToast({ title: 'Outbound confirmed', icon: 'success' })
  } catch (err: any) {
    uni.showToast({ title: err.message || 'Failed', icon: 'none' })
  }
}

onMounted(() => {
  store.fetchOutboundTasks()
})
</script>

<style scoped>
.page { padding: 12px; background: #F7F8FA; min-height: 100vh; }
.header { margin-bottom: 12px; }
.title { font-size: 18px; font-weight: 600; }
.empty { text-align: center; padding: 40px; color: #6B7280; }
.task-card { background: #fff; border-radius: 8px; padding: 12px; margin-bottom: 10px; }
.task-row { display: flex; justify-content: space-between; padding: 4px 0; font-size: 14px; }
.label { color: #6B7280; }
.text-error { color: #DC2626; }
.text-success { color: #16A34A; }
.blocked-msg { margin-top: 8px; padding: 8px; background: #FEF2F2; border-radius: 6px; text-align: center; }
.confirm-btn { margin-top: 8px; background: #1677FF; border-radius: 8px; padding: 10px; text-align: center; }
.confirm-btn-text { color: #fff; font-weight: 600; }
</style>
