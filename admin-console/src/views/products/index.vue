<script setup lang="ts">
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>

<template>
  <div class="products-page">
    <h2 class="page-title">{{ t('menu.products') }}</h2>
    <el-card shadow="never">
      <el-empty description="Coming soon" />
    </el-card>
  </div>
</template>

<style scoped>
.products-page {
  padding: 20px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 20px;
  color: #303133;
}
</style>
