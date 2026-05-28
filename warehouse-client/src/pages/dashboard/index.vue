<template>
  <view class="dashboard">
    <view class="header">
      <text class="title">{{ t('dashboard.title') }}</text>
      <view class="lang-btn" @tap="toggleLang">
        <text>{{ locale === 'zh-CN' ? 'EN' : '中' }}</text>
      </view>
    </view>

    <view class="stats">
      <view class="stat-card" @tap="goTo('/pages/picking/index')">
        <text class="stat-num">{{ store.dashboardStats.pendingPick }}</text>
        <text class="stat-label">{{ t('dashboard.pendingPick') }}</text>
      </view>
      <view class="stat-card" @tap="goTo('/pages/review/index')">
        <text class="stat-num">{{ store.dashboardStats.pendingReview }}</text>
        <text class="stat-label">{{ t('dashboard.pendingReview') }}</text>
      </view>
      <view class="stat-card" @tap="goTo('/pages/outbound/index')">
        <text class="stat-num">{{ store.dashboardStats.pendingOutbound }}</text>
        <text class="stat-label">{{ t('dashboard.pendingOutbound') }}</text>
      </view>
      <view class="stat-card error">
        <text class="stat-num">{{ store.dashboardStats.exceptionOrders }}</text>
        <text class="stat-label">{{ t('dashboard.exceptionOrders') }}</text>
      </view>
    </view>

    <view class="scan-section">
      <text class="section-title">{{ t('dashboard.scanInput') }}</text>
      <input
        class="scan-input"
        :placeholder="t('dashboard.scanHint')"
        focus
        confirm-type="done"
        @confirm="handleScan"
      />
    </view>
  </view>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useWarehouseStore } from '../../stores/warehouse'

const { t, locale } = useI18n()
const store = useWarehouseStore()

function toggleLang() {
  const newLang = locale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  locale.value = newLang
  localStorage.setItem('wh_lang', newLang)
}

function goTo(url: string) {
  uni.navigateTo({ url })
}

function handleScan(e: any) {
  const code = e.detail.value
  if (!code) return
  uni.showToast({ title: `Scanned: ${code}`, icon: 'none' })
}

onMounted(() => {
  store.fetchDashboard()
})
</script>

<style scoped>
.dashboard { padding: 12px; background: #F7F8FA; min-height: 100vh; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.title { font-size: 18px; font-weight: 600; }
.lang-btn { padding: 6px 12px; background: #1677FF; border-radius: 6px; }
.lang-btn text { color: #fff; font-size: 13px; }
.stats { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; margin-bottom: 20px; }
.stat-card { background: #fff; border-radius: 8px; padding: 16px; text-align: center; border-left: 3px solid #1677FF; }
.stat-card.error { border-left-color: #DC2626; }
.stat-num { display: block; font-size: 28px; font-weight: 700; color: #111827; }
.stat-label { display: block; font-size: 12px; color: #6B7280; margin-top: 4px; }
.scan-section { background: #fff; border-radius: 8px; padding: 12px; }
.section-title { font-size: 14px; font-weight: 500; margin-bottom: 8px; display: block; }
.scan-input { height: 44px; border: 2px solid #1677FF; border-radius: 8px; padding: 0 12px; font-size: 16px; }
</style>
