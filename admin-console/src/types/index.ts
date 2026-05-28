export interface IApiResponse<T = unknown> {
  code: string | number
  message: string
  data: T
}

export interface ILoginParams {
  username: string
  password: string
}

export interface ILoginResult {
  token: string
  user?: IUserInfo
}

export interface IUserInfo {
  id: number
  username: string
  nickname?: string
  role?: string
  userType?: string
  status?: string
  preferredLanguage?: string
  lastLoginAt?: string
  createdAt?: string
  displayName?: string
  contactPhone?: string
  email?: string
  nationality?: string
  shipNo?: string
  shipName?: string
  shipNationality?: string
  imo?: string
  mmsi?: string
}

export interface IPageParams {
  page: number
  pageSize?: number
  page_size?: number
  keyword?: string
}

export interface IPageResult<T> {
  items: T[]
  total: number
  page: number
  pageSize: number
}

export interface IProductItem {
  id: number
  skuCode: string
  categoryId: number
  nameZh: string
  nameEn: string
  descriptionZh?: string
  descriptionEn?: string
  mainImageUrl?: string
  specification?: string
  price: string
  weightKg?: string
  volumeM3?: string
  source?: string
  droneDeliverable: boolean
  status: string
  availableQty: number
  lockedQty: number
  outboundQty: number
  createdAt?: string
}

export interface IInventoryItem {
  id: number
  warehouseId: number
  locationCode?: string
  skuCode: string
  productNameZh?: string
  productNameEn?: string
  batchNo?: string
  availableQty: number
  lockedQty: number
  outboundQty: number
  updatedAt?: string
}

export type TOrderStatus =
  | 'PENDING_CONFIRM'
  | 'CONFIRMED'
  | 'WAREHOUSE_PROCESSING'
  | 'PENDING_OUTBOUND'
  | 'OUTBOUND'
  | 'PENDING_LOADING'
  | 'IN_DELIVERY'
  | 'PENDING_RECEIPT'
  | 'COMPLETED'
  | 'CANCELLED'
  | 'EXCEPTION'

export type TTradeMode = 'AUTO_TRADE' | 'MATCHING_ORDER'

export interface IOrderItem {
  id?: number
  productId: number
  skuCode: string
  productNameZh: string
  productNameEn?: string
  unitPrice: string
  quantity: number
  unitWeightKg: string
  unitVolumeM3: string
  lineAmount: string
}

export interface IOrder {
  id: number
  orderNo: string
  userId: number
  totalPrice: string
  totalWeightKg: string
  totalVolumeM3: string
  tradeMode: TTradeMode
  orderStatus: TOrderStatus
  consigneeName: string
  cabinNo: string
  contactInfo?: string
  shipNo: string
  shipName?: string
  shipNationality: string
  imo?: string
  mmsi?: string
  shippingAgentName?: string
  remark?: string
  createdAt?: string
  completedAt?: string
  items?: IOrderItem[]
}
