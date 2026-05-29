import type { IOutboundTask } from '../types'
import { createMockError, mockDelay, pageResult } from './helpers'

const outboundTaskState: IOutboundTask[] = [
  {
    taskId: 3001,
    orderNo: 'TH202605290001',
    packageNo: 'PKG-QD-290001',
    weightKg: 3.8,
    volumeM3: 0.028,
    customsStatus: 'SYNC_SUCCESS',
    droneStatus: 'DRONE_ASSIGNED',
    status: 'PENDING_OUTBOUND',
    customsBlocked: false,
  },
  {
    taskId: 3002,
    orderNo: 'TH202605290005',
    packageNo: 'PKG-QD-290005',
    weightKg: 8.4,
    volumeM3: 0.076,
    customsStatus: 'SYNC_FAILED',
    droneStatus: 'DRONE_BLOCKED',
    status: 'BLOCKED',
    customsBlocked: true,
  },
]

export function getMockOutboundTasks(page = 1, pageSize = 20) {
  return mockDelay(pageResult(outboundTaskState, page, pageSize))
}

export function confirmMockOutbound(taskId: number) {
  const task = outboundTaskState.find((item) => item.taskId === taskId)
  if (!task) {
    throw createMockError('outbound.notFound', 'NOT_FOUND')
  }
  if (task.customsBlocked || task.customsStatus !== 'SYNC_SUCCESS') {
    throw createMockError('outbound.customsBlocked', 'CUSTOMS_BLOCKED')
  }

  task.status = 'OUTBOUNDED'
  return mockDelay({ deliveryTaskNo: `DRONE-${task.orderNo.slice(-6)}` })
}
