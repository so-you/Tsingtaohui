<template>
  <view class="home-page">
    <view class="home-header">
      <text class="greeting">{{ $t('home.title') }}</text>
    </view>

    <view class="search-bar" @tap="handleSearch">
      <text class="search-placeholder">{{ $t('home.search') }}</text>
    </view>

    <view class="section">
      <text class="section-title">{{ $t('home.categories') }}</text>
      <view class="category-grid">
        <!-- placeholder categories -->
        <view class="category-item">
          <view class="category-icon" />
          <text class="category-name">...</text>
        </view>
      </view>
    </view>

    <view class="section">
      <text class="section-title">{{ $t('home.recommend') }}</text>
      <view class="product-list">
        <text class="empty-text">{{ $t('home.noActiveOrders') }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
function handleSearch() {
  uni.navigateTo({ url: '/pages/catalog/index' })
}
</script>

<style lang="scss" scoped>
.home-page {
  padding: 24rpx 32rpx;
}

.home-header {
  margin-bottom: 32rpx;
}

.greeting {
  font-size: 40rpx;
  font-weight: 700;
  color: #1f2937;
}

.search-bar {
  display: flex;
  align-items: center;
  height: 80rpx;
  padding: 0 32rpx;
  background-color: #f3f4f6;
  border-radius: 40rpx;
  margin-bottom: 40rpx;
}

.search-placeholder {
  font-size: 28rpx;
  color: #9ca3af;
}

.section {
  margin-bottom: 40rpx;
}

.section-title {
  font-size: 32rpx;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 24rpx;
  display: block;
}

.category-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 24rpx;
}

.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 140rpx;
}

.category-icon {
  width: 96rpx;
  height: 96rpx;
  border-radius: 24rpx;
  background-color: #e5e7eb;
  margin-bottom: 12rpx;
}

.category-name {
  font-size: 24rpx;
  color: #6b7280;
}

.product-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 48rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #9ca3af;
}
</style>
