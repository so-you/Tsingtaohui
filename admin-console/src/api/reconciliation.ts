import request from '@/utils/request'

export function exportReconciliation(startDate?: string, endDate?: string) {
  return request.get('/admin/reconciliation/export', {
    params: { start_date: startDate, end_date: endDate },
    responseType: 'blob'
  })
}
