<template>
  <view class="mine-page">
    <!-- Logged in state -->
    <template v-if="userStore.isLoggedIn">
      <view class="profile-section">
        <view class="avatar">
          <text class="avatar-text">{{ avatarLetter }}</text>
        </view>
        <view class="profile-info">
          <text class="username">{{ userStore.userInfo?.username || '--' }}</text>
          <text class="user-type">{{ userStore.userInfo?.userType || '' }}</text>
        </view>
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
import { useI18n } from 'vue-i18n'
import { useUserStore } from '../../stores/user'

const { locale, t } = useI18n()
const userStore = useUserStore()

const avatarLetter = computed(() => {
  const name = userStore.userInfo?.username
  return name ? name.charAt(0).toUpperCase() : '?'
})

const currentLangLabel = computed(() => {
  return locale.value === 'zh-CN' ? t('common.languageZh') : t('common.languageEn')
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
  // TODO: implement scan receipt
  uni.scanCode({
    scanType: ['qrCode'],
    success: (res) => {
      console.log('Scan result:', res.result)
    },
    fail: () => {
      uni.showToast({ title: 'Scan not supported', icon: 'none' })
    },
  })
}

function goMyOrders() {
  uni.switchTab({ url: '/pages/order/index' })
}

function goShipInfo() {
  // TODO: navigate to ship info page
}

function goProfile() {
  // TODO: navigate to profile page
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
