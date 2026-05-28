<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getShips, createShip, updateShip, getShippingAgents } from '@/api/ship'
import type { IShip, IShippingAgent } from '@/types'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const { t } = useI18n()

const activeTab = ref<'ships' | 'agents'>('ships')
const loading = ref(false)
const rows = ref<IShip[] | IShippingAgent[]>([])
const total = ref(0)

const shipQuery = reactive({
  keyword: '',
  nationality: '',
  page: 1,
  page_size: 10
})

const agentQuery = reactive({
  keyword: '',
  status: '',
  page: 1,
  page_size: 10
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref<number | null>(null)
const form = reactive({
  shipNo: '',
  shipName: '',
  shipNationality: '',
  imo: '',
  mmsi: '',
  currentBerth: '',
  currentAnchorage: '',
  targetGps: ''
})

onMounted(() => { loadRows() })

async function loadRows() {
  loading.value = true
  try {
    if (activeTab.value === 'ships') {
      const res = await getShips({
        keyword: shipQuery.keyword || undefined,
        nationality: shipQuery.nationality || undefined,
        page: shipQuery.page,
        page_size: shipQuery.page_size
      })
      rows.value = res.items
      total.value = res.total
    } else {
      const res = await getShippingAgents({
        keyword: agentQuery.keyword || undefined,
        status: agentQuery.status || undefined,
        page: agentQuery.page,
        page_size: agentQuery.page_size
      })
      rows.value = res.items
      total.value = res.total
    }
  } finally {
    loading.value = false
  }
}

function searchRows() {
  if (activeTab.value === 'ships') shipQuery.page = 1
  else agentQuery.page = 1
  loadRows()
}

function resetRows() {
  if (activeTab.value === 'ships') {
    shipQuery.keyword = ''
    shipQuery.nationality = ''
    shipQuery.page = 1
  } else {
    agentQuery.keyword = ''
    agentQuery.status = ''
    agentQuery.page = 1
  }
  loadRows()
}

function switchTab(tab: string | number) {
  activeTab.value = tab as 'ships' | 'agents'
  loadRows()
}

function openCreate() {
  isEdit.value = false
  editId.value = null
  Object.assign(form, { shipNo: '', shipName: '', shipNationality: '', imo: '', mmsi: '', currentBerth: '', currentAnchorage: '', targetGps: '' })
  dialogVisible.value = true
}

function openEdit(row: IShip) {
  isEdit.value = true
  editId.value = row.id
  Object.assign(form, {
    shipNo: row.shipNo,
    shipName: row.shipName || '',
    shipNationality: row.shipNationality,
    imo: row.imo || '',
    mmsi: row.mmsi || '',
    currentBerth: row.currentBerth || '',
    currentAnchorage: row.currentAnchorage || '',
    targetGps: row.targetGps || ''
  })
  dialogVisible.value = true
}

async function submitForm() {
  if (isEdit.value && editId.value) {
    await updateShip(editId.value, { ...form })
  } else {
    await createShip({ ...form })
  }
  ElMessage.success(t('common.success'))
  dialogVisible.value = false
  loadRows()
}

const currentQuery = computed(() => activeTab.value === 'ships' ? shipQuery : agentQuery)
</script>

<template>
  <div class="ships-page">
    <div class="page-header">
      <h2 class="page-title">{{ t('ship.title') }}</h2>
      <p class="page-subtitle">{{ activeTab === 'ships' ? '管理船舶主数据信息' : '查看船舶代理人列表' }}</p>
    </div>

    <el-tabs v-model="activeTab" @tab-change="switchTab" type="border-card" class="page-tabs">
      <el-tab-pane :label="t('ship.shipsTab')" name="ships" />
      <el-tab-pane :label="t('ship.agentsTab')" name="agents" />
    </el-tabs>

    <el-card shadow="never" class="search-card">
      <el-form inline>
        <el-form-item :label="t('ship.keywordPlaceholder')">
          <el-input v-model="(activeTab === 'ships' ? shipQuery : agentQuery).keyword"
            :placeholder="t('ship.keywordPlaceholder')" clearable style="width: 240px"
            :prefix-icon="Search" @keyup.enter="searchRows" />
        </el-form-item>
        <el-form-item v-if="activeTab === 'ships'" :label="t('ship.shipNationality')">
          <el-input v-model="shipQuery.nationality" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item v-if="activeTab === 'agents'" :label="t('user.status')">
          <el-select v-model="agentQuery.status" clearable style="width: 140px">
            <el-option label="ENABLED" value="ENABLED" />
            <el-option label="DISABLED" value="DISABLED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="searchRows">{{ t('common.search') }}</el-button>
          <el-button :icon="Refresh" @click="resetRows">{{ t('common.reset') }}</el-button>
          <el-button v-if="activeTab === 'ships'" type="primary" :icon="Plus" @click="openCreate">{{ t('ship.addShip') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Ships Table -->
    <el-card v-if="activeTab === 'ships'" shadow="never">
      <el-table v-loading="loading" :data="rows as IShip[]" stripe>
        <el-table-column prop="shipNo" :label="t('ship.shipNo')" min-width="120" />
        <el-table-column prop="shipName" :label="t('ship.shipName')" min-width="120" />
        <el-table-column prop="shipNationality" :label="t('ship.shipNationality')" min-width="100" />
        <el-table-column prop="imo" :label="t('ship.imo')" min-width="120" />
        <el-table-column prop="mmsi" :label="t('ship.mmsi')" min-width="120" />
        <el-table-column prop="currentBerth" :label="t('ship.currentBerth')" min-width="120" />
        <el-table-column prop="currentAnchorage" :label="t('ship.currentAnchorage')" min-width="120" />
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="170" />
        <el-table-column :label="t('user.actions')" fixed="right" width="100">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">{{ t('common.edit') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Agents Table -->
    <el-card v-if="activeTab === 'agents'" shadow="never">
      <el-table v-loading="loading" :data="rows as IShippingAgent[]" stripe>
        <el-table-column prop="agentNameZh" :label="t('ship.agentNameZh')" min-width="140" />
        <el-table-column prop="agentNameEn" :label="t('ship.agentNameEn')" min-width="140" />
        <el-table-column prop="contactName" :label="t('ship.contactName')" min-width="120" />
        <el-table-column prop="contactPhone" :label="t('ship.contactPhone')" min-width="130" />
        <el-table-column prop="status" :label="t('user.status')" min-width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ENABLED' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="170" />
      </el-table>
    </el-card>

    <div class="pagination-row">
      <el-pagination background layout="total, prev, pager, next"
        :page-size="currentQuery.page_size"
        :current-page="currentQuery.page"
        :total="total"
        @current-change="(page: number) => { currentQuery.page = page; loadRows() }" />
    </div>

    <!-- Ship Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? t('ship.editShip') : t('ship.addShip')" width="520px">
      <el-form :model="form" label-width="100px">
        <el-form-item :label="t('ship.shipNo')" required>
          <el-input v-model="form.shipNo" />
        </el-form-item>
        <el-form-item :label="t('ship.shipName')">
          <el-input v-model="form.shipName" />
        </el-form-item>
        <el-form-item :label="t('ship.shipNationality')" required>
          <el-input v-model="form.shipNationality" />
        </el-form-item>
        <el-form-item :label="t('ship.imo')">
          <el-input v-model="form.imo" />
        </el-form-item>
        <el-form-item :label="t('ship.mmsi')">
          <el-input v-model="form.mmsi" />
        </el-form-item>
        <el-form-item :label="t('ship.currentBerth')">
          <el-input v-model="form.currentBerth" />
        </el-form-item>
        <el-form-item :label="t('ship.currentAnchorage')">
          <el-input v-model="form.currentAnchorage" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submitForm">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.ships-page { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-title { margin: 0 0 4px; font-size: 22px; font-weight: 600; color: #1a1a1a; }
.page-subtitle { margin: 0; font-size: 13px; color: #999; }
.page-tabs { margin-bottom: 16px; }
.search-card { margin-bottom: 16px; border-radius: 8px; }
.pagination-row { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
