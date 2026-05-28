import { get } from '@/utils/request'
import type { IPageParams, IPageResult, IUserInfo } from '@/types'

export function getUsers(params: IPageParams) {
  return get<IPageResult<IUserInfo>>('/admin/users', params as unknown as Record<string, unknown>)
}
