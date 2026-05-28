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
  | 'PENDING_SUBMIT'
  | 'PENDING_CONFIRM'
  | 'CONFIRMED'
  | 'WAREHOUSE_PROCESSING'
  | 'PENDING_DISPATCH'
  | 'DISPATCHED'
  | 'PENDING_LOAD'
  | 'DELIVERING'
  | 'PENDING_RECEIVE'
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
  orderStatus: TOrderStatus
  tradeMode: TTradeMode
  consignee: string
  cabinNo: string
  totalAmount: number
  createdTime: string
  items: IOrderItem[]
}

export interface IOrderItem {
  productId: number
  productName: string
  quantity: number
  unitPrice: number
  weight: number
}
