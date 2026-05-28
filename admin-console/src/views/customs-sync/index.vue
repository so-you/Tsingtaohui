<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCustomsSyncRecords, retryCustomsSync } from '@/api/customs'
import type { ICustomsSyncRecord, TCustomsSyncLevel, TCustomsSyncStatus } from '@/types'
import { Search, Refresh, RefreshRight } from '@element-plus/icons-vue'

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
  const map: Record<TCustomsSyncLevel, { type: 'danger' | 'warning', label: string }> = {
    RED: { type: 'danger', label: '红牌' },
    YELLOW: { type: 'warning', label: '黄牌' }
  }
  return map[level] || { type: 'info' as const, label: level }
}

function statusTag(status: TCustomsSyncStatus) {
  const map: Record<TCustomsSyncStatus, { type: '' | 'success' | 'warning' | 'info' | 'danger', label: string }> = {
    SYNC_NONE: { type: 'info', label: '未同步' },
    SYNCING: { type: '', label: '同步中' },
    SYNC_SUCCESS: { type: 'success', label: '同步成功' },
    SYNC_FAILED: { type: 'danger', label: '同步失败' },
    RETRYING: { type: 'warning', label: '重试中' },
    MANUAL_RESOLVED: { type: 'success', label: '人工解决' }
  }
  return map[status] || { type: 'info' as const, label: status }
}

function canRetry(status: TCustomsSyncStatus) {
  return ['SYNC_FAILED', 'RETRYING'].includes(status)
}

async function handleRetry(row: ICustomsSyncRecord) {
  try {
    await ElMessageBox.confirm(
      `确认重试同步记录 ${row.syncNo}？`,
      '重试确认',
      { type: 'warning', confirmButtonText: '确认重试', cancelButtonText: '取消' }
    )
    await retryCustomsSync(row.syncNo)
    ElMessage.success('已触发重试')
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
        <h2 class="page-title">海关同步管理</h2>
        <p class="page-subtitle">监控海关数据同步状态、处理失败记录和重试操作</p>
      </div>
    </div>

    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="同步编号 / 订单号"
            clearable
            style="width: 220px"
            :prefix-icon="Search"
            @keyup.enter="searchRecords"
          />
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="query.level" clearable style="width: 120px">
            <el-option
              v-for="item in levelOptions"
              :key="item"
              :label="levelTag(item).label"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
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
          <el-button type="primary" :icon="Search" @click="searchRecords">搜索</el-button>
          <el-button :icon="Refresh" @click="resetRecords">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="syncNo" label="同步编号" min-width="180">
          <template #default="{ row }">
            <span class="sync-no">{{ row.syncNo }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderNo" label="订单号" min-width="160">
          <template #default="{ row }">
            <span class="order-no">{{ row.orderNo || row.orderId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="nodeType" label="同步节点" min-width="150" />
        <el-table-column label="级别" min-width="90">
          <template #default="{ row }">
            <el-tag :type="levelTag(row.level).type" size="small" effect="light">
              {{ levelTag(row.level).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="120">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status).type" size="small" effect="light">
              {{ statusTag(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="retryCount" label="重试次数" min-width="100">
          <template #default="{ row }">
            <span>{{ row.retryCount }} / {{ row.maxRetries }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
        <el-table-column prop="updatedAt" label="更新时间" min-width="170" />
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
            <el-button
              v-if="canRetry(row.status)"
              type="warning"
              link
              size="small"
              :icon="RefreshRight"
              @click="handleRetry(row)"
            >
              重试
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
</style>
