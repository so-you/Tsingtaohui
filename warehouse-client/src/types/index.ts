export interface IDashboardStats {
  pendingPick: number
  pendingReview: number
  pendingOutbound: number
  exceptionOrders: number
}

export interface IPickingTask {
  taskId: number
  orderNo: string
  productName: string
  quantity: number
  location: string
  batch: string
}

export interface IReviewTask {
  taskId: number
  orderNo: string
  scannedQty: number
  expectedQty: number
}

export interface IOutboundTask {
  taskId: number
  orderNo: string
  packageNo: string
  customsBlocked: boolean
}

export interface IInventoryItem {
  id: number
  skuCode: string
  location: string
  batch: string
  available: number
  locked: number
}

export interface IPageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}
