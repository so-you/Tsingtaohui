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
import { USE_WAREHOUSE_MOCK } from '../mock'
import { getMockDashboard } from '../mock/dashboard'
import { getMockInventory } from '../mock/inventory'
import { confirmMockOutbound, getMockOutboundTasks } from '../mock/outbound'
import { confirmMockPickingScan, getMockPickingTasks } from '../mock/picking'
import { getMockReviewTasks, packMockOrder, scanMockReviewProduct } from '../mock/review'

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
  if (USE_WAREHOUSE_MOCK) {
    return Promise.resolve<ILoginResult>({
      token: `mock-token-${Date.now()}`,
      user: {
        username,
        displayName: username || 'warehouse.operator',
        role: password ? 'WAREHOUSE_OPERATOR' : 'WAREHOUSE_VIEWER',
        warehouseName: '青岛港保税仓 A 区',
      },
    })
  }

  return post<ILoginResult>(LOGIN_PATH, { username, password })
}

export function getDashboard() {
  if (USE_WAREHOUSE_MOCK) return getMockDashboard().then(normalizeDashboardStats)
  return get<IBackendDashboardStats>('/warehouse/dashboard').then(normalizeDashboardStats)
}

export function getPickingTasks(page = 1, pageSize = 20) {
  if (USE_WAREHOUSE_MOCK) return getMockPickingTasks(page, pageSize)
  return get<IPageResult<IPickingTask>>('/warehouse/picking-tasks', { params: { page, page_size: pageSize } })
}

export function confirmPickingScan(taskId: number, skuCode: string) {
  if (USE_WAREHOUSE_MOCK) return confirmMockPickingScan(skuCode)
  return post<IScanResult<IPickingTask>>(buildPickingScanPath(taskId), { skuCode })
}

export function getReviewTasks(page = 1, pageSize = 20) {
  if (USE_WAREHOUSE_MOCK) return getMockReviewTasks(page, pageSize)
  return get<IPageResult<IReviewTask>>('/warehouse/review-tasks', { params: { page, page_size: pageSize } })
}

export function scanProductForReview(taskId: number, skuCode: string) {
  if (USE_WAREHOUSE_MOCK) return scanMockReviewProduct(skuCode)
  return post<IScanResult<IReviewTask>>(buildReviewScanPath(taskId), { skuCode })
}

export function packOrder(taskId: number) {
  if (USE_WAREHOUSE_MOCK) return packMockOrder(taskId)
  return post<{ packageNo: string }>(`/warehouse/review-tasks/${taskId}/pack`)
}

export function getOutboundTasks(page = 1, pageSize = 20) {
  if (USE_WAREHOUSE_MOCK) return getMockOutboundTasks(page, pageSize)
  return get<IPageResult<IOutboundTask>>('/warehouse/outbound-tasks', { params: { page, page_size: pageSize } })
}

export function confirmOutbound(taskId: number) {
  if (USE_WAREHOUSE_MOCK) return confirmMockOutbound(taskId)
  return post<Record<string, unknown>>(`/warehouse/outbound-tasks/${taskId}/confirm`)
}

export function getInventory(page = 1, pageSize = 20, skuCode = '') {
  if (USE_WAREHOUSE_MOCK) return getMockInventory(page, pageSize, skuCode)
  return get<IPageResult<IBackendInventoryItem>>('/warehouse/inventory', { params: { page, page_size: pageSize } })
    .then((result) => normalizePageResult(result, normalizeInventoryItem))
}
