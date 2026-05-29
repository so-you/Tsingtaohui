<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useResponsive } from '@/composables/useResponsive'
import { ElMessage } from 'element-plus'
import { getRules, updateRule } from '@/api/rule'
import type { IRuleConfig } from '@/types'
import { Search, Refresh } from '@element-plus/icons-vue'

const { t } = useI18n()
const { isMobile } = useResponsive()

const loading = ref(false)
const rows = ref<IRuleConfig[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  status: '',
  page: 1,
  page_size: 20
})

const dialogVisible = ref(false)
const editForm = reactive({
  id: 0,
  ruleValue: '',
  description: '',
  status: ''
})

onMounted(() => { loadRows() })

async function loadRows() {
  loading.value = true
  try {
    const res = await getRules({
      keyword: query.keyword || undefined,
      status: query.status || undefined,
      page: query.page,
      page_size: query.page_size
    })
    rows.value = res.items
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function searchRules() {
  query.page = 1
  loadRows()
}

function resetRules() {
  query.keyword = ''
  query.status = ''
  query.page = 1
  loadRows()
}

function openEdit(row: IRuleConfig) {
  editForm.id = row.id
  editForm.ruleValue = row.ruleValue
  editForm.description = row.description || ''
  editForm.status = row.status
  dialogVisible.value = true
}

async function submitEdit() {
  await updateRule(editForm.id, {
    ruleValue: editForm.ruleValue,
    description: editForm.description,
    status: editForm.status
  })
  ElMessage.success(t('common.success'))
  dialogVisible.value = false
  loadRows()
}
</script>

<template>
  <div class="rules-page">
    <div class="page-header">
      <h2 class="page-title">{{ t('rule.title') }}</h2>
      <p class="page-subtitle">{{ t('rule.pageSubtitle') }}</p>
    </div>

    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item :label="t('ship.keywordPlaceholder')">
          <el-input v-model="query.keyword" :placeholder="t('rule.searchPlaceholder')" clearable style="width: 240px"
            :prefix-icon="Search" @keyup.enter="searchRules" />
        </el-form-item>
        <el-form-item :label="t('user.status')">
          <el-select v-model="query.status" clearable style="width: 140px">
            <el-option :label="t('rule.statuses.ENABLED')" value="ENABLED" />
            <el-option :label="t('rule.statuses.DISABLED')" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchRules">{{ t('common.search') }}</el-button>
          <el-button :icon="Refresh" @click="resetRules">{{ t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="ruleKey" :label="t('rule.ruleKey')" min-width="240">
          <template #default="{ row }">
            <span class="rule-key">{{ row.ruleKey }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ruleNameZh" :label="t('rule.ruleName')" min-width="180" />
        <el-table-column prop="ruleValue" :label="t('rule.ruleValue')" min-width="140" />
        <el-table-column prop="description" :label="t('rule.description')" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" :label="t('user.status')" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'info'" size="small">
              {{ t(`rule.statuses.${row.status}`) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" :label="t('common.updatedAt')" min-width="170" />
        <el-table-column :label="t('user.actions')" fixed="right" width="100">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">{{ t('common.edit') }}</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-row">
        <el-pagination background layout="total, prev, pager, next"
          :page-size="query.page_size" :current-page="query.page" :total="total"
          @current-change="(page: number) => { query.page = page; loadRows() }" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="t('rule.editTitle')" :fullscreen="isMobile">
      <el-form :model="editForm" label-width="80px">
        <el-form-item :label="t('rule.ruleValue')">
          <el-input v-model="editForm.ruleValue" />
        </el-form-item>
        <el-form-item :label="t('rule.description')">
          <el-input v-model="editForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item :label="t('user.status')">
          <el-select v-model="editForm.status" style="width: 100%">
            <el-option :label="t('rule.statuses.ENABLED')" value="ENABLED" />
            <el-option :label="t('rule.statuses.DISABLED')" value="DISABLED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submitEdit">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.rules-page { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-title { margin: 0 0 4px; font-size: 22px; font-weight: 600; color: #1a1a1a; }
.page-subtitle { margin: 0; font-size: 13px; color: #999; }
.search-card { margin-bottom: 16px; border-radius: 8px; }
.rule-key { font-family: 'SF Mono', monospace; font-size: 13px; color: #1677ff; }
.pagination-row { display: flex; justify-content: flex-end; margin-top: 16px; }

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
