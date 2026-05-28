import { get } from '@/utils/request'
import type { IPageParams, IPageResult, IAuditLog } from '@/types'

export function getAuditLogs(params: IPageParams & {
  module?: string
  actor_id?: number
  target_type?: string
  target_id?: string
  start_time?: string
  end_time?: string
}) {
  return get<IPageResult<IAuditLog>>('/admin/audit-logs', params as unknown as Record<string, unknown>)
}
