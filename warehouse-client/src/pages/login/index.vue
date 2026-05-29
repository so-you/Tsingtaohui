<template>
  <view class="login-page">
    <view class="login-page__language" @tap="toggleLang">
      <text>{{ locale === 'zh-CN' ? 'EN' : '中' }}</text>
    </view>

    <view class="login-panel warehouse-card">
      <view class="login-panel__mark">
        <text>WH</text>
      </view>
      <text class="login-panel__title">{{ t('login.title') }}</text>
      <text class="login-panel__subtitle">{{ t('login.subtitle') }}</text>

      <view class="login-form">
        <input
          v-model="username"
          class="login-form__input"
          :placeholder="t('login.usernamePlaceholder')"
          confirm-type="next"
        />
        <text v-if="usernameError" class="login-form__error">{{ usernameError }}</text>

        <input
          v-model="password"
          class="login-form__input"
          :placeholder="t('login.passwordPlaceholder')"
          password
          confirm-type="done"
          @confirm="submitLogin"
        />
        <text v-if="passwordError" class="login-form__error">{{ passwordError }}</text>

        <view
          class="warehouse-primary-button login-form__button"
          :class="{ 'warehouse-primary-button--disabled': submitting }"
          @tap="submitLogin"
        >
          <text>{{ submitting ? t('common.loading') : t('login.submit') }}</text>
        </view>
      </view>

      <text class="login-panel__hint">{{ t('login.demoHint') }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useWarehouseStore } from '../../stores/warehouse'

const { t, locale } = useI18n()
const store = useWarehouseStore()

const username = ref('operator01')
const password = ref('demo123')
const usernameError = ref('')
const passwordError = ref('')
const submitting = ref(false)

function toggleLang() {
  const next = locale.value === 'zh-CN' ? 'en-US' : 'zh-CN'
  locale.value = next
  localStorage.setItem('wh_lang', next)
}

async function submitLogin() {
  usernameError.value = username.value.trim() ? '' : t('login.usernameRequired')
  passwordError.value = password.value.trim() ? '' : t('login.passwordRequired')
  if (usernameError.value || passwordError.value || submitting.value) return

  submitting.value = true
  try {
    await store.login(username.value.trim(), password.value)
    uni.switchTab({ url: '/pages/dashboard/index' })
  } catch (error: any) {
    uni.showToast({ title: error?.message || t('common.error'), icon: 'none' })
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
@use '../../styles/theme-warehouse.scss' as *;

.login-page {
  display: flex;
  min-height: 100vh;
  align-items: center;
  justify-content: center;
  padding: $space-xl $space-md;
  background: linear-gradient(180deg, #eff6ff 0%, #f5f5f5 54%);
}

.login-page__language {
  position: fixed;
  top: 32rpx;
  right: 24rpx;
  display: flex;
  min-width: 76rpx;
  height: 56rpx;
  align-items: center;
  justify-content: center;
  border-radius: $radius-pill;
  background: $bg-card;
  color: $warehouse-primary;
  font-size: $font-sm;
  font-weight: $font-weight-semibold;
  box-shadow: $shadow-sm;
}

.login-panel {
  width: 100%;
  max-width: 680rpx;
  padding: 56rpx $space-lg $space-lg;
}

.login-panel__mark {
  display: flex;
  width: 96rpx;
  height: 96rpx;
  align-items: center;
  justify-content: center;
  margin-bottom: $space-md;
  border-radius: $radius-lg;
  background: $warehouse-gradient;
  color: #ffffff;
  font-size: $font-md;
  font-weight: $font-weight-bold;
}

.login-panel__title,
.login-panel__subtitle,
.login-panel__hint {
  display: block;
}

.login-panel__title {
  color: $text-primary;
  font-size: $font-xl;
  font-weight: $font-weight-bold;
}

.login-panel__subtitle {
  margin-top: $space-xs;
  color: $text-secondary;
  font-size: $font-base;
}

.login-panel__hint {
  margin-top: $space-md;
  color: $text-tertiary;
  font-size: $font-sm;
  line-height: 1.45;
}

.login-form {
  margin-top: $space-lg;
}

.login-form__input {
  width: 100%;
  height: 88rpx;
  margin-top: $space-sm;
  padding: 0 $space-md;
  border: 1rpx solid $border-color;
  border-radius: $radius-md;
  background: $bg-surface;
  color: $text-primary;
  font-size: $font-base;
}

.login-form__error {
  display: block;
  margin-top: 8rpx;
  color: $warehouse-error;
  font-size: $font-xs;
}

.login-form__button {
  margin-top: $space-lg;
}
</style>
