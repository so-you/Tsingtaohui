<template>
  <view class="mine-page">
    <!-- Logged in state -->
    <template v-if="userStore.isLoggedIn">
      <!-- Profile Header -->
      <view class="profile-header">
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
            <view class="ship-card-title-row">
              <text class="ship-icon">🚢</text>
              <text class="ship-card-title">{{ $t('mine.shipInfo') }}</text>
            </view>
            <text class="menu-arrow">›</text>
          </view>
          <text class="ship-card-name">{{ currentShipName }}</text>
          <text v-if="currentShipMeta" class="ship-card-meta">{{ currentShipMeta }}</text>
        </view>
      </view>

      <!-- Menu Grid -->
      <view class="menu-grid">
        <view class="menu-grid-item" @tap="goScanReceipt">
          <view class="menu-grid-icon" style="background: #FFF3E0;">
            <text class="grid-emoji">📷</text>
          </view>
          <text class="menu-grid-label">{{ $t('mine.scanReceipt') }}</text>
        </view>
        <view class="menu-grid-item" @tap="goMyOrders">
          <view class="menu-grid-icon" style="background: #E3F2FD;">
            <text class="grid-emoji">📋</text>
          </view>
          <text class="menu-grid-label">{{ $t('mine.myOrders') }}</text>
        </view>
        <view class="menu-grid-item" @tap="goShipInfo">
          <view class="menu-grid-icon" style="background: #E8F5E9;">
            <text class="grid-emoji">🚢</text>
          </view>
          <text class="menu-grid-label">{{ $t('mine.shipInfo') }}</text>
        </view>
        <view class="menu-grid-item" @tap="goProfile">
          <view class="menu-grid-icon" style="background: #F3E5F5;">
            <text class="grid-emoji">👤</text>
          </view>
          <text class="menu-grid-label">{{ $t('mine.profile') }}</text>
        </view>
      </view>

      <!-- Settings List -->
      <view class="settings-section">
        <view class="settings-item" @tap="toggleLanguage">
          <view class="settings-left">
            <text class="settings-icon">🌐</text>
            <text class="settings-label">{{ $t('mine.language') }}</text>
          </view>
          <view class="settings-right">
            <text class="settings-value">{{ currentLangLabel }}</text>
            <text class="settings-arrow">›</text>
          </view>
        </view>
      </view>

      <button class="btn-logout" @tap="handleLogout">
        {{ $t('mine.logout') }}
      </button>
    </template>

    <!-- Not logged in state -->
    <template v-else>
      <view class="login-prompt">
        <view class="login-illustration">
          <text class="login-emoji">🚁</text>
        </view>
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
@import '../../styles/theme.scss';

.mine-page {
  min-height: 100vh;
  background-color: $bg-page;
  padding-bottom: 120rpx;
}

/* Profile Header */
.profile-header {
  background: $brand-gradient;
  padding: $space-xl $space-lg $space-lg;
  border-radius: 0 0 $radius-xl $radius-xl;
  margin-bottom: $space-md;
}

.profile-section {
  display: flex;
  align-items: center;
  margin-bottom: $space-lg;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: $radius-pill;
  background-color: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8rpx);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: $space-md;
  border: 2rpx solid rgba(255, 255, 255, 0.3);
}

.avatar-text {
  font-size: 48rpx;
  font-weight: $font-weight-bold;
  color: #ffffff;
}

.profile-info {
  display: flex;
  flex-direction: column;
}

.username {
  font-size: $font-xl;
  font-weight: $font-weight-bold;
  color: #ffffff;
  margin-bottom: 6rpx;
}

.user-type {
  font-size: $font-sm;
  color: rgba(255, 255, 255, 0.8);
}

/* Ship Card */
.ship-card {
  padding: $space-md;
  background-color: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(8rpx);
  border-radius: $radius-md;
  border: 1rpx solid rgba(255, 255, 255, 0.2);
}

.ship-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: $space-xs;
}

.ship-card-title-row {
  display: flex;
  align-items: center;
  gap: 8rpx;
}

.ship-icon {
  font-size: 28rpx;
}

.ship-card-title {
  font-size: $font-sm;
  color: rgba(255, 255, 255, 0.8);
}

.menu-arrow {
  font-size: $font-md;
  color: rgba(255, 255, 255, 0.6);
}

.ship-card-name {
  display: block;
  font-size: $font-lg;
  font-weight: $font-weight-bold;
  color: #ffffff;
}

.ship-card-meta {
  display: block;
  margin-top: 6rpx;
  font-size: $font-xs;
  color: rgba(255, 255, 255, 0.7);
}

/* Menu Grid */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: $space-sm;
  padding: 0 $space-lg;
  margin-bottom: $space-md;
}

.menu-grid-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $space-sm 0;
  background-color: $bg-card;
  border-radius: $radius-md;
  box-shadow: $shadow-card;
  transition: transform $transition-fast;
}

.menu-grid-item:active {
  transform: scale(0.95);
}

.menu-grid-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: $radius-lg;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $space-xs;
}

.grid-emoji {
  font-size: 40rpx;
}

.menu-grid-label {
  font-size: $font-xs;
  color: $text-secondary;
}

/* Settings */
.settings-section {
  margin: 0 $space-lg;
  background-color: $bg-card;
  border-radius: $radius-md;
  box-shadow: $shadow-card;
  overflow: hidden;
}

.settings-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $space-md $space-lg;
}

.settings-left {
  display: flex;
  align-items: center;
  gap: $space-sm;
}

.settings-icon {
  font-size: 32rpx;
}

.settings-label {
  font-size: $font-base;
  color: $text-primary;
}

.settings-right {
  display: flex;
  align-items: center;
  gap: $space-xs;
}

.settings-value {
  font-size: $font-sm;
  color: $text-tertiary;
}

.settings-arrow {
  font-size: $font-md;
  color: $text-placeholder;
}

/* Logout Button */
.btn-logout {
  margin: $space-lg $space-lg 0;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $bg-card;
  color: $brand-primary;
  font-size: $font-md;
  font-weight: $font-weight-medium;
  border-radius: $radius-md;
  box-shadow: $shadow-card;
}

/* Login Prompt */
.login-prompt {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 200rpx $space-lg 0;
}

.login-illustration {
  width: 200rpx;
  height: 200rpx;
  border-radius: $radius-xl;
  background: $brand-gradient;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: $space-lg;
  box-shadow: $shadow-md;
}

.login-emoji {
  font-size: 96rpx;
}

.prompt-title {
  font-size: $font-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $space-xs;
}

.prompt-desc {
  font-size: $font-sm;
  color: $text-tertiary;
  margin-bottom: $space-xl;
}

.prompt-actions {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: $space-sm;
}

.btn-primary {
  width: 100%;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $brand-gradient;
  color: #ffffff;
  font-size: $font-md;
  font-weight: $font-weight-semibold;
  border-radius: $radius-pill;
  box-shadow: $shadow-md;
}

.btn-outline {
  width: 100%;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: $bg-card;
  color: $brand-primary;
  font-size: $font-md;
  font-weight: $font-weight-semibold;
  border-radius: $radius-pill;
  border: 2rpx solid $brand-primary;
}
</style>
