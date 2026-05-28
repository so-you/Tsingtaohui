<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const stats = ref([
  { key: 'totalUsers', value: 1284, icon: 'User', color: '#409eff' },
  { key: 'totalOrders', value: 3847, icon: 'List', color: '#67c23a' },
  { key: 'activeDeliveries', value: 23, icon: 'Van', color: '#e6a23c' },
  { key: 'pendingMatches', value: 8, icon: 'Connection', color: '#f56c6c' }
])
</script>

<template>
  <div class="dashboard">
    <h2 class="page-title">{{ t('menu.dashboard') }}</h2>
    <el-row :gutter="20">
      <el-col
        v-for="stat in stats"
        :key="stat.key"
        :xs="12"
        :sm="12"
        :md="6"
      >
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <div class="stat-label">{{ t(`dashboard.${stat.key}`) }}</div>
              <div class="stat-value">{{ stat.value.toLocaleString() }}</div>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: stat.color + '20', color: stat.color }">
              <el-icon :size="28">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 20px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 20px;
  color: #303133;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
