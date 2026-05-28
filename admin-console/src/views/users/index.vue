<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getUsers, updateUserStatus } from '@/api/user'
import type { IUserInfo } from '@/types'

const { t } = useI18n()

const loading = ref(false)
const tableData = ref<IUserInfo[]>([])
const total = ref(0)

const query = reactive({
  keyword: '',
  user_type: '',
  status: '',
  page: 1,
  page_size: 10
})

const userTypeOptions = ['CUSTOMER', 'WAREHOUSE_OPERATOR', 'ADMIN', 'OPERATOR', 'DRONE_DISPATCHER', 'FINANCE']
const statusOptions = ['ENABLED', 'DISABLED', 'LOCKED']

onMounted(() => {
  loadUsers()
})

async function loadUsers() {
  loading.value = true
  try {
    const res = await getUsers({
      keyword: query.keyword || undefined,
      user_type: query.user_type || undefined,
      status: query.status || undefined,
      page: query.page,
      page_size: query.page_size
    })
    tableData.value = res.items
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadUsers()
}

function handleReset() {
  query.keyword = ''
  query.user_type = ''
  query.status = ''
  query.page = 1
  loadUsers()
}

async function handleStatus(row: IUserInfo, status: string) {
  if (!row.id || row.status === status) return
  await updateUserStatus(row.id, status)
  ElMessage.success(t('common.success'))
  loadUsers()
}

function handlePageChange(page: number) {
  query.page = page
  loadUsers()
}

function statusTag(status?: string) {
  if (status === 'ENABLED') return 'success'
  if (status === 'DISABLED') return 'info'
  if (status === 'LOCKED') return 'danger'
  return 'warning'
}
</script>

<template>
  <div class="users-page">
    <h2 class="page-title">{{ t('user.title') }}</h2>

    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item :label="t('user.username')">
          <el-input
            v-model="query.keyword"
            :placeholder="t('user.username')"
            clearable
            style="width: 220px"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item :label="t('user.userType')">
          <el-select v-model="query.user_type" clearable style="width: 190px">
            <el-option
              v-for="item in userTypeOptions"
              :key="item"
              :label="item"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item :label="t('user.status')">
          <el-select v-model="query.status" clearable style="width: 150px">
            <el-option
              v-for="item in statusOptions"
              :key="item"
              :label="t(`user.${item.toLowerCase()}`)"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">{{ t('common.search') }}</el-button>
          <el-button @click="handleReset">{{ t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="id" label="ID" min-width="170" />
        <el-table-column prop="username" :label="t('user.username')" min-width="150" />
        <el-table-column prop="displayName" :label="t('user.displayName')" min-width="140" />
        <el-table-column prop="userType" :label="t('user.userType')" min-width="150" />
        <el-table-column prop="status" :label="t('user.status')" min-width="110">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">
              {{ row.status ? t(`user.${row.status.toLowerCase()}`) : '-' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="shipNo" :label="t('user.shipNo')" min-width="140" />
        <el-table-column prop="shipNationality" :label="t('user.shipNationality')" min-width="130" />
        <el-table-column prop="imo" :label="t('user.imo')" min-width="130" />
        <el-table-column prop="mmsi" :label="t('user.mmsi')" min-width="130" />
        <el-table-column prop="createdAt" :label="t('common.createdAt')" min-width="170" />
        <el-table-column :label="t('user.actions')" fixed="right" width="190">
          <template #default="{ row }">
            <el-button
              v-if="row.status !== 'ENABLED'"
              type="success"
              link
              size="small"
              @click="handleStatus(row, 'ENABLED')"
            >
              {{ t('user.enableAction') }}
            </el-button>
            <el-button
              v-if="row.status === 'ENABLED'"
              type="warning"
              link
              size="small"
              @click="handleStatus(row, 'DISABLED')"
            >
              {{ t('user.disableAction') }}
            </el-button>
            <el-button
              v-if="row.status !== 'LOCKED'"
              type="danger"
              link
              size="small"
              @click="handleStatus(row, 'LOCKED')"
            >
              {{ t('user.lockAction') }}
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
          @current-change="handlePageChange"
        />
      </div>
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

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
