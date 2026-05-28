<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getMatchingOrders, getOrders, updateOrderStatus } from '@/api/order'
import type { IOrder, TOrderStatus, TTradeMode } from '@/types'

const { t } = useI18n()

const activeTab = ref<'orders' | 'matching'>('orders')
const loading = ref(false)
const rows = ref<IOrder[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  status: '' as TOrderStatus | '',
  tradeMode: '' as TTradeMode | '',
  page: 1,
  page_size: 10
})

const statusOptions: TOrderStatus[] = [
  'PENDING_CONFIRM',
  'CONFIRMED',
  'WAREHOUSE_PROCESSING',
  'PENDING_OUTBOUND',
  'OUTBOUND',
  'PENDING_LOADING',
  'IN_DELIVERY',
  'PENDING_RECEIPT',
  'COMPLETED',
  'CANCELLED',
  'EXCEPTION'
]

const tradeModeOptions: TTradeMode[] = ['AUTO_TRADE', 'MATCHING_ORDER']
const currentRows = computed(() => rows.value)

onMounted(() => {
  loadRows()
})

async function loadRows() {
  loading.value = true
  try {
    const params = {
      keyword: query.keyword || undefined,
      status: query.status || undefined,
      page: query.page,
      page_size: query.page_size
    }
    const res = activeTab.value === 'matching'
      ? await getMatchingOrders(params)
      : await getOrders({ ...params, trade_mode: query.tradeMode || undefined })
    rows.value = res.items
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function searchOrders() {
  query.page = 1
  loadRows()
}

function resetOrders() {
  query.keyword = ''
  query.status = ''
  query.tradeMode = ''
  query.page = 1
  loadRows()
}

function switchTab(tab: string | number) {
  activeTab.value = tab as 'orders' | 'matching'
  query.page = 1
  if (activeTab.value === 'matching') {
    query.tradeMode = ''
  }
  loadRows()
}

async function handleStatus(row: IOrder, status: TOrderStatus) {
  if (row.orderStatus === status) return
  await updateOrderStatus(row.id, status)
  ElMessage.success(t('common.success'))
  loadRows()
}

function statusTag(status: TOrderStatus) {
  if (status === 'COMPLETED') return 'success'
  if (status === 'EXCEPTION' || status === 'CANCELLED') return 'danger'
  if (status === 'PENDING_CONFIRM' || status === 'PENDING_RECEIPT') return 'warning'
  return 'primary'
}

function tradeModeTag(mode: TTradeMode) {
  return mode === 'AUTO_TRADE' ? 'success' : 'warning'
}

function terminal(status: TOrderStatus) {
  return ['COMPLETED', 'CANCELLED', 'EXCEPTION'].includes(status)
}
</script>

<template>
  <div class="orders-page">
    <h2 class="page-title">{{ t('menu.orders') }}</h2>

    <el-tabs v-model="activeTab" @tab-change="switchTab">
      <el-tab-pane :label="t('order.ordersTab')" name="orders" />
      <el-tab-pane :label="t('order.matchingTab')" name="matching" />
    </el-tabs>

    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item :label="t('order.keyword')">
          <el-input
            v-model="query.keyword"
            :placeholder="t('order.keywordPlaceholder')"
            clearable
            style="width: 240px"
            @keyup.enter="searchOrders"
          />
        </el-form-item>
        <el-form-item :label="t('order.status')">
          <el-select v-model="query.status" clearable style="width: 180px">
            <el-option
              v-for="item in statusOptions"
              :key="item"
              :label="t(`order.statuses.${item}`)"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item v-if="activeTab === 'orders'" :label="t('order.tradeMode')">
          <el-select v-model="query.tradeMode" clearable style="width: 170px">
            <el-option
              v-for="item in tradeModeOptions"
              :key="item"
              :label="t(`order.tradeModes.${item}`)"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchOrders">{{ t('common.search') }}</el-button>
          <el-button @click="resetOrders">{{ t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="currentRows" stripe>
        <el-table-column prop="orderNo" :label="t('order.orderNo')" min-width="180" />
        <el-table-column :label="t('order.tradeMode')" min-width="130">
          <template #default="{ row }">
            <el-tag :type="tradeModeTag(row.tradeMode)">
              {{ t(`order.tradeModes.${row.tradeMode}`) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('order.status')" min-width="150">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.orderStatus)">
              {{ t(`order.statuses.${row.orderStatus}`) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalPrice" :label="t('order.totalPrice')" min-width="110" />
        <el-table-column prop="shipNo" :label="t('user.shipNo')" min-width="130" />
        <el-table-column prop="shipNationality" :label="t('user.shipNationality')" min-width="120" />
        <el-table-column prop="consigneeName" :label="t('order.consigneeName')" min-width="140" />
        <el-table-column prop="cabinNo" :label="t('order.cabinNo')" min-width="100" />
        <el-table-column prop="totalWeightKg" :label="t('order.totalWeight')" min-width="120" />
        <el-table-column prop="totalVolumeM3" :label="t('order.totalVolume')" min-width="120" />
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="170" />
        <el-table-column :label="t('user.actions')" fixed="right" width="220">
          <template #default="{ row }">
            <el-button
              v-if="row.orderStatus === 'PENDING_CONFIRM'"
              type="success"
              link
              size="small"
              @click="handleStatus(row, 'CONFIRMED')"
            >
              {{ t('order.confirmAction') }}
            </el-button>
            <el-button
              v-if="!terminal(row.orderStatus)"
              type="warning"
              link
              size="small"
              @click="handleStatus(row, 'EXCEPTION')"
            >
              {{ t('order.exceptionAction') }}
            </el-button>
            <el-button
              v-if="!terminal(row.orderStatus)"
              type="danger"
              link
              size="small"
              @click="handleStatus(row, 'CANCELLED')"
            >
              {{ t('order.cancelAction') }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-row">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :page-size="query.page_size"
          :current-page="query.page"
          :total="total"
          @current-change="(page: number) => { query.page = page; loadRows() }"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.orders-page {
  padding: 20px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 20px;
  color: #303133;
}

.search-card {
  margin-bottom: 16px;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
