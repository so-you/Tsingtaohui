<template>
  <view class="mine-page">
    <!-- Logged in state -->
    <template v-if="userStore.isLoggedIn">
      <view class="profile-section">
        <view class="avatar">
          <text class="avatar-text">{{ avatarLetter }}</text>
        </view>
        <view class="profile-info">
          <text class="username">{{ displayName }}</text>
          <text class="user-type">{{ currentShipLabel }}</text>
        </view>
      </view>

      <view class="ship-card" @tap="goShipInfo">
        <view class="ship-card-header">
          <text class="ship-card-title">{{ $t('mine.shipInfo') }}</text>
          <text class="menu-arrow">></text>
        </view>
        <text class="ship-card-name">{{ currentShipName }}</text>
        <text v-if="currentShipMeta" class="ship-card-meta">{{ currentShipMeta }}</text>
      </view>

      <view class="menu-section">
        <view class="menu-item" @tap="goScanReceipt">
          <text class="menu-label">{{ $t('mine.scanReceipt') }}</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @tap="goMyOrders">
          <text class="menu-label">{{ $t('mine.myOrders') }}</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @tap="goShipInfo">
          <text class="menu-label">{{ $t('mine.shipInfo') }}</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @tap="goProfile">
          <text class="menu-label">{{ $t('mine.profile') }}</text>
          <text class="menu-arrow">></text>
        </view>
        <view class="menu-item" @tap="toggleLanguage">
          <text class="menu-label">{{ $t('mine.language') }}</text>
          <text class="menu-value">{{ currentLangLabel }}</text>
          <text class="menu-arrow">></text>
        </view>
      </view>

      <button class="btn-logout" @tap="handleLogout">
        {{ $t('mine.logout') }}
      </button>
    </template>

    <!-- Not logged in state -->
    <template v-else>
      <view class="login-prompt">
        <text class="prompt-title">{{ $t('mine.notLoggedIn') }}</text>
        <text class="prompt-desc">{{ $t('mine.loginPrompt') }}</text>
        <view class="prompt-actions">
          <button class="btn-primary" @tap="goLogin">{{ $t('auth.login') }}</button>
          <button class="btn-outline" @tap="goRegister">{{ $t('auth.register') }}</button>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '../../stores/user'
import type { IShip } from '../../types'

const { locale, t } = useI18n()
const userStore = useUserStore()

const avatarLetter = computed(() => {
  const name = displayName.value
  return name ? name.charAt(0).toUpperCase() : '?'
})

const displayName = computed(() => {
  return userStore.userInfo?.displayName || userStore.userInfo?.username || '--'
})

const currentShip = computed<IShip | null>(() => {
  const userInfo = userStore.userInfo
  if (!userInfo) return null
  const ship = userInfo.ships?.find((item) => item.isDefault) || userInfo.ships?.[0]
  if (ship) return ship
  if (!userInfo.shipNo && !userInfo.shipNationality) return null
  return {
    shipNo: userInfo.shipNo || '',
    shipName: userInfo.shipName || '',
    shipNationality: userInfo.shipNationality || userInfo.nationality || '',
    imo: userInfo.imo || '',
    mmsi: userInfo.mmsi || '',
  }
})

const currentShipName = computed(() => {
  return currentShip.value?.shipName || currentShip.value?.shipNo || t('mine.noShip')
})

const currentShipLabel = computed(() => {
  return currentShip.value?.shipNo || userStore.userInfo?.userType || ''
})

const currentShipMeta = computed(() => {
  const ship = currentShip.value
  if (!ship) return ''
  return [ship.shipNationality, ship.imo ? `IMO ${ship.imo}` : '', ship.mmsi ? `MMSI ${ship.mmsi}` : '']
    .filter(Boolean)
    .join(' / ')
})

const currentLangLabel = computed(() => {
  return locale.value === 'zh-CN' ? t('common.languageZh') : t('common.languageEn')
})

onShow(() => {
  if (userStore.isLoggedIn) {
    userStore.fetchProfile().catch(() => undefined)
  }
})

function toggleLanguage() {
  const newLocale = locale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  locale.value = newLocale
  try {
    localStorage.setItem('locale', newLocale)
  } catch {
    // ignore
  }
}

function goScanReceipt() {
  uni.scanCode({
    scanType: ['qrCode'],
    success: (res) => {
      uni.showToast({ title: res.result ? t('mine.scanSuccess') : t('common.success'), icon: 'success' })
    },
    fail: () => {
      uni.showToast({ title: t('mine.scanNotSupported'), icon: 'none' })
    },
  })
}

function goMyOrders() {
  uni.switchTab({ url: '/pages/order/index' })
}

function goShipInfo() {
  uni.navigateTo({ url: '/pages/mine/ship' })
}

function goProfile() {
  uni.showToast({ title: t('mine.profileComingSoon'), icon: 'none' })
}

function handleLogout() {
  uni.showModal({
    title: t('mine.logout'),
    content: t('auth.logoutConfirm'),
    confirmText: t('common.confirm'),
    cancelText: t('common.cancel'),
    success: (res) => {
      if (res.confirm) {
        userStore.logout()
      }
    },
  })
}

function goLogin() {
  uni.navigateTo({ url: '/pages/auth/login' })
}

function goRegister() {
  uni.navigateTo({ url: '/pages/auth/register' })
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background-color: #f7f8fa;
  padding-bottom: 120rpx;
}

.profile-section {
  display: flex;
  align-items: center;
  padding: 48rpx 32rpx;
  background-color: #ffffff;
  margin-bottom: 24rpx;
}

.avatar {
  width: 112rpx;
  height: 112rpx;
  border-radius: 56rpx;
  background-color: #1677ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 32rpx;
}

.avatar-text {
  font-size: 44rpx;
  font-weight: 700;
  color: #ffffff;
}

.profile-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: 36rpx;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 8rpx;
}

.user-type {
  font-size: 26rpx;
  color: #9ca3af;
}

.ship-card {
  margin: 0 32rpx 24rpx;
  padding: 28rpx 32rpx;
  background-color: #ffffff;
  border-radius: 16rpx;
}

.ship-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18rpx;
}

.ship-card-title {
  font-size: 26rpx;
  color: #6b7280;
}

.ship-card-name {
  display: block;
  font-size: 34rpx;
  font-weight: 700;
  color: #111827;
}

.ship-card-meta {
  display: block;
  margin-top: 10rpx;
  font-size: 24rpx;
  line-height: 34rpx;
  color: #6b7280;
}

.menu-section {
  background-color: #ffffff;
  border-radius: 16rpx;
  margin: 0 32rpx 32rpx;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 32rpx;
  border-bottom: 1rpx solid #f3f4f6;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-label {
  flex: 1;
  font-size: 30rpx;
  color: #1f2937;
}

.menu-value {
  font-size: 28rpx;
  color: #9ca3af;
  margin-right: 12rpx;
}

.menu-arrow {
  font-size: 28rpx;
  color: #d1d5db;
}

.btn-logout {
  margin: 48rpx 32rpx 0;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  color: #ef4444;
  font-size: 32rpx;
  border-radius: 16rpx;
  border: 2rpx solid #fca5a5;
}

.login-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 48rpx 0;
}

.prompt-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 16rpx;
}

.prompt-desc {
  font-size: 28rpx;
  color: #9ca3af;
  margin-bottom: 64rpx;
}

.prompt-actions {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 24rpx;
}

.btn-primary {
  width: 100%;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #1677ff;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 16rpx;
}

.btn-outline {
  width: 100%;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #ffffff;
  color: #1677ff;
  font-size: 32rpx;
  font-weight: 600;
  border-radius: 16rpx;
  border: 2rpx solid #1677ff;
}
</style>
