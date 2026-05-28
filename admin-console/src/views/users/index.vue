<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const searchKeyword = ref('')

interface ITableRow {
  id: number
  username: string
  userType: string
  status: string
  createdAt: string
}

const tableData = ref<ITableRow[]>([
  { id: 1, username: 'captain_zhang', userType: 'crew', status: 'enabled', createdAt: '2026-05-20' },
  { id: 2, username: 'sailor_li', userType: 'crew', status: 'enabled', createdAt: '2026-05-21' },
  { id: 3, username: 'agent_wang', userType: 'agent', status: 'disabled', createdAt: '2026-05-22' },
  { id: 4, username: 'crew_chen', userType: 'crew', status: 'enabled', createdAt: '2026-05-23' },
  { id: 5, username: 'manager_zhao', userType: 'admin', status: 'enabled', createdAt: '2026-05-24' }
])

function handleSearch() {
  // TODO: connect to API
}

function handleReset() {
  searchKeyword.value = ''
}
</script>

<template>
  <div class="users-page">
    <h2 class="page-title">{{ t('user.title') }}</h2>

    <el-card shadow="never" class="search-card">
      <el-row :gutter="16" align="middle">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            :placeholder="t('user.username')"
            clearable
          />
        </el-col>
        <el-col :span="16">
          <el-button type="primary" @click="handleSearch">{{ t('common.search') }}</el-button>
          <el-button @click="handleReset">{{ t('common.reset') }}</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table :data="tableData" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" :label="t('user.username')" />
        <el-table-column prop="userType" :label="t('user.userType')" />
        <el-table-column prop="status" :label="t('user.status')">
          <template #default="{ row }">
            <el-tag :type="row.status === 'enabled' ? 'success' : 'danger'">
              {{ row.status === 'enabled' ? t('user.enabled') : t('user.disabled') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="Created At" />
        <el-table-column :label="t('user.actions')" width="200">
          <template #default>
            <el-button type="primary" link size="small">{{ t('common.edit') }}</el-button>
            <el-button type="danger" link size="small">{{ t('common.delete') }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<style scoped>
.users-page {
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

.table-card {
  margin-bottom: 16px;
}
</style>
