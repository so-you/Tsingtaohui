import { get, post } from '../utils/request'
import type { IDashboardStats, IInventoryItem, IOutboundTask, IPageResult, IPickingTask, IReviewTask } from '../types'

export function getDashboard() {
  return get<IDashboardStats>('/warehouse/dashboard')
}

export function getPickingTasks(page = 1, pageSize = 20) {
  return get<IPageResult<IPickingTask>>('/warehouse/picking-tasks', { params: { page, page_size: pageSize } })
}

export function confirmPickingScan(taskId: number, skuCode: string) {
  return post<void>(`/warehouse/picking-tasks/${taskId}/scan`, { skuCode })
}

export function getReviewTasks(page = 1, pageSize = 20) {
  return get<IPageResult<IReviewTask>>('/warehouse/review-tasks', { params: { page, page_size: pageSize } })
}

export function scanProductForReview(taskId: number, skuCode: string) {
  return post<void>(`/warehouse/review-tasks/${taskId}/scan-product`, { skuCode })
}

export function packOrder(taskId: number) {
  return post<{ packageNo: string }>(`/warehouse/review-tasks/${taskId}/pack`)
}

export function getOutboundTasks(page = 1, pageSize = 20) {
  return get<IPageResult<IOutboundTask>>('/warehouse/outbound-tasks', { params: { page, page_size: pageSize } })
}

export function confirmOutbound(taskId: number) {
  return post<Record<string, unknown>>(`/warehouse/outbound-tasks/${taskId}/confirm`)
}

export function getInventory(page = 1, pageSize = 20) {
  return get<IPageResult<IInventoryItem>>('/warehouse/inventory', { params: { page, page_size: pageSize } })
}
