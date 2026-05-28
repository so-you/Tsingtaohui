import { get, post, put } from '@/utils/request'
import type { IPageParams, IPageResult, IShip, IShippingAgent } from '@/types'

export function getShips(params: IPageParams & { nationality?: string }) {
  return get<IPageResult<IShip>>('/admin/ships', params as unknown as Record<string, unknown>)
}

export function createShip(data: Record<string, unknown>) {
  return post<IShip>('/admin/ships', data)
}

export function updateShip(shipId: number, data: Record<string, unknown>) {
  return put<IShip>(`/admin/ships/${shipId}`, data)
}

export function getShippingAgents(params: IPageParams & { status?: string }) {
  return get<IPageResult<IShippingAgent>>('/admin/shipping-agents', params as unknown as Record<string, unknown>)
}
