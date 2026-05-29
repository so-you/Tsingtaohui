import type { IReviewTask, IScanResult } from '../types'
import { cloneData, createMockError, mockDelay, pageResult } from './helpers'

const initialReviewTasks: IReviewTask[] = [
  {
    taskId: 2001,
    orderNo: 'TH202605290001',
    expectedQty: 10,
    scannedQty: 6,
    packageNo: '',
    priority: 'HIGH',
    status: 'REVIEWING',
    items: [
      {
        skuCode: 'SKU-COKE-330',
        productName: '可口可乐 330ml',
        productNameEn: 'Coca-Cola 330ml',
        quantity: 6,
        scannedQty: 6,
        location: 'A-01-03',
        batch: 'B20260501',
      },
      {
        skuCode: 'SKU-BISCUIT-120',
        productName: '黄油饼干 120g',
        productNameEn: 'Butter Biscuits 120g',
        quantity: 4,
        scannedQty: 0,
        location: 'C-02-11',
        batch: 'B20260319',
      },
    ],
  },
  {
    taskId: 2002,
    orderNo: 'TH202605290004',
    expectedQty: 5,
    scannedQty: 5,
    packageNo: '',
    priority: 'NORMAL',
    status: 'PENDING_REVIEW',
    items: [
      {
        skuCode: 'SKU-WATER-550',
        productName: '矿泉水 550ml',
        productNameEn: 'Mineral Water 550ml',
        quantity: 5,
        scannedQty: 5,
        location: 'A-02-08',
        batch: 'B20260506',
      },
    ],
  },
]

let reviewTaskState = cloneData(initialReviewTasks)

export function resolveReviewScan(tasks: IReviewTask[], skuCode: string): IScanResult<IReviewTask> {
  const task = tasks.find((item) =>
    item.items.some((line) => line.skuCode === skuCode && line.scannedQty < line.quantity),
  )

  if (!task) {
    const duplicateTask = tasks.find((item) => item.items.some((line) => line.skuCode === skuCode))
    return duplicateTask
      ? { status: 'duplicate', messageKey: 'review.scanDuplicate', task: duplicateTask }
      : { status: 'failed', messageKey: 'review.scanMismatch' }
  }

  const line = task.items.find((item) => item.skuCode === skuCode && item.scannedQty < item.quantity)
  if (!line) {
    return { status: 'failed', messageKey: 'review.scanMismatch' }
  }

  line.scannedQty += 1
  task.scannedQty += 1
  task.status = task.scannedQty >= task.expectedQty ? 'PENDING_REVIEW' : 'REVIEWING'

  return { status: 'success', messageKey: 'review.scanSuccess', task }
}

export function getMockReviewTasks(page = 1, pageSize = 20) {
  return mockDelay(pageResult(reviewTaskState, page, pageSize))
}

export async function scanMockReviewProduct(skuCode: string) {
  const result = resolveReviewScan(reviewTaskState, skuCode)
  if (result.status !== 'success') {
    throw createMockError(result.messageKey, result.status)
  }
  return mockDelay(result)
}

export function packMockOrder(taskId: number) {
  const task = reviewTaskState.find((item) => item.taskId === taskId)
  if (!task || task.scannedQty < task.expectedQty) {
    throw createMockError('review.packBlocked', 'PACK_BLOCKED')
  }

  task.packageNo = task.packageNo || `PKG-QD-${task.orderNo.slice(-6)}`
  task.status = 'PACKED'
  return mockDelay({ packageNo: task.packageNo })
}
