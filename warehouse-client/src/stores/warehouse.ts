import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type {
  IDashboardStats,
  IInventoryItem,
  IOutboundTask,
  IPickingTask,
  IReviewTask,
  IScanResult,
  IWarehouseUser,
} from '../types'
import * as warehouseApi from '../api/warehouse'

const USER_STORAGE_KEY = 'warehouse_user'

function readStoredUser() {
  try {
    const raw = localStorage.getItem(USER_STORAGE_KEY)
    if (!raw) return null
    return JSON.parse(raw) as { token: string; user: IWarehouseUser }
  } catch {
    return null
  }
}

export const useWarehouseStore = defineStore('warehouse', () => {
  const dashboardStats = ref<IDashboardStats>({
    pendingPicking: 0,
    pendingReview: 0,
    pendingOutbound: 0,
    exceptionOrders: 0,
  })
  const stored = readStoredUser()
  const token = ref(stored?.token || '')
  const user = ref<IWarehouseUser | null>(stored?.user || null)
  const pickingTasks = ref<IPickingTask[]>([])
  const reviewTasks = ref<IReviewTask[]>([])
  const outboundTasks = ref<IOutboundTask[]>([])
  const inventoryItems = ref<IInventoryItem[]>([])
  const loading = ref(false)
  const isLoggedIn = computed(() => Boolean(token.value && user.value))

  async function login(username: string, password: string) {
    const result = await warehouseApi.login(username, password)
    token.value = result.token
    user.value = result.user
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(result))
    return result
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem(USER_STORAGE_KEY)
  }

  async function fetchDashboard() {
    loading.value = true
    try {
      dashboardStats.value = await warehouseApi.getDashboard()
    } finally {
      loading.value = false
    }
  }

  async function fetchPickingTasks() {
    loading.value = true
    try {
      const res = await warehouseApi.getPickingTasks()
      pickingTasks.value = res.items
    } finally {
      loading.value = false
    }
  }

  async function scanPicking(skuCode: string): Promise<IScanResult<IPickingTask>> {
    const task = pickingTasks.value.find((item) =>
      item.skuCode === skuCode || item.items?.some((line) => line.skuCode === skuCode),
    ) || pickingTasks.value[0]
    const result = await warehouseApi.confirmPickingScan(task?.taskId || 0, skuCode)
    await fetchPickingTasks()
    await fetchDashboard()
    return result
  }

  async function fetchReviewTasks() {
    loading.value = true
    try {
      const res = await warehouseApi.getReviewTasks()
      reviewTasks.value = res.items
    } finally {
      loading.value = false
    }
  }

  async function scanReviewProduct(skuCode: string): Promise<IScanResult<IReviewTask>> {
    const task = reviewTasks.value.find((item) =>
      item.items.some((line) => line.skuCode === skuCode),
    ) || reviewTasks.value[0]
    const result = await warehouseApi.scanProductForReview(task?.taskId || 0, skuCode)
    await fetchReviewTasks()
    return result
  }

  async function packReviewOrder(taskId: number) {
    const result = await warehouseApi.packOrder(taskId)
    await fetchReviewTasks()
    await fetchDashboard()
    return result.packageNo
  }

  async function fetchOutboundTasks() {
    loading.value = true
    try {
      const res = await warehouseApi.getOutboundTasks()
      outboundTasks.value = res.items
    } finally {
      loading.value = false
    }
  }

  async function confirmOutboundTask(taskId: number) {
    await warehouseApi.confirmOutbound(taskId)
    await fetchOutboundTasks()
    await fetchDashboard()
  }

  async function fetchInventory(skuCode = '') {
    loading.value = true
    try {
      const res = await warehouseApi.getInventory(1, 20, skuCode)
      inventoryItems.value = res.items
    } finally {
      loading.value = false
    }
  }

  return {
    dashboardStats,
    token,
    user,
    pickingTasks,
    reviewTasks,
    outboundTasks,
    inventoryItems,
    loading,
    isLoggedIn,
    login,
    logout,
    fetchDashboard,
    fetchPickingTasks,
    scanPicking,
    fetchReviewTasks,
    scanReviewProduct,
    packReviewOrder,
    fetchOutboundTasks,
    confirmOutboundTask,
    fetchInventory,
  }
})
