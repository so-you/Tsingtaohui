<template>
  <view class="login-page">
    <view class="login-header">
      <text class="app-title">Tsingtaohui</text>
      <text class="app-subtitle">{{ $t('auth.login') }}</text>
    </view>

    <view class="login-form">
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

      <button class="btn-primary" :disabled="loading" @tap="handleLogin">
        {{ loading ? $t('common.loading') : $t('auth.loginBtn') }}
      </button>
    </view>

    <view class="login-footer">
      <text class="footer-text">{{ $t('auth.noAccount') }}</text>
      <text class="footer-link" @tap="goRegister">{{ $t('auth.goRegister') }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useUserStore } from '../../stores/user'

const userStore = useUserStore()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
})

async function handleLogin() {
  if (!form.username.trim()) {
    uni.showToast({ title: 'auth.usernameRequired', icon: 'none' })
    return
  }
  if (!form.password) {
    uni.showToast({ title: 'auth.passwordRequired', icon: 'none' })
    return
  }

  loading.value = true
  try {
    await userStore.login(form.username.trim(), form.password)
    uni.showToast({ title: 'loginSuccess', icon: 'success' })
    uni.reLaunch({ url: '/pages/home/index' })
  } catch (err: unknown) {
    const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'common.error'
    uni.showToast({ title: message, icon: 'none' })
  } finally {
    loading.value = false
  }
}

function goRegister() {
  uni.navigateTo({ url: '/pages/auth/register' })
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 0 48rpx;
  background-color: #ffffff;
}

.login-header {
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

.login-form {
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
  margin-top: 48rpx;
}

.btn-primary[disabled] {
  opacity: 0.6;
}

.login-footer {
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
