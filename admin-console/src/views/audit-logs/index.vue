<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { getAuditLogs } from '@/api/audit'
import type { IAuditLog } from '@/types'
import { Search, Refresh } from '@element-plus/icons-vue'

const { t } = useI18n()

const loading = ref(false)
const rows = ref<IAuditLog[]>([])
const total = ref(0)

const query = reactive({
  module: '',
  start_time: '',
  end_time: '',
  page: 1,
  page_size: 20
})

const moduleOptions = ['USER', 'PRODUCT', 'ORDER', 'SHIP', 'AGENT', 'RULE', 'CUSTOMS', 'DRONE']

const dialogVisible = ref(false)
const detailRow = ref<IAuditLog | null>(null)

onMounted(() => { loadRows() })

async function loadRows() {
  loading.value = true
  try {
    const res = await getAuditLogs({
      module: query.module || undefined,
      start_time: query.start_time || undefined,
      end_time: query.end_time || undefined,
      page: query.page,
      page_size: query.page_size
    })
    rows.value = res.items
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function searchLogs() {
  query.page = 1
  loadRows()
}

function resetLogs() {
  query.module = ''
  query.start_time = ''
  query.end_time = ''
  query.page = 1
  loadRows()
}

function viewDetail(row: IAuditLog) {
  detailRow.value = row
  dialogVisible.value = true
}
</script>

<template>
  <div class="audit-page">
    <div class="page-header">
      <h2 class="page-title">{{ t('audit.title') }}</h2>
      <p class="page-subtitle">查看系统操作审计日志，追溯变更记录</p>
    </div>

    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item :label="t('audit.module')">
          <el-select v-model="query.module" clearable style="width: 140px">
            <el-option v-for="m in moduleOptions" :key="m" :label="t(`audit.modules.${m}`)" :value="m" />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('audit.dateRange')">
          <el-date-picker v-model="query.start_time" type="date" value-format="YYYY-MM-DD"
            :placeholder="t('audit.dateRange')" style="width: 140px" />
          <span style="margin: 0 8px; color: #999">-</span>
          <el-date-picker v-model="query.end_time" type="date" value-format="YYYY-MM-DD"
            :placeholder="t('audit.dateRange')" style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchLogs">{{ t('common.search') }}</el-button>
          <el-button :icon="Refresh" @click="resetLogs">{{ t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="actorName" :label="t('audit.actorName')" min-width="120" />
        <el-table-column prop="module" :label="t('audit.module')" min-width="100">
          <template #default="{ row }">
            <el-tag size="small" effect="light">{{ t(`audit.modules.${row.module}`) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="action" :label="t('audit.action')" min-width="100" />
        <el-table-column prop="targetType" :label="t('audit.targetType')" min-width="100" />
        <el-table-column prop="targetId" :label="t('audit.targetId')" min-width="120">
          <template #default="{ row }">
            <span class="mono">{{ row.targetId }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="clientIp" :label="t('audit.clientIp')" min-width="120" />
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="170" />
        <el-table-column :label="t('user.actions')" fixed="right" width="80">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row)">{{ t('common.edit') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-row">
        <el-pagination background layout="total, prev, pager, next"
          :page-size="query.page_size" :current-page="query.page" :total="total"
          @current-change="(page: number) => { query.page = page; loadRows() }" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="t('audit.detailTitle')" width="560px">
      <template v-if="detailRow">
        <el-descriptions :column="2" border>
          <el-descriptions-item :label="t('audit.actorName')">{{ detailRow.actorName }}</el-descriptions-item>
          <el-descriptions-item :label="t('audit.clientIp')">{{ detailRow.clientIp }}</el-descriptions-item>
          <el-descriptions-item :label="t('audit.module')">{{ detailRow.module }}</el-descriptions-item>
          <el-descriptions-item :label="t('audit.action')">{{ detailRow.action }}</el-descriptions-item>
          <el-descriptions-item :label="t('audit.targetType')">{{ detailRow.targetType }}</el-descriptions-item>
          <el-descriptions-item :label="t('audit.targetId')">{{ detailRow.targetId }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="detailRow.beforeValue" style="margin-top: 16px">
          <h4>{{ t('audit.beforeValue') }}</h4>
          <el-input type="textarea" :model-value="detailRow.beforeValue" :rows="3" readonly />
        </div>
        <div v-if="detailRow.afterValue" style="margin-top: 12px">
          <h4>{{ t('audit.afterValue') }}</h4>
          <el-input type="textarea" :model-value="detailRow.afterValue" :rows="3" readonly />
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.audit-page { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-title { margin: 0 0 4px; font-size: 22px; font-weight: 600; color: #1a1a1a; }
.page-subtitle { margin: 0; font-size: 13px; color: #999; }
.search-card { margin-bottom: 16px; border-radius: 8px; }
.mono { font-family: 'SF Mono', monospace; font-size: 13px; }
.pagination-row { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
