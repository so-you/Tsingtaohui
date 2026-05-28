export interface IApiResponse<T = unknown> {
  code: number
  message: string
  data: T
}

export interface ILoginParams {
  username: string
  password: string
}

export interface ILoginResult {
  token: string
}

export interface IUserInfo {
  id: number
  username: string
  nickname: string
  role: string
}

export interface IPageParams {
  page: number
  pageSize: number
  keyword?: string
}

export interface IPageResult<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}
