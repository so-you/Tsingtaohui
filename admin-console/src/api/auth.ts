import { post } from '@/utils/request'
import type { ILoginParams, ILoginResult } from '@/types'

export function login(params: ILoginParams) {
  return post<ILoginResult>('/auth/login', params as unknown as Record<string, unknown>)
}
