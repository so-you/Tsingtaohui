<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useResponsive } from '@/composables/useResponsive'
import { ElMessage } from 'element-plus'
import { getInventory, getProducts, updateProduct, updateProductStatus } from '@/api/product'
import type { IInventoryItem, IProductItem } from '@/types'
import { Search, Refresh, Picture } from '@element-plus/icons-vue'

const { t, locale } = useI18n()
const { isMobile } = useResponsive()

const activeTab = ref('products')
const productLoading = ref(false)
const inventoryLoading = ref(false)
const productRows = ref<IProductItem[]>([])
const inventoryRows = ref<IInventoryItem[]>([])
const productTotal = ref(0)
const inventoryTotal = ref(0)
const productDialogVisible = ref(false)
const productSaving = ref(false)
const editingProductId = ref<number | null>(null)

const productQuery = reactive({
  keyword: '',
  status: '',
  page: 1,
  page_size: 10
})

const inventoryQuery = reactive({
  keyword: '',
  warehouseId: undefined as number | undefined,
  page: 1,
  page_size: 10
})

const productStatusOptions = ['ON_SALE', 'OFF_SALE']
const productForm = reactive({
  categoryId: undefined as number | undefined,
  nameZh: '',
  nameEn: '',
  descriptionZh: '',
  descriptionEn: '',
  mainImageUrl: '',
  specification: '',
  price: '',
  weightKg: '',
  volumeM3: '',
  source: '',
  droneDeliverable: true,
  status: 'ON_SALE'
})

const currentProductRows = computed(() => productRows.value)
const currentInventoryRows = computed(() => inventoryRows.value)

onMounted(() => {
  loadProducts()
  loadInventory()
})

async function loadProducts() {
  productLoading.value = true
  try {
    const res = await getProducts({
      keyword: productQuery.keyword || undefined,
      status: productQuery.status || undefined,
      page: productQuery.page,
      page_size: productQuery.page_size
    })
    productRows.value = res.items
    productTotal.value = res.total
  } finally {
    productLoading.value = false
  }
}

async function loadInventory() {
  inventoryLoading.value = true
  try {
    const res = await getInventory({
      keyword: inventoryQuery.keyword || undefined,
      warehouse_id: inventoryQuery.warehouseId,
      page: inventoryQuery.page,
      page_size: inventoryQuery.page_size
    })
    inventoryRows.value = res.items
    inventoryTotal.value = res.total
  } finally {
    inventoryLoading.value = false
  }
}

function productName(row: IProductItem | IInventoryItem) {
  if ('nameZh' in row) {
    return locale.value === 'en-US' ? row.nameEn || row.nameZh : row.nameZh
  }
  return locale.value === 'en-US'
    ? row.productNameEn || row.productNameZh || '-'
    : row.productNameZh || row.productNameEn || '-'
}

function searchProducts() {
  productQuery.page = 1
  loadProducts()
}

function resetProducts() {
  productQuery.keyword = ''
  productQuery.status = ''
  productQuery.page = 1
  loadProducts()
}

function searchInventory() {
  inventoryQuery.page = 1
  loadInventory()
}

function resetInventory() {
  inventoryQuery.keyword = ''
  inventoryQuery.warehouseId = undefined
  inventoryQuery.page = 1
  loadInventory()
}

async function handleProductStatus(row: IProductItem, status: string) {
  if (row.status === status) return
  await updateProductStatus(row.id, status)
  ElMessage.success(t('common.success'))
  loadProducts()
}

function openProductEdit(row: IProductItem) {
  editingProductId.value = row.id
  productForm.categoryId = row.categoryId
  productForm.nameZh = row.nameZh
  productForm.nameEn = row.nameEn
  productForm.descriptionZh = row.descriptionZh || ''
  productForm.descriptionEn = row.descriptionEn || ''
  productForm.mainImageUrl = row.mainImageUrl || ''
  productForm.specification = row.specification || ''
  productForm.price = row.price
  productForm.weightKg = row.weightKg || ''
  productForm.volumeM3 = row.volumeM3 || ''
  productForm.source = row.source || ''
  productForm.droneDeliverable = row.droneDeliverable
  productForm.status = row.status
  productDialogVisible.value = true
}

async function saveProduct() {
  if (!editingProductId.value) return
  productSaving.value = true
  try {
    await updateProduct(editingProductId.value, {
      categoryId: productForm.categoryId,
      nameZh: productForm.nameZh,
      nameEn: productForm.nameEn,
      descriptionZh: productForm.descriptionZh,
      descriptionEn: productForm.descriptionEn,
      mainImageUrl: productForm.mainImageUrl,
      specification: productForm.specification,
      price: productForm.price,
      weightKg: productForm.weightKg,
      volumeM3: productForm.volumeM3,
      source: productForm.source,
      droneDeliverable: productForm.droneDeliverable,
      status: productForm.status
    })
    ElMessage.success(t('common.success'))
    productDialogVisible.value = false
    loadProducts()
  } finally {
    productSaving.value = false
  }
}

function productStatusTag(status?: string) {
  return status === 'ON_SALE' ? 'success' : 'info'
}
</script>

<template>
  <div class="products-page">
    <!-- Page Header -->
    <div class="page-header">
      <div>
        <h2 class="page-title">{{ t('menu.products') }}</h2>
        <p class="page-subtitle">{{ t('product.pageSubtitle') }}</p>
      </div>
    </div>

    <el-tabs v-model="activeTab" class="product-tabs" type="border-card">
      <el-tab-pane :label="t('product.productsTab')" name="products">
        <el-card shadow="never" class="search-card">
          <el-form :model="productQuery" inline>
            <el-form-item :label="t('product.keyword')">
              <el-input
                v-model="productQuery.keyword"
                :placeholder="t('product.keywordPlaceholder')"
                clearable
                style="width: 240px"
                :prefix-icon="Search"
                @keyup.enter="searchProducts"
              />
            </el-form-item>
            <el-form-item :label="t('product.status')">
              <el-select v-model="productQuery.status" clearable style="width: 150px">
                <el-option
                  v-for="item in productStatusOptions"
                  :key="item"
                  :label="t(`product.${item.toLowerCase()}`)"
                  :value="item"
                />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="searchProducts">{{ t('common.search') }}</el-button>
              <el-button :icon="Refresh" @click="resetProducts">{{ t('common.reset') }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never">
          <el-table v-loading="productLoading" :data="currentProductRows" stripe>
            <el-table-column :label="t('product.name')" min-width="200">
              <template #default="{ row }">
                <div class="product-cell">
                  <div class="product-thumb">
                    <el-image
                      v-if="row.mainImageUrl"
                      :src="row.mainImageUrl"
                      fit="cover"
                      style="width: 48px; height: 48px; border-radius: 6px;"
                    />
                    <div v-else class="thumb-placeholder">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </div>
                  <div class="product-info">
                    <div class="product-name">{{ productName(row) }}</div>
                    <div class="product-sku">{{ row.skuCode }}</div>
                  </div>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="price" :label="t('product.price')" min-width="110">
              <template #default="{ row }">
                <span class="price">¥{{ row.price }}</span>
              </template>
            </el-table-column>
            <el-table-column :label="t('product.weight')" min-width="110">
              <template #default="{ row }">{{ row.weightKg || '-' }} kg</template>
            </el-table-column>
            <el-table-column :label="t('product.volume')" min-width="110">
              <template #default="{ row }">{{ row.volumeM3 || '-' }} m³</template>
            </el-table-column>
            <el-table-column prop="availableQty" :label="t('inventory.availableQty')" min-width="110" />
            <el-table-column prop="lockedQty" :label="t('inventory.lockedQty')" min-width="110" />
            <el-table-column :label="t('product.droneDeliverable')" min-width="130">
              <template #default="{ row }">
                <el-tag :type="row.droneDeliverable ? 'success' : 'info'" size="small" effect="light">
                  {{ row.droneDeliverable ? t('common.yes') : t('common.no') }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" :label="t('product.status')" min-width="120">
              <template #default="{ row }">
                <el-tag :type="productStatusTag(row.status)" size="small" effect="light">
                  {{ t(`product.${row.status.toLowerCase()}`) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column :label="t('user.actions')" fixed="right" width="210">
              <template #default="{ row }">
                <el-button type="primary" link size="small" @click="openProductEdit(row)">
                  {{ t('common.edit') }}
                </el-button>
                <el-button
                  v-if="row.status !== 'ON_SALE'"
                  type="success"
                  link
                  size="small"
                  @click="handleProductStatus(row, 'ON_SALE')"
                >
                  {{ t('product.onSaleAction') }}
                </el-button>
                <el-button
                  v-if="row.status === 'ON_SALE'"
                  type="warning"
                  link
                  size="small"
                  @click="handleProductStatus(row, 'OFF_SALE')"
                >
                  {{ t('product.offSaleAction') }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>

          <div class="pagination-row">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :page-size="productQuery.page_size"
              :current-page="productQuery.page"
              :total="productTotal"
              @current-change="(page: number) => { productQuery.page = page; loadProducts() }"
            />
          </div>
        </el-card>
      </el-tab-pane>

      <el-tab-pane :label="t('inventory.title')" name="inventory">
        <el-card shadow="never" class="search-card">
          <el-form :model="inventoryQuery" inline>
            <el-form-item :label="t('product.skuCode')">
              <el-input
                v-model="inventoryQuery.keyword"
                :placeholder="t('product.skuCode')"
                clearable
                style="width: 220px"
                :prefix-icon="Search"
                @keyup.enter="searchInventory"
              />
            </el-form-item>
            <el-form-item :label="t('inventory.warehouseId')">
              <el-input-number
                v-model="inventoryQuery.warehouseId"
                :min="1"
                controls-position="right"
                style="width: 160px"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="searchInventory">{{ t('common.search') }}</el-button>
              <el-button :icon="Refresh" @click="resetInventory">{{ t('common.reset') }}</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never">
          <el-table v-loading="inventoryLoading" :data="currentInventoryRows" stripe>
            <el-table-column prop="warehouseId" :label="t('inventory.warehouseId')" min-width="120" />
            <el-table-column prop="locationCode" :label="t('inventory.locationCode')" min-width="140" />
            <el-table-column prop="skuCode" :label="t('product.skuCode')" min-width="150" />
            <el-table-column :label="t('product.name')" min-width="180">
              <template #default="{ row }">{{ productName(row) }}</template>
            </el-table-column>
            <el-table-column prop="batchNo" :label="t('inventory.batchNo')" min-width="140" />
            <el-table-column prop="availableQty" :label="t('inventory.availableQty')" min-width="110">
              <template #default="{ row }">
                <span class="qty-available">{{ row.availableQty }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="lockedQty" :label="t('inventory.lockedQty')" min-width="110">
              <template #default="{ row }">
                <span class="qty-locked">{{ row.lockedQty }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="outboundQty" :label="t('inventory.outboundQty')" min-width="110" />
            <el-table-column prop="updatedAt" :label="t('common.updatedAt')" min-width="170" />
          </el-table>

          <div class="pagination-row">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :page-size="inventoryQuery.page_size"
              :current-page="inventoryQuery.page"
              :total="inventoryTotal"
              @current-change="(page: number) => { inventoryQuery.page = page; loadInventory() }"
            />
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="productDialogVisible" :title="t('product.editTitle')" :fullscreen="isMobile" destroy-on-close>
      <el-form :model="productForm" label-width="130px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="t('product.categoryId')">
              <el-input-number v-model="productForm.categoryId" :min="1" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('product.status')">
              <el-select v-model="productForm.status" style="width: 100%">
                <el-option
                  v-for="item in productStatusOptions"
                  :key="item"
                  :label="t(`product.${item.toLowerCase()}`)"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="t('product.nameZh')">
              <el-input v-model="productForm.nameZh" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('product.nameEn')">
              <el-input v-model="productForm.nameEn" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item :label="t('product.price')">
              <el-input v-model="productForm.price" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="t('product.weight')">
              <el-input v-model="productForm.weightKg" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item :label="t('product.volume')">
              <el-input v-model="productForm.volumeM3" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="t('product.specification')">
              <el-input v-model="productForm.specification" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('product.source')">
              <el-input v-model="productForm.source" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="t('product.mainImageUrl')">
          <el-input v-model="productForm.mainImageUrl" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item :label="t('product.descriptionZh')">
              <el-input v-model="productForm.descriptionZh" type="textarea" :rows="4" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item :label="t('product.descriptionEn')">
              <el-input v-model="productForm.descriptionEn" type="textarea" :rows="4" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item :label="t('product.droneDeliverable')">
          <el-switch v-model="productForm.droneDeliverable" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="productSaving" @click="saveProduct">{{ t('common.save') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.products-page {
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

.product-tabs {
  background: transparent;
}

.product-tabs :deep(.el-tabs__header) {
  margin-bottom: 16px;
}

.search-card {
  margin-bottom: 16px;
  border-radius: 8px;
}

.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}

.product-thumb {
  flex-shrink: 0;
}

.thumb-placeholder {
  width: 48px;
  height: 48px;
  background: #f5f5f5;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
}

.product-info {
  display: flex;
  flex-direction: column;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 2px;
}

.product-sku {
  font-size: 12px;
  color: #999;
  font-family: 'SF Mono', monospace;
}

.price {
  font-weight: 600;
  color: #f5222d;
}

.qty-available {
  color: #52c41a;
  font-weight: 600;
}

.qty-locked {
  color: #fa8c16;
  font-weight: 600;
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
