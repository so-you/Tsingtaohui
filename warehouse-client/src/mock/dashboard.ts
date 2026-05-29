import type { IDashboardStats } from '../types'
import { mockDelay } from './helpers'

export const dashboardStats: IDashboardStats = {
  pendingPicking: 12,
  pendingReview: 5,
  pendingOutbound: 3,
  exceptionOrders: 2,
}

export function getMockDashboard() {
  return mockDelay(dashboardStats)
}
