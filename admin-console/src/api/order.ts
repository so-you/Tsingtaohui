import { get, patch } from '@/utils/request'
import type { IOrder, IPageParams, IPageResult, TOrderStatus, TTradeMode } from '@/types'

export function getOrders(params: IPageParams & { status?: TOrderStatus; trade_mode?: TTradeMode }) {
  return get<IPageResult<IOrder>>('/admin/orders', params as unknown as Record<string, unknown>)
}

export function getMatchingOrders(params: IPageParams & { status?: TOrderStatus }) {
  return get<IPageResult<IOrder>>('/admin/matching-orders', params as unknown as Record<string, unknown>)
}

export function updateOrderStatus(orderId: number, status: TOrderStatus) {
  return patch<IOrder>(`/admin/orders/${orderId}/status`, { status })
}
