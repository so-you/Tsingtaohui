import { get, post } from '../utils/request'
import type { ICreateOrderParams, IOrder, IOrderEstimate, IOrderRequestItem, IPageResult, TOrderStatus } from '../types'

export function estimateCart(items: IOrderRequestItem[]) {
  return post<IOrderEstimate>('/cart/estimate', { items })
}

export function createOrder(params: ICreateOrderParams) {
  return post<IOrder>('/orders', params)
}

export function getOrders(params: { status?: TOrderStatus; page?: number; page_size?: number }) {
  return get<IPageResult<IOrder>>('/orders', { params })
}

export function getOrderDetail(orderId: number) {
  return get<IOrder>(`/orders/${orderId}`)
}
