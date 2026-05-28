<template>
  <view class="catalog-page">
    <text class="page-title">{{ $t('product.title') }}</text>
    <view class="product-list">
      <text class="empty-text">{{ $t('order.noOrders') }}</text>
    </view>
  </view>
</template>

<script setup lang="ts">
// Product list page placeholder
</script>

<style lang="scss" scoped>
.catalog-page {
  padding: 24rpx 32rpx;
}

.page-title {
  font-size: 40rpx;
  font-weight: 700;
  color: #1f2937;
  display: block;
  margin-bottom: 32rpx;
}

.product-list {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 96rpx 0;
}

.empty-text {
  font-size: 28rpx;
  color: #9ca3af;
}
</style>
