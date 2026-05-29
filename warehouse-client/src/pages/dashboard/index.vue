<template>
  <view class="warehouse-page dashboard-page">
    <view class="warehouse-header">
      <view class="warehouse-title-group">
        <text class="warehouse-title">{{ t('dashboard.title') }}</text>
        <text class="warehouse-subtitle">{{ t('dashboard.subtitle') }}</text>
      </view>
      <view class="dashboard-page__actions">
        <view class="dashboard-page__language" @tap="toggleLang">
          <text>{{ locale === 'zh-CN' ? 'EN' : '中' }}</text>
        </view>
        <view class="dashboard-page__logout" @tap="logout">
          <text>{{ t('common.logout') }}</text>
        </view>
      </view>
    </view>

    <view class="dashboard-stats">
      <view class="dashboard-stat warehouse-card" @tap="goTab('/pages/picking/index')">
        <text class="dashboard-stat__code">PICK</text>
        <text class="dashboard-stat__value">{{ store.dashboardStats.pendingPicking }}</text>
        <text class="dashboard-stat__label">{{ t('dashboard.pendingPicking') }}</text>
      </view>
      <view class="dashboard-stat warehouse-card" @tap="goTab('/pages/review/index')">
        <text class="dashboard-stat__code dashboard-stat__code--review">REV</text>
        <text class="dashboard-stat__value">{{ store.dashboardStats.pendingReview }}</text>
        <text class="dashboard-stat__label">{{ t('dashboard.pendingReview') }}</text>
      </view>
      <view class="dashboard-stat warehouse-card" @tap="goTab('/pages/outbound/index')">
        <text class="dashboard-stat__code dashboard-stat__code--outbound">OUT</text>
        <text class="dashboard-stat__value">{{ store.dashboardStats.pendingOutbound }}</text>
        <text class="dashboard-stat__label">{{ t('dashboard.pendingOutbound') }}</text>
      </view>
      <view class="dashboard-stat dashboard-stat--error warehouse-card" @tap="goTab('/pages/outbound/index')">
        <text class="dashboard-stat__code dashboard-stat__code--error">ERR</text>
        <text class="dashboard-stat__value">{{ store.dashboardStats.exceptionOrders }}</text>
        <text class="dashboard-stat__label">{{ t('dashboard.exceptionOrders') }}</text>
      </view>
    </view>

    <view class="warehouse-section">
      <ScanInput
        :title="t('dashboard.scanInput')"
        :hint="t('dashboard.scanHint')"
        :mode-label="t('dashboard.scanMode')"
        :placeholder="t('dashboard.scanHint')"
        :feedback="scanFeedback"
        @scan="handleDashboardScan"
      />
    </view>

    <view class="warehouse-section">
      <text class="warehouse-section-title">{{ t('dashboard.operationEntry') }}</text>
      <view class="dashboard-entry warehouse-card" @tap="goInventory">
        <view>
          <text class="dashboard-entry__title">{{ t('dashboard.inventoryLookup') }}</text>
          <text class="dashboard-entry__desc">{{ t('inventory.subtitle') }}</text>
        </view>
        <text class="dashboard-entry__arrow">GO</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import ScanInput from '../../components/ScanInput.vue'
import { useWarehouseStore } from '../../stores/warehouse'
import type { IScanHistoryItem } from '../../utils/scanner'
import { createScanHistoryItem } from '../../utils/scanner'

const { t, locale } = useI18n()
const store = useWarehouseStore()
const scanFeedback = ref<IScanHistoryItem | null>(null)

function toggleLang() {
  const next = locale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  locale.value = next
  localStorage.setItem('wh_lang', next)
}

function logout() {
  store.logout()
  uni.reLaunch({ url: '/pages/login/index' })
}

function goTab(url: string) {
  uni.switchTab({ url })
}

function goInventory() {
  uni.navigateTo({ url: '/pages/inventory/index' })
}

function handleDashboardScan(code: string) {
  const result = { status: 'success' as const, messageKey: 'dashboard.scanSuccess' }
  scanFeedback.value = createScanHistoryItem(code, result, t(result.messageKey))
  uni.showToast({ title: t(result.messageKey), icon: 'success' })

  if (code.startsWith('PKG-')) {
    setTimeout(() => goTab('/pages/outbound/index'), 200)
    return
  }
  if (code.startsWith('SKU-')) {
    setTimeout(() => goTab('/pages/picking/index'), 200)
  }
}

onShow(() => {
  store.fetchDashboard()
})
</script>

<style scoped lang="scss">
@use '../../styles/theme-warehouse.scss' as *;

.dashboard-page__actions {
  display: flex;
  flex-shrink: 0;
  gap: $space-xs;
}

.dashboard-page__language,
.dashboard-page__logout {
  display: flex;
  height: 56rpx;
  align-items: center;
  justify-content: center;
  border-radius: $radius-pill;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
}

.dashboard-page__language {
  min-width: 72rpx;
  background: $warehouse-primary-bg;
  color: $warehouse-primary;
}

.dashboard-page__logout {
  padding: 0 $space-sm;
  background: $bg-card;
  color: $text-secondary;
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: $space-sm;
}

.dashboard-stat {
  min-height: 180rpx;
  padding: $space-md;
}

.dashboard-stat__code,
.dashboard-stat__value,
.dashboard-stat__label {
  display: block;
}

.dashboard-stat__code {
  width: max-content;
  padding: 5rpx 10rpx;
  border-radius: $radius-sm;
  background: $warehouse-primary-bg;
  color: $warehouse-primary;
  font-size: $font-xs;
  font-weight: $font-weight-bold;
}

.dashboard-stat__code--review {
  background: $warehouse-warning-bg;
  color: $warehouse-warning;
}

.dashboard-stat__code--outbound {
  background: $warehouse-info-bg;
  color: $warehouse-info;
}

.dashboard-stat__code--error {
  background: $warehouse-error-bg;
  color: $warehouse-error;
}

.dashboard-stat__value {
  margin-top: $space-sm;
  color: $text-primary;
  font-size: 56rpx;
  font-weight: $font-weight-bold;
  line-height: 1;
}

.dashboard-stat__label {
  margin-top: $space-xs;
  color: $text-secondary;
  font-size: $font-sm;
}

.dashboard-entry {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: $space-md;
  padding: $space-md;
}

.dashboard-entry__title,
.dashboard-entry__desc {
  display: block;
}

.dashboard-entry__title {
  color: $text-primary;
  font-size: $font-md;
  font-weight: $font-weight-semibold;
}

.dashboard-entry__desc {
  margin-top: 6rpx;
  color: $text-tertiary;
  font-size: $font-sm;
  line-height: 1.4;
}

.dashboard-entry__arrow {
  flex-shrink: 0;
  color: $warehouse-primary;
  font-size: $font-sm;
  font-weight: $font-weight-bold;
}
</style>
