import type { IInventoryItem } from '../types'
import { mockDelay, pageResult } from './helpers'

export const inventoryItems: IInventoryItem[] = [
  {
    id: 1,
    skuCode: 'SKU-COKE-330',
    productName: '可口可乐 330ml',
    productNameEn: 'Coca-Cola 330ml',
    location: 'A-01-03',
    batch: 'B20260501',
    availableQty: 128,
    lockedQty: 16,
    status: 'ENOUGH',
  },
  {
    id: 2,
    skuCode: 'SKU-WATER-550',
    productName: '矿泉水 550ml',
    productNameEn: 'Mineral Water 550ml',
    location: 'A-02-08',
    batch: 'B20260506',
    availableQty: 42,
    lockedQty: 8,
    status: 'ENOUGH',
  },
  {
    id: 3,
    skuCode: 'SKU-COFFEE-200',
    productName: '即饮咖啡 200ml',
    productNameEn: 'Ready-to-drink Coffee 200ml',
    location: 'B-04-02',
    batch: 'B20260420',
    availableQty: 7,
    lockedQty: 5,
    status: 'LOW',
  },
  {
    id: 4,
    skuCode: 'SKU-BISCUIT-120',
    productName: '黄油饼干 120g',
    productNameEn: 'Butter Biscuits 120g',
    location: 'C-02-11',
    batch: 'B20260319',
    availableQty: 0,
    lockedQty: 12,
    status: 'ZERO',
  },
  {
    id: 5,
    skuCode: 'SKU-TEA-500',
    productName: '瓶装绿茶 500ml',
    productNameEn: 'Green Tea 500ml',
    location: 'A-03-06',
    batch: 'B20260512',
    availableQty: 23,
    lockedQty: 4,
    status: 'ENOUGH',
  },
  {
    id: 6,
    skuCode: 'SKU-NOODLE-110',
    productName: '方便面 110g',
    productNameEn: 'Instant Noodles 110g',
    location: 'D-01-05',
    batch: 'B20260430',
    availableQty: 5,
    lockedQty: 9,
    status: 'LOW',
  },
]

export function filterInventoryBySku(items: IInventoryItem[], query: string) {
  const keyword = query.trim().toLowerCase()
  if (!keyword) return items
  return items.filter((item) => item.skuCode.toLowerCase().includes(keyword))
}

export function getMockInventory(page = 1, pageSize = 20, skuCode = '') {
  return mockDelay(pageResult(filterInventoryBySku(inventoryItems, skuCode), page, pageSize))
}
