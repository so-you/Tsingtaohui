import { get, post } from '../utils/request'
import type {
  IDashboardStats,
  IBackendDashboardStats,
  IBackendInventoryItem,
  IInventoryItem,
  ILoginResult,
  IOutboundTask,
  IPageResult,
  IPickingTask,
  IReviewTask,
  IScanResult,
} from '../types'

export const LOGIN_PATH = '/auth/login'

export function buildPickingScanPath(taskId: number) {
  return `/warehouse/picking-tasks/${taskId}/scan`
}

export function buildReviewScanPath(taskId: number) {
  return `/warehouse/review-tasks/${taskId}/scan-product`
}

function toNumber(value: unknown, fallback = 0) {
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : fallback
}

function normalizePageResult<TInput, TOutput>(
  result: IPageResult<TInput> | { list?: TInput[]; total?: number; page?: number; pageSize?: number },
  normalizeItem: (item: TInput) => TOutput,
): IPageResult<TOutput> {
  const items = 'items' in result ? result.items : result.list
  return {
    items: (items || []).map(normalizeItem),
    total: toNumber(result.total, 0),
    page: toNumber(result.page, 1),
    pageSize: toNumber(result.pageSize, 20),
  }
}

export function normalizeDashboardStats(stats: IBackendDashboardStats): IDashboardStats {
  return {
    pendingPicking: toNumber(stats.pendingPicking ?? stats.pendingPick, 0),
    pendingReview: toNumber(stats.pendingReview, 0),
    pendingOutbound: toNumber(stats.pendingOutbound, 0),
    exceptionOrders: toNumber(stats.exceptionOrders, 0),
  }
}

function deriveInventoryStatus(availableQty: number) {
  if (availableQty <= 0) return 'ZERO'
  if (availableQty <= 10) return 'LOW'
  return 'ENOUGH'
}

export function normalizeInventoryItem(item: IBackendInventoryItem): IInventoryItem {
  const availableQty = toNumber(item.availableQty ?? item.available, 0)
  return {
    id: toNumber(item.id, 0),
    skuCode: item.skuCode || '',
    productName: item.productName || item.productNameZh || item.skuCode || '',
    productNameEn: item.productNameEn || item.productName || item.productNameZh || item.skuCode || '',
    location: item.location || item.locationCode || '',
    batch: item.batch || item.batchNo || '',
    availableQty,
    lockedQty: toNumber(item.lockedQty ?? item.locked, 0),
    status: item.status || deriveInventoryStatus(availableQty),
  }
}

export function login(username: string, password: string) {
  return post<ILoginResult>(LOGIN_PATH, { username, password })
}

export function getDashboard() {
  return get<IBackendDashboardStats>('/warehouse/dashboard').then(normalizeDashboardStats)
}

export function getPickingTasks(page = 1, pageSize = 20) {
  return get<IPageResult<IPickingTask>>('/warehouse/picking-tasks', { params: { page, page_size: pageSize } })
}

export function confirmPickingScan(taskId: number, skuCode: string) {
  return post<IScanResult<IPickingTask>>(buildPickingScanPath(taskId), { skuCode })
}

export function getReviewTasks(page = 1, pageSize = 20) {
  return get<IPageResult<IReviewTask>>('/warehouse/review-tasks', { params: { page, page_size: pageSize } })
}

export function scanProductForReview(taskId: number, skuCode: string) {
  return post<IScanResult<IReviewTask>>(buildReviewScanPath(taskId), { skuCode })
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

export function getInventory(page = 1, pageSize = 20, skuCode = '') {
  return get<IPageResult<IBackendInventoryItem>>('/warehouse/inventory', { params: { page, page_size: pageSize } })
    .then((result) => normalizePageResult(result, normalizeInventoryItem))
}
