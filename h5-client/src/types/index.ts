export interface IUserInfo {
  id: number
  username: string
  userType: string
  preferredLanguage: string
  shipNo?: string
  nationality?: string
  imo?: string
  mmsi?: string
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
  sku: string
  name: string
  nameEn?: string
  price: number
  weight: number
  volume: number
  stock: number
  droneDeliverable: boolean
  imageUrl?: string
  categoryId?: number
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
