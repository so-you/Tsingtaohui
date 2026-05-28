import { get, patch } from '@/utils/request'
import type { IInventoryItem, IPageParams, IPageResult, IProductItem } from '@/types'

export function getProducts(params: IPageParams & { category_id?: number; status?: string }) {
  return get<IPageResult<IProductItem>>('/admin/products', params as unknown as Record<string, unknown>)
}

export function updateProductStatus(productId: number, status: string) {
  return patch<IProductItem>(`/admin/products/${productId}/status`, { status })
}

export function getInventory(params: IPageParams & { warehouse_id?: number }) {
  return get<IPageResult<IInventoryItem>>('/admin/inventory', params as unknown as Record<string, unknown>)
}
