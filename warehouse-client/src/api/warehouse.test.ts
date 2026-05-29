import { describe, expect, it } from 'vitest'
import {
  buildPickingScanPath,
  buildReviewScanPath,
  LOGIN_PATH,
  normalizeDashboardStats,
  normalizeInventoryItem,
} from './warehouse'
import { isWarehouseMockEnabled } from '../mock'

describe('warehouse real-backend compatibility', () => {
  it('enables mock mode only when explicitly requested', () => {
    expect(isWarehouseMockEnabled(undefined)).toBe(false)
    expect(isWarehouseMockEnabled('')).toBe(false)
    expect(isWarehouseMockEnabled('false')).toBe(false)
    expect(isWarehouseMockEnabled('true')).toBe(true)
  })

  it('uses existing backend auth and task-scoped scan routes', () => {
    expect(LOGIN_PATH).toBe('/auth/login')
    expect(buildPickingScanPath(1001)).toBe('/warehouse/picking-tasks/1001/scan')
    expect(buildReviewScanPath(2001)).toBe('/warehouse/review-tasks/2001/scan-product')
  })

  it('normalizes dashboard stats from backend and mock key shapes', () => {
    expect(normalizeDashboardStats({ pendingPick: 7, pendingReview: 3, pendingOutbound: 2, exceptionOrders: 1 }))
      .toEqual({ pendingPicking: 7, pendingReview: 3, pendingOutbound: 2, exceptionOrders: 1 })
    expect(normalizeDashboardStats({ pendingPicking: 5, pendingReview: 0, pendingOutbound: 0, exceptionOrders: 0 }))
      .toEqual({ pendingPicking: 5, pendingReview: 0, pendingOutbound: 0, exceptionOrders: 0 })
  })

  it('normalizes real inventory records and derives status when backend omits it', () => {
    const item = normalizeInventoryItem({
      id: 9,
      skuCode: 'SKU-REAL-001',
      locationCode: 'R-01-02',
      batchNo: 'RB202605',
      availableQty: 0,
      lockedQty: 4,
    })

    expect(item.location).toBe('R-01-02')
    expect(item.batch).toBe('RB202605')
    expect(item.status).toBe('ZERO')
  })
})
