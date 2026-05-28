import { get, put } from '@/utils/request'
import type { IPageParams, IPageResult, IRuleConfig } from '@/types'

export function getRules(params: IPageParams & { status?: string }) {
  return get<IPageResult<IRuleConfig>>('/admin/rules', params as unknown as Record<string, unknown>)
}

export function updateRule(ruleId: number, data: Partial<IRuleConfig>) {
  return put<IRuleConfig>(`/admin/rules/${ruleId}`, data as unknown as Record<string, unknown>)
}
