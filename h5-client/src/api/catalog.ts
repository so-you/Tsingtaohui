import { get } from '../utils/request'
import type { ICategory, IPageResult, IProduct } from '../types'

export function getCategories() {
  return get<ICategory[]>('/catalog/categories')
}

export function getProducts(params: {
  category_id?: number
  keyword?: string
  page?: number
  page_size?: number
}) {
  return get<IPageResult<IProduct>>('/catalog/products', { params })
}

export function getProductDetail(productId: number) {
  return get<IProduct>(`/catalog/products/${productId}`)
}
