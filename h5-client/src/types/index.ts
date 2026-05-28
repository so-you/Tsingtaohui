export interface IUserInfo {
  id: number
  username: string
  userType: string
  preferredLanguage: string
  displayName?: string
  contactPhone?: string
  email?: string
  shipNo?: string
  shipName?: string
  shipNationality?: string
  nationality?: string
  imo?: string
  mmsi?: string
  ships?: IShip[]
}

export interface ILoginParams {
  username: string
  password: string
}

export interface IRegisterParams {
  username: string
  password: string
  preferredLanguage: string
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

export interface IProduct {
  id: number
  skuCode: string
  nameZh: string
  nameEn?: string
  price: string
  weightKg?: string
  volumeM3?: string
  availableQty: number
  droneDeliverable: boolean
  mainImageUrl?: string
  categoryId?: number
  descriptionZh?: string
  descriptionEn?: string
  specification?: string
  source?: string
  status?: string
}

export interface ICategory {
  id: number
  parentId?: number | null
  nameZh: string
  nameEn: string
  sortOrder?: number
  children?: ICategory[]
}

export interface IPageResult<T> {
  items: T[]
  page: number
  pageSize: number
  total: number
}

export interface IShip {
  id?: number
  shipNo: string
  shipName?: string
  shipNationality: string
  imo?: string
  mmsi?: string
  isDefault?: boolean
}

export interface IOrder {
  id: number
  orderNo: string
  userId?: number
  orderStatus: TOrderStatus
  tradeMode: TTradeMode
  consigneeName: string
  cabinNo: string
  contactInfo?: string
  totalPrice: string
  totalWeightKg: string
  totalVolumeM3: string
  shipNo: string
  shipName?: string
  shipNationality: string
  imo?: string
  mmsi?: string
  shippingAgentName?: string
  remark?: string
  createdAt?: string
  items: IOrderItem[]
}

export interface IOrderItem {
  id?: number
  productId: number
  skuCode: string
  productNameZh: string
  productNameEn?: string
  quantity: number
  unitPrice: string
  unitWeightKg: string
  unitVolumeM3: string
  lineAmount: string
}

export interface ICartItem {
  productId: number
  skuCode: string
  nameZh: string
  nameEn?: string
  price: string
  quantity: number
  weightKg?: string
  volumeM3?: string
  mainImageUrl?: string
}

export interface IOrderRequestItem {
  productId: number
  quantity: number
}

export interface IOrderEstimate {
  totalPrice: string
  totalWeightKg: string
  totalVolumeM3: string
  tradeMode: TTradeMode
  canAutoTrade: boolean
  reasons: string[]
  items: IOrderItem[]
}

export interface ICreateOrderParams {
  items: IOrderRequestItem[]
  consigneeName: string
  cabinNo: string
  contactInfo?: string
  remark?: string
  shipNo?: string
  shipName?: string
  shipNationality?: string
  imo?: string
  mmsi?: string
}
