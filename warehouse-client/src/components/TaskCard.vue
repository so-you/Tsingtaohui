<template>
  <view class="task-card warehouse-card">
    <view class="task-card__head">
      <view class="task-card__title-group">
        <text class="task-card__order">{{ task.orderNo }}</text>
        <text class="task-card__subtitle">{{ subtitle }}</text>
      </view>
      <view class="task-card__tags">
        <StatusTag v-if="priority" :status="priority" size="sm" />
        <StatusTag :status="task.status" size="sm" />
      </view>
    </view>

    <view v-if="type === 'picking' && pickingTask" class="task-card__body">
      <view class="task-card__product-line">
        <text class="task-card__product">{{ localizedProductName }}</text>
        <text class="task-card__quantity">{{ pickingTask.pickedQty }}/{{ pickingTask.quantity }}</text>
      </view>
      <view class="warehouse-meta-row">
        <text class="warehouse-meta-label">{{ t('picking.location') }}</text>
        <text class="warehouse-meta-value">{{ pickingTask.location }}</text>
      </view>
      <view class="warehouse-meta-row">
        <text class="warehouse-meta-label">{{ t('picking.batch') }}</text>
        <text class="warehouse-meta-value">{{ pickingTask.batch }}</text>
      </view>
      <view class="warehouse-meta-row">
        <text class="warehouse-meta-label">{{ t('picking.expectedTime') }}</text>
        <text class="warehouse-meta-value">{{ pickingTask.expectedDeliveryTime }}</text>
      </view>
    </view>

    <view v-if="type === 'review' && reviewTask" class="task-card__body">
      <view class="task-card__progress-head">
        <text>{{ t('review.scanProgress') }}</text>
        <text>{{ reviewTask.scannedQty }}/{{ reviewTask.expectedQty }}</text>
      </view>
      <view class="task-card__progress">
        <view class="task-card__progress-bar" :style="{ width: `${reviewProgress}%` }" />
      </view>
      <view v-for="item in reviewTask.items" :key="item.skuCode" class="task-card__item-row">
        <text class="task-card__item-name">{{ locale === 'zh-CN' ? item.productName : item.productNameEn }}</text>
        <text class="task-card__item-count">{{ item.scannedQty }}/{{ item.quantity }}</text>
      </view>
    </view>

    <view v-if="type === 'outbound' && outboundTask" class="task-card__body">
      <view class="warehouse-meta-row">
        <text class="warehouse-meta-label">{{ t('outbound.packageNo') }}</text>
        <text class="warehouse-meta-value">{{ outboundTask.packageNo }}</text>
      </view>
      <view class="warehouse-meta-row">
        <text class="warehouse-meta-label">{{ t('outbound.weight') }}</text>
        <text class="warehouse-meta-value">{{ outboundTask.weightKg }}kg / {{ outboundTask.volumeM3 }}m3</text>
      </view>
      <view class="task-card__status-row">
        <StatusTag :status="outboundTask.customsStatus" />
        <StatusTag :status="outboundTask.droneStatus" />
      </view>
    </view>

    <slot />
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import type { IOutboundTask, IPickingTask, IReviewTask } from '../types'
import StatusTag from './StatusTag.vue'

const props = defineProps<{
  task: IPickingTask | IReviewTask | IOutboundTask
  type: 'picking' | 'review' | 'outbound'
}>()

const { t, locale } = useI18n()

const pickingTask = computed(() => (props.type === 'picking' ? (props.task as IPickingTask) : null))
const reviewTask = computed(() => (props.type === 'review' ? (props.task as IReviewTask) : null))
const outboundTask = computed(() => (props.type === 'outbound' ? (props.task as IOutboundTask) : null))
const priority = computed(() => ('priority' in props.task ? props.task.priority : ''))

const localizedProductName = computed(() => {
  if (!pickingTask.value) return ''
  return locale.value === 'zh-CN' ? pickingTask.value.productName : pickingTask.value.productNameEn
})

const subtitle = computed(() => {
  if (pickingTask.value) return pickingTask.value.skuCode
  if (reviewTask.value) return t('review.itemsCount', { count: reviewTask.value.items.length })
  if (outboundTask.value) return outboundTask.value.packageNo
  return ''
})

const reviewProgress = computed(() => {
  if (!reviewTask.value) return 0
  return Math.min(100, Math.round((reviewTask.value.scannedQty / reviewTask.value.expectedQty) * 100))
})
</script>

<style scoped lang="scss">
@use '../styles/theme-warehouse.scss' as *;

.task-card {
  padding: $space-md;
}

.task-card + .task-card {
  margin-top: $space-sm;
}

.task-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: $space-sm;
  margin-bottom: $space-md;
}

.task-card__title-group {
  min-width: 0;
}

.task-card__order,
.task-card__subtitle {
  display: block;
}

.task-card__order {
  color: $text-primary;
  font-size: $font-md;
  font-weight: $font-weight-bold;
  line-height: 1.25;
}

.task-card__subtitle {
  margin-top: 4rpx;
  color: $text-tertiary;
  font-size: $font-xs;
}

.task-card__tags {
  display: flex;
  flex-shrink: 0;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8rpx;
}

.task-card__product-line,
.task-card__progress-head,
.task-card__item-row,
.task-card__status-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $space-sm;
}

.task-card__product {
  min-width: 0;
  color: $text-primary;
  font-size: $font-base;
  font-weight: $font-weight-semibold;
  line-height: 1.35;
}

.task-card__quantity {
  flex-shrink: 0;
  color: $warehouse-primary;
  font-size: $font-lg;
  font-weight: $font-weight-bold;
}

.task-card__progress-head {
  color: $text-secondary;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
}

.task-card__progress {
  overflow: hidden;
  height: 14rpx;
  margin: $space-xs 0 $space-sm;
  border-radius: $radius-pill;
  background: $bg-input;
}

.task-card__progress-bar {
  height: 100%;
  border-radius: $radius-pill;
  background: $warehouse-gradient;
}

.task-card__item-row {
  padding: 8rpx 0;
  border-top: 1rpx solid $divider-color;
}

.task-card__item-name {
  min-width: 0;
  color: $text-secondary;
  font-size: $font-sm;
  line-height: 1.35;
}

.task-card__item-count {
  flex-shrink: 0;
  color: $text-primary;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
}

.task-card__status-row {
  justify-content: flex-start;
  flex-wrap: wrap;
  margin-top: $space-sm;
}
</style>
