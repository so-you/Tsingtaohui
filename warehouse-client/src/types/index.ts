export type TPriority = 'HIGH' | 'NORMAL'

export type TPickingStatus = 'PENDING_PICK' | 'PICKING' | 'PICKED'

export type TReviewStatus = 'PENDING_REVIEW' | 'REVIEWING' | 'PACKED'

export type TOutboundStatus = 'PENDING_OUTBOUND' | 'OUTBOUNDED' | 'BLOCKED'

export type TCustomsSyncStatus = 'SYNC_SUCCESS' | 'SYNC_FAILED' | 'SYNC_PENDING'

export type TDroneAssignStatus = 'DRONE_ASSIGNED' | 'WAITING_DRONE' | 'DRONE_BLOCKED'

export type TInventoryStatus = 'ENOUGH' | 'LOW' | 'ZERO'

export type TScanResultStatus = 'success' | 'failed' | 'duplicate' | 'overflow'

export interface IDashboardStats {
  pendingPicking: number
  pendingReview: number
  pendingOutbound: number
  exceptionOrders: number
}

export interface IBackendDashboardStats {
  pendingPick?: number
  pendingPicking?: number
  pendingReview?: number
  pendingOutbound?: number
  exceptionOrders?: number
}

export interface IWarehouseUser {
  username: string
  displayName: string
  role: string
  warehouseName: string
}

export interface ILoginResult {
  token: string
  user: IWarehouseUser
}

export interface IProductLine {
  skuCode: string
  productName: string
  productNameEn: string
  quantity: number
  scannedQty: number
  location?: string
  batch?: string
}

export interface IPickingTask {
  taskId: number
  orderNo: string
  skuCode: string
  productName: string
  productNameEn: string
  quantity: number
  pickedQty: number
  location: string
  batch: string
  priority: TPriority
  expectedDeliveryTime: string
  status: TPickingStatus
  scannedCodes: string[]
  items?: IProductLine[]
}

export interface IReviewTask {
  taskId: number
  orderNo: string
  scannedQty: number
  expectedQty: number
  packageNo: string
  priority: TPriority
  status: TReviewStatus
  items: IProductLine[]
}

export interface IOutboundTask {
  taskId: number
  orderNo: string
  packageNo: string
  weightKg: number
  volumeM3: number
  customsStatus: TCustomsSyncStatus
  droneStatus: TDroneAssignStatus
  status: TOutboundStatus
  customsBlocked: boolean
}

export interface IInventoryItem {
  id: number
  skuCode: string
  productName: string
  productNameEn: string
  location: string
  batch: string
  availableQty: number
  lockedQty: number
  status: TInventoryStatus
}

export interface IBackendInventoryItem {
  id?: number
  skuCode?: string
  productName?: string
  productNameZh?: string
  productNameEn?: string
  location?: string
  locationCode?: string
  batch?: string
  batchNo?: string
  availableQty?: number
  available?: number
  lockedQty?: number
  locked?: number
  status?: TInventoryStatus
}

export interface IPageResult<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
}

export interface IScanResult<TTask = unknown> {
  status: TScanResultStatus
  messageKey: string
  task?: TTask
}
