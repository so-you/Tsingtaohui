import { describe, expect, it } from 'vitest'
import type { IScanHistoryItem } from './scanner'
import { addScanHistory, normalizeScanCode } from './scanner'

describe('scanner utilities', () => {
  it('normalizes keyboard-emulated scanner input', () => {
    expect(normalizeScanCode('  SKU-COKE-330\r\n')).toBe('SKU-COKE-330')
    expect(normalizeScanCode('\tPKG-QD-20260529-001\n')).toBe('PKG-QD-20260529-001')
  })

  it('keeps the latest five scan records first', () => {
    const history = ['A', 'B', 'C', 'D', 'E', 'F'].reduce<IScanHistoryItem[]>(
      (items, code) =>
        addScanHistory(items, {
          code,
          status: 'success',
          message: `scan ${code}`,
          scannedAt: `2026-05-29T10:00:0${code}.000Z`,
        }),
      [],
    )

    expect(history.map((item) => item.code)).toEqual(['F', 'E', 'D', 'C', 'B'])
  })
})
