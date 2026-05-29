import { describe, expect, it } from 'vitest'
import { clonePickingTasks, resolvePickingScan } from './picking'
import { filterInventoryBySku, inventoryItems } from './inventory'

describe('warehouse mock workflow', () => {
  it('filters inventory by SKU case-insensitively', () => {
    const results = filterInventoryBySku(inventoryItems, 'coke')

    expect(results).toHaveLength(1)
    expect(results[0].skuCode).toBe('SKU-COKE-330')
  })

  it('marks a picking task as picked after matching SKU scan', () => {
    const tasks = clonePickingTasks()
    const result = resolvePickingScan(tasks, 'SKU-COKE-330')

    expect(result.status).toBe('success')
    expect(result.task?.status).toBe('PICKED')
  })

  it('reports duplicate picking scans', () => {
    const tasks = clonePickingTasks()

    resolvePickingScan(tasks, 'SKU-COKE-330')
    const result = resolvePickingScan(tasks, 'SKU-COKE-330')

    expect(result.status).toBe('duplicate')
  })
})
