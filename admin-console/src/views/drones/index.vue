<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getDrones, createDrone } from '@/api/drone'
import type { ICreateDroneParams, IDrone, TDroneStatus } from '@/types'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const rows = ref<IDrone[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitting = ref(false)

const query = reactive({
  keyword: '',
  status: '' as TDroneStatus | '',
  page: 1,
  page_size: 10
})

const statusOptions: TDroneStatus[] = ['AVAILABLE', 'DISPATCHED', 'MAINTENANCE', 'OFFLINE']

const defaultForm: ICreateDroneParams = {
  droneCode: '',
  model: '',
  flightNo: '',
  maxPayloadKg: 0,
  maxVolumeM3: 0,
  maxRangeKm: 0,
  deliverableCategories: [],
  status: 'AVAILABLE'
}

const form = reactive<ICreateDroneParams>({ ...defaultForm })

const formRules: FormRules = {
  droneCode: [{ required: true, message: '请输入无人机编码', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
  flightNo: [{ required: true, message: '请输入航班号', trigger: 'blur' }],
  maxPayloadKg: [{ required: true, message: '请输入最大载重', trigger: 'blur' }],
  maxVolumeM3: [{ required: true, message: '请输入最大体积', trigger: 'blur' }],
  maxRangeKm: [{ required: true, message: '请输入最大航程', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
}

const categoryInput = ref('')

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
    const res = await getDrones(params)
    rows.value = res.items
    total.value = res.total
  } finally {
    loading.value = false
  }
}

function searchDrones() {
  query.page = 1
  loadRows()
}

function resetDrones() {
  query.keyword = ''
  query.status = ''
  query.page = 1
  loadRows()
}

function statusTag(status: TDroneStatus) {
  const map: Record<TDroneStatus, { type: '' | 'success' | 'warning' | 'info' | 'danger', label: string }> = {
    AVAILABLE: { type: 'success', label: '可用' },
    DISPATCHED: { type: '', label: '已调度' },
    MAINTENANCE: { type: 'warning', label: '维护中' },
    OFFLINE: { type: 'info', label: '离线' }
  }
  return map[status] || { type: 'info' as const, label: status }
}

function openDialog() {
  Object.assign(form, { ...defaultForm })
  categoryInput.value = ''
  dialogVisible.value = true
}

function addCategory() {
  const val = categoryInput.value.trim()
  if (val && !form.deliverableCategories.includes(val)) {
    form.deliverableCategories.push(val)
  }
  categoryInput.value = ''
}

function removeCategory(index: number) {
  form.deliverableCategories.splice(index, 1)
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await createDrone({ ...form, deliverableCategories: [...form.deliverableCategories] })
    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadRows()
  } finally {
    submitting.value = false
  }
}
</script>

<template>
  <div class="drones-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">无人机管理</h2>
        <p class="page-subtitle">管理无人机设备信息、状态监控与调度记录</p>
      </div>
      <el-button type="primary" :icon="Plus" @click="openDialog">添加无人机</el-button>
    </div>

    <el-card shadow="never" class="search-card">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="编码 / 型号 / 航班号"
            clearable
            style="width: 240px"
            :prefix-icon="Search"
            @keyup.enter="searchDrones"
          />
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
          <el-button type="primary" :icon="Search" @click="searchDrones">搜索</el-button>
          <el-button :icon="Refresh" @click="resetDrones">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="droneCode" label="编码" min-width="130">
          <template #default="{ row }">
            <span class="drone-code">{{ row.droneCode }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="model" label="型号" min-width="120" />
        <el-table-column prop="flightNo" label="航班号" min-width="120" />
        <el-table-column prop="maxPayloadKg" label="最大载重(kg)" min-width="120" />
        <el-table-column prop="maxVolumeM3" label="最大体积(m³)" min-width="120" />
        <el-table-column prop="maxRangeKm" label="航程(km)" min-width="100" />
        <el-table-column label="可配送品类" min-width="160">
          <template #default="{ row }">
            <div class="categories-cell">
              <el-tag
                v-for="cat in (row.deliverableCategories || []).slice(0, 3)"
                :key="cat"
                size="small"
                effect="plain"
                class="category-tag"
              >
                {{ cat }}
              </el-tag>
              <el-tag
                v-if="(row.deliverableCategories || []).length > 3"
                size="small"
                effect="plain"
                type="info"
              >
                +{{ row.deliverableCategories.length - 3 }}
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" min-width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status).type" size="small" effect="light">
              {{ statusTag(row.status).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="170" />
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

    <!-- Add Drone Dialog -->
    <el-dialog v-model="dialogVisible" title="添加无人机" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="110px">
        <el-form-item label="无人机编码" prop="droneCode">
          <el-input v-model="form.droneCode" placeholder="如 DRONE-001" />
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" placeholder="如 DJI M600" />
        </el-form-item>
        <el-form-item label="航班号" prop="flightNo">
          <el-input v-model="form.flightNo" placeholder="如 FL-20260001" />
        </el-form-item>
        <el-form-item label="最大载重(kg)" prop="maxPayloadKg">
          <el-input-number v-model="form.maxPayloadKg" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大体积(m³)" prop="maxVolumeM3">
          <el-input-number v-model="form.maxVolumeM3" :min="0" :precision="3" style="width: 100%" />
        </el-form-item>
        <el-form-item label="最大航程(km)" prop="maxRangeKm">
          <el-input-number v-model="form.maxRangeKm" :min="0" :precision="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="可配送品类">
          <div class="categories-input">
            <div class="categories-tags">
              <el-tag
                v-for="(cat, idx) in form.deliverableCategories"
                :key="cat"
                closable
                size="small"
                effect="plain"
                @close="removeCategory(idx)"
                class="category-tag"
              >
                {{ cat }}
              </el-tag>
            </div>
            <div class="categories-add">
              <el-input
                v-model="categoryInput"
                placeholder="输入品类名称"
                size="small"
                style="width: 180px"
                @keyup.enter="addCategory"
              />
              <el-button size="small" @click="addCategory">添加</el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option
              v-for="item in statusOptions"
              :key="item"
              :label="statusTag(item).label"
              :value="item"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.drones-page {
  padding: 0;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
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

.drone-code {
  font-family: 'SF Mono', monospace;
  font-size: 13px;
  color: #1677ff;
  font-weight: 500;
}

.categories-cell {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.category-tag {
  margin: 0;
}

.pagination-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.categories-input {
  width: 100%;
}

.categories-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}

.categories-add {
  display: flex;
  gap: 8px;
}
</style>
