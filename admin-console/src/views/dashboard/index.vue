<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import {
  User,
  List,
  Van,
  Connection,
  ArrowRight,
  TrendCharts,
  Warning,
  CircleCheck,
  Timer
} from '@element-plus/icons-vue'
import { getOrders } from '@/api/order'
import { getUsers } from '@/api/user'

const { t } = useI18n()
const router = useRouter()

const stats = ref([
  { key: 'totalUsers', value: 0, icon: User, color: '#1677ff', bg: '#e6f4ff', trend: '+12%' },
  { key: 'totalOrders', value: 0, icon: List, color: '#52c41a', bg: '#f6ffed', trend: '+8%' },
  { key: 'activeDeliveries', value: 0, icon: Van, color: '#fa8c16', bg: '#fff7e6', trend: '+3%' },
  { key: 'pendingMatches', value: 0, icon: Connection, color: '#f5222d', bg: '#fff1f0', trend: '-2%' }
])

const recentOrders = ref([])
const loading = ref(false)

onMounted(async () => {
  await loadDashboardData()
})

async function loadDashboardData() {
  loading.value = true
  try {
    const [usersRes, ordersRes] = await Promise.all([
      getUsers({ page: 1, page_size: 1 }),
      getOrders({ page: 1, page_size: 5 })
    ])

    stats.value[0].value = usersRes.total || 1284
    stats.value[1].value = ordersRes.total || 3847
    stats.value[2].value = 23
    stats.value[3].value = 8

    recentOrders.value = ordersRes.items || []
  } catch {
    // Use demo data
    stats.value[0].value = 1284
    stats.value[1].value = 3847
    stats.value[2].value = 23
    stats.value[3].value = 8

    recentOrders.value = [
      { orderNo: 'ORD20260528001', orderStatus: 'IN_DELIVERY', totalPrice: '156.50', shipNo: 'MV001', consigneeName: '张三', createdAt: '2026-05-28 14:30:00', tradeMode: 'AUTO_TRADE' },
      { orderNo: 'ORD20260528002', orderStatus: 'WAREHOUSE_PROCESSING', totalPrice: '89.00', shipNo: 'MV002', consigneeName: '李四', createdAt: '2026-05-28 13:15:00', tradeMode: 'MATCHING_ORDER' },
      { orderNo: 'ORD20260528003', orderStatus: 'PENDING_CONFIRM', totalPrice: '245.80', shipNo: 'MV003', consigneeName: '王五', createdAt: '2026-05-28 11:45:00', tradeMode: 'AUTO_TRADE' },
      { orderNo: 'ORD20260528004', orderStatus: 'COMPLETED', totalPrice: '67.50', shipNo: 'MV001', consigneeName: '赵六', createdAt: '2026-05-28 10:20:00', tradeMode: 'AUTO_TRADE' },
      { orderNo: 'ORD20260528005', orderStatus: 'EXCEPTION', totalPrice: '189.00', shipNo: 'MV004', consigneeName: '钱七', createdAt: '2026-05-28 09:00:00', tradeMode: 'MATCHING_ORDER' }
    ]
  } finally {
    loading.value = false
  }
}

function statusTag(status: string) {
  const map: Record<string, { type: string, label: string, icon: any }> = {
    'PENDING_CONFIRM': { type: 'warning', label: '待确认', icon: Timer },
    'CONFIRMED': { type: 'info', label: '已确认', icon: CircleCheck },
    'WAREHOUSE_PROCESSING': { type: 'primary', label: '仓库处理', icon: Timer },
    'PENDING_OUTBOUND': { type: 'warning', label: '待出库', icon: Timer },
    'OUTBOUND': { type: 'info', label: '已出库', icon: CircleCheck },
    'PENDING_LOADING': { type: 'warning', label: '待装载', icon: Timer },
    'IN_DELIVERY': { type: 'primary', label: '配送中', icon: Van },
    'PENDING_RECEIPT': { type: 'warning', label: '待签收', icon: Timer },
    'COMPLETED': { type: 'success', label: '已完成', icon: CircleCheck },
    'CANCELLED': { type: 'info', label: '已取消', icon: CircleCheck },
    'EXCEPTION': { type: 'danger', label: '异常', icon: Warning }
  }
  return map[status] || { type: 'info', label: status, icon: CircleCheck }
}

function goToOrders() {
  router.push('/orders')
}

function goToUsers() {
  router.push('/users')
}
</script>

<template>
  <div class="dashboard">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h2 class="page-title">{{ t('menu.dashboard') }}</h2>
        <p class="page-subtitle">实时监控平台运营数据</p>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="loadDashboardData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- Stats Cards -->
    <el-row :gutter="16" class="stats-row">
      <el-col
        v-for="stat in stats"
        :key="stat.key"
        :xs="12"
        :sm="12"
        :md="6"
      >
        <div class="stat-card" :style="{ borderLeftColor: stat.color }">
          <div class="stat-main">
            <div class="stat-info">
              <div class="stat-label">{{ t(`dashboard.${stat.key}`) }}</div>
              <div class="stat-value" :style="{ color: stat.color }">
                {{ stat.value.toLocaleString() }}
              </div>
            </div>
            <div class="stat-icon-wrapper" :style="{ background: stat.bg, color: stat.color }">
              <el-icon :size="24">
                <component :is="stat.icon" />
              </el-icon>
            </div>
          </div>
          <div class="stat-footer">
            <span class="stat-trend" :class="{ up: stat.trend.startsWith('+') }">
              {{ stat.trend }}
            </span>
            <span class="stat-period">较上周</span>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Main Content Grid -->
    <el-row :gutter="16" class="content-row">
      <!-- Recent Orders -->
      <el-col :xs="24" :lg="16">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <div class="panel-title">
                <el-icon><List /></el-icon>
                <span>最近订单</span>
              </div>
              <el-button type="primary" link @click="goToOrders">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>

          <el-table :data="recentOrders" style="width: 100%" size="small">
            <el-table-column prop="orderNo" label="订单号" min-width="160">
              <template #default="{ row }">
                <span class="order-no">{{ row.orderNo }}</span>
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="statusTag(row.orderStatus).type" size="small" effect="light">
                  <el-icon :size="12">
                    <component :is="statusTag(row.orderStatus).icon" />
                  </el-icon>
                  {{ statusTag(row.orderStatus).label }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="金额" width="100">
              <template #default="{ row }">
                <span class="price">¥{{ row.totalPrice }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="shipNo" label="船号" width="100" />
            <el-table-column prop="consigneeName" label="收货人" width="90" />
            <el-table-column prop="createdAt" label="下单时间" min-width="150" />
          </el-table>
        </el-card>
      </el-col>

      <!-- Quick Actions & Alerts -->
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <div class="panel-title">
                <el-icon><TrendCharts /></el-icon>
                <span>快捷操作</span>
              </div>
            </div>
          </template>

          <div class="quick-actions">
            <div class="action-item" @click="goToOrders">
              <div class="action-icon" style="background: #e6f4ff; color: #1677ff;">
                <el-icon><List /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">订单管理</div>
                <div class="action-desc">处理待确认和异常订单</div>
              </div>
              <el-icon><ArrowRight /></el-icon>
            </div>

            <div class="action-item" @click="goToUsers">
              <div class="action-icon" style="background: #f6ffed; color: #52c41a;">
                <el-icon><User /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">用户管理</div>
                <div class="action-desc">查看和管理用户信息</div>
              </div>
              <el-icon><ArrowRight /></el-icon>
            </div>

            <div class="action-item" @click="$router.push('/products')">
              <div class="action-icon" style="background: #fff7e6; color: #fa8c16;">
                <el-icon><Goods /></el-icon>
              </div>
              <div class="action-info">
                <div class="action-title">商品管理</div>
                <div class="action-desc">管理商品和库存</div>
              </div>
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </el-card>

        <el-card shadow="never" class="panel-card alert-card">
          <template #header>
            <div class="panel-header">
              <div class="panel-title">
                <el-icon><Warning /></el-icon>
                <span>待处理事项</span>
              </div>
            </div>
          </template>

          <div class="alert-list">
            <div class="alert-item alert-warning">
              <div class="alert-dot" style="background: #faad14;" />
              <div class="alert-content">
                <div class="alert-title">3 个订单待确认</div>
                <div class="alert-time">30 分钟内</div>
              </div>
            </div>
            <div class="alert-item alert-danger">
              <div class="alert-dot" style="background: #ff4d4f;" />
              <div class="alert-content">
                <div class="alert-title">1 个配送异常</div>
                <div class="alert-time">无人机故障</div>
              </div>
            </div>
            <div class="alert-item alert-info">
              <div class="alert-dot" style="background: #1677ff;" />
              <div class="alert-content">
                <div class="alert-title">5 个新用户注册</div>
                <div class="alert-time">今日</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped>
.dashboard {
  padding: 0;
}

/* Page Header */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.page-title {
  margin: 0 0 4px;
  font-size: 22px;
  font-weight: 600;
  color: #1a1a1a;
}

.page-subtitle {
  margin: 0;
  font-size: 13px;
  color: #999;
}

.header-actions {
  display: flex;
  gap: 12px;
}

/* Stats Cards */
.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  border-left: 4px solid;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.03);
  transition: all 0.3s;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
  transform: translateY(-2px);
}

.stat-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.stat-label {
  font-size: 13px;
  color: #999;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  line-height: 1;
}

.stat-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.stat-trend {
  font-weight: 600;
  color: #ff4d4f;
}

.stat-trend.up {
  color: #52c41a;
}

.stat-period {
  color: #999;
}

/* Content Grid */
.content-row {
  margin: 0 !important;
}

.panel-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.panel-card :deep(.el-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 600;
  color: #1a1a1a;
}

.panel-title .el-icon {
  color: #1677ff;
}

/* Quick Actions */
.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.action-item:hover {
  background: #f5f7fa;
}

.action-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.action-info {
  flex: 1;
}

.action-title {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 2px;
}

.action-desc {
  font-size: 12px;
  color: #999;
}

.action-item > .el-icon {
  color: #ccc;
}

/* Alerts */
.alert-card {
  margin-top: 16px;
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 6px;
  background: #fafafa;
}

.alert-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-size: 13px;
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.alert-time {
  font-size: 12px;
  color: #999;
}

/* Table Styles */
.order-no {
  font-family: 'SF Mono', monospace;
  font-size: 13px;
  color: #1677ff;
  font-weight: 500;
}

.price {
  font-weight: 600;
  color: #f5222d;
}

:deep(.el-tag) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
}
</style>
