<template>
  <view class="ship-page">
    <view class="form-card">
      <view class="form-item">
        <text class="form-label">{{ $t('ship.shipNo') }}</text>
        <input
          v-model="form.shipNo"
          class="form-input"
          type="text"
          :placeholder="$t('ship.shipNoPlaceholder')"
          :placeholder-style="'color: #9CA3AF'"
        />
      </view>
      <view class="form-item">
        <text class="form-label">{{ $t('ship.shipName') }}</text>
        <input
          v-model="form.shipName"
          class="form-input"
          type="text"
          :placeholder="$t('ship.shipNamePlaceholder')"
          :placeholder-style="'color: #9CA3AF'"
        />
      </view>
      <view class="form-item">
        <text class="form-label">{{ $t('ship.shipNationality') }}</text>
        <input
          v-model="form.shipNationality"
          class="form-input"
          type="text"
          :placeholder="$t('ship.shipNationalityPlaceholder')"
          :placeholder-style="'color: #9CA3AF'"
        />
      </view>
      <view class="form-item">
        <text class="form-label">{{ $t('ship.imo') }}</text>
        <input
          v-model="form.imo"
          class="form-input"
          type="text"
          :placeholder="$t('ship.imoPlaceholder')"
          :placeholder-style="'color: #9CA3AF'"
        />
      </view>
      <view class="form-item">
        <text class="form-label">{{ $t('ship.mmsi') }}</text>
        <input
          v-model="form.mmsi"
          class="form-input"
          type="text"
          :placeholder="$t('ship.mmsiPlaceholder')"
          :placeholder-style="'color: #9CA3AF'"
        />
      </view>
    </view>

    <button class="save-button" :loading="saving" :disabled="saving" @tap="saveShip">
      {{ $t('ship.save') }}
    </button>
  </view>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useI18n } from 'vue-i18n'
import { updateShip } from '../../api/user'
import { useUserStore } from '../../stores/user'
import type { IShip } from '../../types'

const { t } = useI18n()
const userStore = useUserStore()
const saving = ref(false)

const form = reactive({
  shipNo: '',
  shipName: '',
  shipNationality: '',
  imo: '',
  mmsi: '',
})

onShow(async () => {
  if (userStore.isLoggedIn) {
    await userStore.fetchProfile().catch(() => undefined)
    fillForm(getCurrentShip())
  }
})

function getCurrentShip(): IShip | null {
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
}

function fillForm(ship: IShip | null) {
  form.shipNo = ship?.shipNo || ''
  form.shipName = ship?.shipName || ''
  form.shipNationality = ship?.shipNationality || ''
  form.imo = ship?.imo || ''
  form.mmsi = ship?.mmsi || ''
}

function validateForm() {
  if (!form.shipNo.trim()) {
    uni.showToast({ title: t('ship.shipNoRequired'), icon: 'none' })
    return false
  }
  if (!form.shipNationality.trim()) {
    uni.showToast({ title: t('ship.shipNationalityRequired'), icon: 'none' })
    return false
  }
  return true
}

async function saveShip() {
  if (!validateForm()) return
  saving.value = true
  try {
    await updateShip({
      shipNo: form.shipNo.trim(),
      shipName: form.shipName.trim() || undefined,
      shipNationality: form.shipNationality.trim(),
      imo: form.imo.trim() || undefined,
      mmsi: form.mmsi.trim() || undefined,
    })
    await userStore.fetchProfile().catch(() => undefined)
    uni.showToast({ title: t('ship.saveSuccess'), icon: 'success' })
    setTimeout(() => {
      uni.navigateBack()
    }, 500)
  } catch (err: unknown) {
    const message =
      (err as { message?: string; response?: { data?: { message?: string } } })?.message ||
      (err as { response?: { data?: { message?: string } } })?.response?.data?.message ||
      t('common.error')
    uni.showToast({ title: message, icon: 'none' })
  } finally {
    saving.value = false
  }
}
</script>

<style lang="scss" scoped>
.ship-page {
  min-height: 100vh;
  padding: 24rpx 32rpx 120rpx;
  background-color: #f7f8fa;
}

.form-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
}

.form-item {
  padding: 28rpx 32rpx;
  border-bottom: 1rpx solid #f3f4f6;
}

.form-item:last-child {
  border-bottom: none;
}

.form-label {
  display: block;
  margin-bottom: 14rpx;
  font-size: 26rpx;
  color: #4b5563;
}

.form-input {
  width: 100%;
  height: 64rpx;
  font-size: 30rpx;
  color: #111827;
}

.save-button {
  margin-top: 40rpx;
  width: 100%;
  height: 88rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12rpx;
  background-color: #1677ff;
  color: #ffffff;
  font-size: 30rpx;
  font-weight: 600;
}
</style>
