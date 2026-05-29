<template>
  <view class="scan-input-card warehouse-card">
    <view class="scan-input-card__head">
      <view>
        <text class="scan-input-card__title">{{ title }}</text>
        <text class="scan-input-card__hint">{{ hint }}</text>
      </view>
      <text class="scan-input-card__mode">{{ modeLabel }}</text>
    </view>

    <input
      v-model="scanCode"
      class="scan-input-card__control"
      :style="{ borderColor }"
      :placeholder="placeholder"
      :focus="autoFocus"
      confirm-type="done"
      @confirm="submitScan"
    />

    <view v-if="history.length" class="scan-input-card__history">
      <text class="scan-input-card__history-title">{{ t('scan.recent') }}</text>
      <view
        v-for="item in history"
        :key="`${item.code}-${item.scannedAt}`"
        class="scan-input-card__history-item"
        :class="getScanFeedbackClass(item.status)"
      >
        <text class="scan-input-card__history-code">{{ item.code }}</text>
        <text class="scan-input-card__history-message">{{ item.message }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import type { IScanHistoryItem } from '../utils/scanner'
import { addScanHistory, getScanFeedbackClass, normalizeScanCode } from '../utils/scanner'

const props = withDefaults(
  defineProps<{
    title?: string
    hint?: string
    modeLabel?: string
    placeholder?: string
    borderColor?: string
    autoFocus?: boolean
    feedback?: IScanHistoryItem | null
  }>(),
  {
    title: '',
    hint: '',
    modeLabel: '',
    placeholder: '',
    borderColor: '#2563EB',
    autoFocus: true,
    feedback: null,
  },
)

const emit = defineEmits<{
  scan: [code: string]
}>()

const { t } = useI18n()
const scanCode = ref('')
const history = ref<IScanHistoryItem[]>([])

function submitScan(event: { detail?: { value?: string } }) {
  const code = normalizeScanCode(event.detail?.value || scanCode.value)
  if (!code) return
  emit('scan', code)
  scanCode.value = ''
}

watch(
  () => props.feedback,
  (item) => {
    if (!item) return
    history.value = addScanHistory(history.value, item)
  },
)
</script>

<style scoped lang="scss">
@use '../styles/theme-warehouse.scss' as *;

.scan-input-card {
  padding: $space-md;
}

.scan-input-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: $space-md;
  margin-bottom: $space-sm;
}

.scan-input-card__title,
.scan-input-card__hint {
  display: block;
}

.scan-input-card__title {
  color: $text-primary;
  font-size: $font-md;
  font-weight: $font-weight-semibold;
}

.scan-input-card__hint {
  margin-top: 4rpx;
  color: $text-tertiary;
  font-size: $font-sm;
}

.scan-input-card__mode {
  flex-shrink: 0;
  padding: 6rpx 14rpx;
  border-radius: $radius-pill;
  background: $warehouse-primary-bg;
  color: $warehouse-primary;
  font-size: $font-xs;
  font-weight: $font-weight-semibold;
}

.scan-input-card__control {
  width: 100%;
  height: 92rpx;
  padding: 0 $space-md;
  border: 3rpx solid $warehouse-primary;
  border-radius: $radius-md;
  background: $bg-card;
  color: $text-primary;
  font-size: $font-md;
  font-weight: $font-weight-semibold;
}

.scan-input-card__history {
  margin-top: $space-sm;
}

.scan-input-card__history-title {
  display: block;
  margin-bottom: $space-xs;
  color: $text-tertiary;
  font-size: $font-xs;
}

.scan-input-card__history-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $space-sm;
  margin-top: 8rpx;
  padding: 10rpx 12rpx;
  border-radius: $radius-sm;
  background: $bg-surface;
}

.scan-input-card__history-code {
  min-width: 0;
  color: $text-primary;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
  word-break: break-all;
}

.scan-input-card__history-message {
  flex-shrink: 0;
  font-size: $font-xs;
  font-weight: $font-weight-semibold;
}

.scan-feedback--success {
  background: $warehouse-success-bg;
  color: $warehouse-success;
}

.scan-feedback--success .scan-input-card__history-message {
  color: $warehouse-success;
}

.scan-feedback--failed {
  background: $warehouse-error-bg;
}

.scan-feedback--failed .scan-input-card__history-message {
  color: $warehouse-error;
}

.scan-feedback--duplicate,
.scan-feedback--overflow {
  background: $warehouse-warning-bg;
}

.scan-feedback--duplicate .scan-input-card__history-message,
.scan-feedback--overflow .scan-input-card__history-message {
  color: $warehouse-warning;
}
</style>
