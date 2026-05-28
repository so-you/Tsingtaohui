import { get, patch } from '@/utils/request'
import type { IPageParams, IPageResult, IUserInfo } from '@/types'

export function getProfile() {
  return get<IUserInfo>('/admin/profile')
}

export function getUsers(params: IPageParams & { user_type?: string; status?: string }) {
  return get<IPageResult<IUserInfo>>('/admin/users', params as unknown as Record<string, unknown>)
}

export function updateUserStatus(userId: number, status: string) {
  return patch<IUserInfo>(`/admin/users/${userId}/status`, { status })
}
