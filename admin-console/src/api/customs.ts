import { get, post } from '@/utils/request'
import type { ICustomsSyncRecord, IPageParams, IPageResult } from '@/types'

export function getCustomsSyncRecords(params: IPageParams) {
  return get<IPageResult<ICustomsSyncRecord>>('/admin/customs-sync-records', params as unknown as Record<string, unknown>)
}

export function retryCustomsSync(syncNo: string) {
  return post<ICustomsSyncRecord>(`/admin/customs-sync-records/${syncNo}/retry`)
}
