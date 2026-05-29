import type { IScanResult, TScanResultStatus } from '../types'

export interface IScanHistoryItem {
  code: string
  status: TScanResultStatus
  message: string
  scannedAt: string
}

export function normalizeScanCode(value: string) {
  return value.replace(/[\r\n\t]/g, '').trim()
}

export function addScanHistory(
  history: IScanHistoryItem[],
  item: IScanHistoryItem,
  maxLength = 5,
) {
  return [item, ...history].slice(0, maxLength)
}

export function createScanHistoryItem(
  code: string,
  result: IScanResult,
  message: string,
): IScanHistoryItem {
  return {
    code,
    status: result.status,
    message,
    scannedAt: new Date().toISOString(),
  }
}

export function getScanFeedbackClass(status: TScanResultStatus) {
  return `scan-feedback--${status}`
}
