import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { IDashboardStats, IInventoryItem, IOutboundTask, IPickingTask, IReviewTask } from '../types'
import * as warehouseApi from '../api/warehouse'

export const useWarehouseStore = defineStore('warehouse', () => {
  const dashboardStats = ref<IDashboardStats>({
    pendingPick: 0,
    pendingReview: 0,
    pendingOutbound: 0,
    exceptionOrders: 0,
  })
  const pickingTasks = ref<IPickingTask[]>([])
  const reviewTasks = ref<IReviewTask[]>([])
  const outboundTasks = ref<IOutboundTask[]>([])
  const inventoryItems = ref<IInventoryItem[]>([])
  const loading = ref(false)

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
      pickingTasks.value = res.list
    } finally {
      loading.value = false
    }
  }

  async function scanPicking(taskId: number, skuCode: string) {
    await warehouseApi.confirmPickingScan(taskId, skuCode)
    await fetchPickingTasks()
    await fetchDashboard()
  }

  async function fetchReviewTasks() {
    loading.value = true
    try {
      const res = await warehouseApi.getReviewTasks()
      reviewTasks.value = res.list
    } finally {
      loading.value = false
    }
  }

  async function scanReviewProduct(taskId: number, skuCode: string) {
    await warehouseApi.scanProductForReview(taskId, skuCode)
    await fetchReviewTasks()
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
      outboundTasks.value = res.list
    } finally {
      loading.value = false
    }
  }

  async function confirmOutboundTask(taskId: number) {
    await warehouseApi.confirmOutbound(taskId)
    await fetchOutboundTasks()
    await fetchDashboard()
  }

  async function fetchInventory() {
    loading.value = true
    try {
      const res = await warehouseApi.getInventory()
      inventoryItems.value = res.list
    } finally {
      loading.value = false
    }
  }

  return {
    dashboardStats,
    pickingTasks,
    reviewTasks,
    outboundTasks,
    inventoryItems,
    loading,
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
