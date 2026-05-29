<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomsSyncRecords, retryCustomsSync } from '@/api/customs'
import type { ICustomsSyncRecord, TCustomsSyncLevel, TCustomsSyncStatus } from '@/types'
import { Search, Refresh, RefreshRight } from '@element-plus/icons-vue'

const { t } = useI18n()
const loading = ref(false)
const rows = ref<ICustomsSyncRecord[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  status: '' as TCustomsSyncStatus | '',
  level: '' as TCustomsSyncLevel | '',
  page: 1,
  page_size: 10
})

const statusOptions: TCustomsSyncStatus[] = [
  'SYNC_NONE', 'SYNCING', 'SYNC_SUCCESS', 'SYNC_FAILED', 'RETRYING', 'MANUAL_RESOLVED'
]
const levelOptions: TCustomsSyncLevel[] = ['RED', 'YELLOW']

onMounted(() => {
  loadRows()
})

async function loadRows() {
  loading.value = true
  try {
    const params = {
      keyword: query.keyword || undefined,
      status: query.status || undefined,
      level: query.level || undefined,
      page: query.page,
      page_size: query.page_size
    }
    const res = await getCustomsSyncRecords(params)
    rows.value = res.items
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function searchRecords() {
  query.page = 1
  loadRows()
}

function resetRecords() {
  query.keyword = ''
  query.status = ''
  query.level = ''
  query.page = 1
  loadRows()
}

function levelTag(level: TCustomsSyncLevel) {
  const map: Record<TCustomsSyncLevel, 'danger' | 'warning'> = {
    RED: 'danger',
    YELLOW: 'warning'
  }
  const type = map[level] || 'info'
  return { type: type as 'danger' | 'warning', label: t(`customs.levels.${level}`) || level }
}

function statusTag(status: TCustomsSyncStatus) {
  const map: Record<TCustomsSyncStatus, '' | 'success' | 'warning' | 'info' | 'danger'> = {
    SYNC_NONE: 'info',
    SYNCING: '',
    SYNC_SUCCESS: 'success',
    SYNC_FAILED: 'danger',
    RETRYING: 'warning',
    MANUAL_RESOLVED: 'success'
  }
  const type = map[status] || 'info'
  return { type: type as '' | 'success' | 'warning' | 'info' | 'danger', label: t(`customs.statuses.${status}`) || status }
}

function canRetry(status: TCustomsSyncStatus) {
  return ['SYNC_FAILED', 'RETRYING'].includes(status)
}

async function handleRetry(row: ICustomsSyncRecord) {
  try {
    await ElMessageBox.confirm(
      t('customs.retryConfirmMessage', { syncNo: row.syncNo }),
      t('customs.retryConfirmTitle'),
      { type: 'warning', confirmButtonText: t('customs.retryConfirmBtn'), cancelButtonText: t('common.cancel') }
    )
    await retryCustomsSync(row.syncNo)
    ElMessage.success(t('customs.retryTriggered'))
    loadRows()
  } catch {
    // user cancelled
  }
}
</script>

<template>
  <div class="customs-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">{{ t('customs.title') }}</h2>
        <p class="page-subtitle">{{ t('customs.pageSubtitle') }}</p>
      </div>
    </div>

    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item :label="t('customs.keywordLabel')">
          <el-input
            v-model="query.keyword"
            :placeholder="t('customs.keywordPlaceholder')"
            clearable
            style="width: 220px"
            :prefix-icon="Search"
            @keyup.enter="searchRecords"
          />
        </el-form-item>
        <el-form-item :label="t('customs.level')">
          <el-select v-model="query.level" clearable style="width: 120px">
            <el-option
              v-for="item in levelOptions"
              :key="item"
              :label="levelTag(item).label"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('customs.status')">
          <el-select v-model="query.status" clearable style="width: 150px">
            <el-option
              v-for="item in statusOptions"
              :key="item"
              :label="statusTag(item).label"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchRecords">{{ t('common.search') }}</el-button>
          <el-button :icon="Refresh" @click="resetRecords">{{ t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="syncNo" :label="t('customs.syncNo')" min-width="180">
          <template #default="{ row }">
            <span class="sync-no">{{ row.syncNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" :label="t('customs.orderId')" min-width="160">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo || row.orderId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nodeType" :label="t('customs.nodeType')" min-width="150" />
        <el-table-column :label="t('customs.level')" min-width="90">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.level).type" size="small" effect="light">
              {{ levelTag(row.level).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('customs.status')" min-width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status).type" size="small" effect="light">
              {{ statusTag(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="retryCount" :label="t('customs.retryCount')" min-width="100">
          <template #default="{ row }">
            <span>{{ row.retryCount }} / {{ row.maxRetries }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="170" />
        <el-table-column prop="updatedAt" :label="t('common.updatedAt')" min-width="170" />
        <el-table-column :label="t('user.actions')" fixed="right" width="120">
          <template #default="{ row }">
            <el-button
              v-if="canRetry(row.status)"
              type="warning"
              link
              size="small"
              :icon="RefreshRight"
              @click="handleRetry(row)"
            >
              {{ t('customs.retryAction') }}
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
.customs-page {
  padding: 0;
}

.page-header {
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

.search-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.sync-no {
  font-family: 'SF Mono', monospace;
  font-size: 13px;
  color: #1677ff;
  font-weight: 500;
}

.order-no {
  font-family: 'SF Mono', monospace;
  font-size: 13px;
  color: #333;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  .search-card :deep(.el-form--inline .el-form-item) {
    display: block;
    margin-right: 0;
    margin-bottom: 12px;
    width: 100%;
  }
  .search-card :deep(.el-form--inline .el-form-item .el-input),
  .search-card :deep(.el-form--inline .el-form-item .el-select) {
    width: 100% !important;
  }
  .pagination-row {
    justify-content: center;
  }
}
</style>
