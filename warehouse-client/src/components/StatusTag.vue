<template>
  <text class="status-tag" :class="[`status-tag--${tone}`, `status-tag--${size}`]">
    {{ label }}
  </text>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'

const props = withDefaults(
  defineProps<{
    status: string
    size?: 'sm' | 'md'
  }>(),
  {
    size: 'md',
  },
)

const { t } = useI18n()

const statusMap: Record<string, { labelKey: string; tone: string }> = {
  HIGH: { labelKey: 'common.priorityHigh', tone: 'error' },
  NORMAL: { labelKey: 'common.priorityNormal', tone: 'primary' },
  PENDING_PICK: { labelKey: 'status.pendingPick', tone: 'warning' },
  PICKING: { labelKey: 'status.picking', tone: 'info' },
  PICKED: { labelKey: 'status.picked', tone: 'success' },
  PENDING_REVIEW: { labelKey: 'status.pendingReview', tone: 'warning' },
  REVIEWING: { labelKey: 'status.reviewing', tone: 'info' },
  PACKED: { labelKey: 'status.packed', tone: 'success' },
  PENDING_OUTBOUND: { labelKey: 'status.pendingOutbound', tone: 'warning' },
  OUTBOUNDED: { labelKey: 'status.outbounded', tone: 'success' },
  BLOCKED: { labelKey: 'status.blocked', tone: 'error' },
  SYNC_SUCCESS: { labelKey: 'status.syncSuccess', tone: 'success' },
  SYNC_FAILED: { labelKey: 'status.syncFailed', tone: 'error' },
  SYNC_PENDING: { labelKey: 'status.syncPending', tone: 'warning' },
  DRONE_ASSIGNED: { labelKey: 'status.droneAssigned', tone: 'success' },
  WAITING_DRONE: { labelKey: 'status.waitingDrone', tone: 'warning' },
  DRONE_BLOCKED: { labelKey: 'status.droneBlocked', tone: 'error' },
  ENOUGH: { labelKey: 'status.enough', tone: 'success' },
  LOW: { labelKey: 'status.low', tone: 'warning' },
  ZERO: { labelKey: 'status.zero', tone: 'error' },
}

const option = computed(() => statusMap[props.status] || { labelKey: props.status, tone: 'primary' })
const label = computed(() => t(option.value.labelKey))
const tone = computed(() => option.value.tone)
</script>

<style scoped lang="scss">
@use '../styles/theme-warehouse.scss' as *;

.status-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  max-width: 100%;
  border-radius: $radius-pill;
  font-weight: $font-weight-semibold;
  line-height: 1.2;
  white-space: nowrap;
}

.status-tag--sm {
  padding: 4rpx 10rpx;
  font-size: $font-xs;
}

.status-tag--md {
  padding: 6rpx 14rpx;
  font-size: $font-sm;
}

.status-tag--primary {
  background: $warehouse-primary-bg;
  color: $warehouse-primary;
}

.status-tag--success {
  background: $warehouse-success-bg;
  color: $warehouse-success;
}

.status-tag--warning {
  background: $warehouse-warning-bg;
  color: $warehouse-warning;
}

.status-tag--error {
  background: $warehouse-error-bg;
  color: $warehouse-error;
}

.status-tag--info {
  background: $warehouse-info-bg;
  color: $warehouse-info;
}
</style>
