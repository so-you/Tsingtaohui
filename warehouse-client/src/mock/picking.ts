import type { IPickingTask, IScanResult } from '../types'
import { cloneData, createMockError, mockDelay, pageResult } from './helpers'

const initialPickingTasks: IPickingTask[] = [
  {
    taskId: 1001,
    orderNo: 'TH202605290001',
    skuCode: 'SKU-COKE-330',
    productName: '可口可乐 330ml',
    productNameEn: 'Coca-Cola 330ml',
    quantity: 6,
    pickedQty: 0,
    location: 'A-01-03',
    batch: 'B20260501',
    priority: 'HIGH',
    expectedDeliveryTime: '10:30',
    status: 'PENDING_PICK',
    scannedCodes: [],
  },
  {
    taskId: 1002,
    orderNo: 'TH202605290002',
    skuCode: 'SKU-COFFEE-200',
    productName: '即饮咖啡 200ml',
    productNameEn: 'Ready-to-drink Coffee 200ml',
    quantity: 4,
    pickedQty: 0,
    location: 'B-04-02',
    batch: 'B20260420',
    priority: 'NORMAL',
    expectedDeliveryTime: '11:15',
    status: 'PENDING_PICK',
    scannedCodes: [],
  },
  {
    taskId: 1003,
    orderNo: 'TH202605290003',
    skuCode: 'SKU-NOODLE-110',
    productName: '方便面 110g',
    productNameEn: 'Instant Noodles 110g',
    quantity: 8,
    pickedQty: 2,
    location: 'D-01-05',
    batch: 'B20260430',
    priority: 'HIGH',
    expectedDeliveryTime: '12:00',
    status: 'PICKING',
    scannedCodes: [],
  },
]

let pickingTaskState = cloneData(initialPickingTasks)

export function clonePickingTasks() {
  return cloneData(initialPickingTasks)
}

export function resolvePickingScan(tasks: IPickingTask[], skuCode: string): IScanResult<IPickingTask> {
  const task = tasks.find((item) => item.skuCode === skuCode)
  if (!task) {
    return { status: 'failed', messageKey: 'picking.scanMismatch' }
  }

  if (task.scannedCodes.includes(skuCode) || task.status === 'PICKED') {
    return { status: 'duplicate', messageKey: 'picking.scanDuplicate', task }
  }

  if (task.pickedQty >= task.quantity) {
    return { status: 'overflow', messageKey: 'picking.scanOverflow', task }
  }

  task.pickedQty = task.quantity
  task.status = 'PICKED'
  task.scannedCodes.push(skuCode)

  return { status: 'success', messageKey: 'picking.scanSuccess', task }
}

export function getMockPickingTasks(page = 1, pageSize = 20) {
  return mockDelay(pageResult(pickingTaskState, page, pageSize))
}

export async function confirmMockPickingScan(skuCode: string) {
  const result = resolvePickingScan(pickingTaskState, skuCode)
  if (result.status !== 'success') {
    throw createMockError(result.messageKey, result.status)
  }
  return mockDelay(result)
}
