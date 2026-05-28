<template>
  <view class="register-page">
    <view class="register-header">
      <text class="app-title">Tsingtaohui</text>
      <text class="app-subtitle">{{ $t('auth.register') }}</text>
    </view>

    <view class="register-form">
      <view class="form-item">
        <input
          v-model="form.username"
          class="form-input"
          type="text"
          :placeholder="$t('auth.username')"
          :placeholder-style="'color: #9CA3AF'"
        />
      </view>
      <view class="form-item">
        <input
          v-model="form.password"
          class="form-input"
          type="password"
          :placeholder="$t('auth.password')"
          :placeholder-style="'color: #9CA3AF'"
        />
      </view>
      <view class="form-item">
        <input
          v-model="form.confirmPassword"
          class="form-input"
          type="password"
          :placeholder="$t('auth.confirmPassword')"
          :placeholder-style="'color: #9CA3AF'"
        />
      </view>

      <view v-if="passwordHint" class="password-hint">
        <text class="hint-text">{{ passwordHint }}</text>
      </view>

      <button class="btn-primary" :disabled="loading" @tap="handleRegister">
        {{ loading ? $t('common.loading') : $t('auth.registerBtn') }}
      </button>
    </view>

    <view class="register-footer">
      <text class="footer-text">{{ $t('auth.hasAccount') }}</text>
      <text class="footer-link" @tap="goLogin">{{ $t('auth.goLogin') }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '../../stores/user'

const { t } = useI18n()
const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: '',
})

const passwordHint = computed(() => {
  if (!form.password) return ''
  if (form.password.length < 8) return t('auth.passwordMin')
  const hasLetter = /[a-zA-Z]/.test(form.password)
  const hasNumber = /[0-9]/.test(form.password)
  if (!hasLetter || !hasNumber) return t('auth.passwordMin')
  if (form.confirmPassword && form.password !== form.confirmPassword) return t('auth.passwordMismatch')
  return ''
})

function validateForm(): boolean {
  if (!form.username.trim()) {
    uni.showToast({ title: t('auth.usernameRequired'), icon: 'none' })
    return false
  }
  if (!form.password) {
    uni.showToast({ title: t('auth.passwordRequired'), icon: 'none' })
    return false
  }
  if (form.password.length < 8 || !/[a-zA-Z]/.test(form.password) || !/[0-9]/.test(form.password)) {
    uni.showToast({ title: t('auth.passwordMin'), icon: 'none' })
    return false
  }
  if (form.password !== form.confirmPassword) {
    uni.showToast({ title: t('auth.passwordMismatch'), icon: 'none' })
    return false
  }
  return true
}

async function handleRegister() {
  if (!validateForm()) return

  loading.value = true
  try {
    await userStore.register(form.username.trim(), form.password)
    uni.showToast({ title: t('auth.registerSuccess'), icon: 'success' })
    uni.reLaunch({ url: '/pages/home/index' })
  } catch (err: unknown) {
    const message =
      (err as { message?: string; response?: { data?: { message?: string } } })?.message ||
      (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
      t('common.error')
    uni.showToast({ title: message, icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goLogin() {
  uni.navigateTo({ url: '/pages/auth/login' })
}
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 48rpx;
  background-color: #ffffff;
}

.register-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 80rpx;
}

.app-title {
  font-size: 56rpx;
  font-weight: 700;
  color: #1677ff;
  margin-bottom: 16rpx;
}

.app-subtitle {
  font-size: 32rpx;
  color: #6b7280;
}

.register-form {
  width: 100%;
}

.form-item {
  margin-bottom: 32rpx;
}

.form-input {
  width: 100%;
  height: 96rpx;
  padding: 0 32rpx;
  font-size: 32rpx;
  border: 2rpx solid #e5e7eb;
  border-radius: 16rpx;
  background-color: #f9fafb;
}

.password-hint {
  margin-bottom: 24rpx;
}

.hint-text {
  font-size: 24rpx;
  color: #ef4444;
}

.btn-primary {
  width: 100%;
  height: 96rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #1677ff;
  color: #ffffff;
  font-size: 34rpx;
  font-weight: 600;
  border-radius: 16rpx;
  margin-top: 24rpx;
}

.btn-primary[disabled] {
  opacity: 0.6;
}

.register-footer {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 48rpx;
  gap: 8rpx;
}

.footer-text {
  font-size: 28rpx;
  color: #9ca3af;
}

.footer-link {
  font-size: 28rpx;
  color: #1677ff;
  font-weight: 500;
}
</style>
