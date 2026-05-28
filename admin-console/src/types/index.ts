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
