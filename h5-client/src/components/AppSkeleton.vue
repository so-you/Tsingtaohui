<template>
  <view class="skeleton" :class="{ active: loading }">
    <slot />
  </view>
</template>

<script setup lang="ts">
defineProps<{
  loading: boolean
}>()
</script>

<style lang="scss" scoped>
.skeleton {
  position: relative;
}

.skeleton.active ::v-deep .s-item {
  background: linear-gradient(90deg, #f0f0f0 25%, #e8e8e8 50%, #f0f0f0 75%);
  background-size: 200% 100%;
  animation: shimmer 1.5s infinite;
  color: transparent !important;
  border-radius: 8rpx;
}

/* Keep emoji icons visible during skeleton loading */
.skeleton.active ::v-deep .s-item .category-emoji,
.skeleton.active ::v-deep .s-item .grid-emoji,
.skeleton.active ::v-deep .s-item .empty-emoji,
.skeleton.active ::v-deep .s-item .login-emoji,
.skeleton.active ::v-deep .s-item .banner-emoji,
.skeleton.active ::v-deep .s-item .ship-icon,
.skeleton.active ::v-deep .s-item .settings-icon {
  color: initial !important;
}

.skeleton.active ::v-deep .s-item image,
.skeleton.active ::v-deep .s-item img {
  opacity: 0;
}

@keyframes shimmer {
  0% {
    background-position: -200% 0;
  }
  100% {
    background-position: 200% 0;
  }
}
</style>
