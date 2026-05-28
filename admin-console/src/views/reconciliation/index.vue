<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { exportReconciliation } from '@/api/reconciliation'
import { Download } from '@element-plus/icons-vue'

const { t } = useI18n()

const exporting = ref(false)
const form = reactive({
  startDate: '',
  endDate: ''
})

async function handleExport() {
  exporting.value = true
  try {
    const response = await exportReconciliation(form.startDate || undefined, form.endDate || undefined)
    const blob = response instanceof Blob ? response : new Blob([response as unknown as BlobPart])
    const url = URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `reconciliation_${new Date().toISOString().slice(0, 10)}.csv`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(url)
    ElMessage.success(t('common.success'))
  } catch {
    ElMessage.error(t('common.error'))
  } finally {
    exporting.value = false
  }
}
</script>

<template>
  <div class="reconciliation-page">
    <div class="page-header">
      <h2 class="page-title">{{ t('reconciliation.title') }}</h2>
      <p class="page-subtitle">{{ t('reconciliation.hint') }}</p>
    </div>

    <el-card shadow="never" class="export-card">
      <el-form :model="form" inline>
        <el-form-item :label="t('reconciliation.startDate')">
          <el-date-picker v-model="form.startDate" type="date" value-format="YYYY-MM-DD"
            :placeholder="t('reconciliation.startDate')" style="width: 180px" />
        </el-form-item>
        <el-form-item :label="t('reconciliation.endDate')">
          <el-date-picker v-model="form.endDate" type="date" value-format="YYYY-MM-DD"
            :placeholder="t('reconciliation.endDate')" style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Download" :loading="exporting" @click="handleExport">
            {{ t('reconciliation.exportBtn') }}
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<style scoped>
.reconciliation-page { padding: 0; }
.page-header { margin-bottom: 20px; }
.page-title { margin: 0 0 4px; font-size: 22px; font-weight: 600; color: #1a1a1a; }
.page-subtitle { margin: 0; font-size: 13px; color: #999; }
.export-card { border-radius: 8px; }
</style>
