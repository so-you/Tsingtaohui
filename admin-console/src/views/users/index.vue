<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { getUsers, updateUserStatus } from '@/api/user'
import type { IUserInfo } from '@/types'
import { Search, Refresh } from '@element-plus/icons-vue'

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

function userTypeTag(type?: string) {
  const colors: Record<string, string> = {
    'ADMIN': 'danger',
    'OPERATOR': 'warning',
    'CUSTOMER': 'success',
    'WAREHOUSE_OPERATOR': 'primary',
    'DRONE_DISPATCHER': 'info',
    'FINANCE': 'info'
  }
  return colors[type || ''] || 'info'
}

function avatarLetter(name?: string) {
  return name ? name.charAt(0).toUpperCase() : '?'
}
</script>

<template>
  <div class="users-page">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h2 class="page-title">{{ t('user.title') }}</h2>
        <p class="page-subtitle">管理平台用户账号和权限</p>
      </div>
    </div>

    <!-- Search Card -->
    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item :label="t('user.username')">
          <el-input
            v-model="query.keyword"
            :placeholder="t('user.username')"
            clearable
            style="width: 220px"
            :prefix-icon="Search"
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
          <el-button type="primary" :icon="Search" @click="handleSearch">{{ t('common.search') }}</el-button>
          <el-button :icon="Refresh" @click="handleReset">{{ t('common.reset') }}</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table Card -->
    <el-card shadow="never" class="table-card">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column label="用户" min-width="180">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="36" class="user-avatar">
                {{ avatarLetter(row.displayName || row.username) }}
              </el-avatar>
              <div class="user-info">
                <div class="user-name">{{ row.displayName || row.username }}</div>
                <div class="user-username">{{ row.username }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="t('user.userType')" min-width="150">
          <template #default="{ row }">
            <el-tag :type="userTypeTag(row.userType)" size="small" effect="light">
              {{ row.userType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="t('user.status')" min-width="110">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small" effect="light">
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

.table-card {
  border-radius: 8px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  background: #1677ff;
  color: #fff;
  font-size: 14px;
  font-weight: 600;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
}

.user-username {
  font-size: 12px;
  color: #999;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
