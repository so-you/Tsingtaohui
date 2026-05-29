import type { IPageResult } from '../types'

export function cloneData<T>(data: T): T {
  return JSON.parse(JSON.stringify(data)) as T
}

export function pageResult<T>(items: T[], page = 1, pageSize = 20): IPageResult<T> {
  return {
    items: items.slice((page - 1) * pageSize, page * pageSize),
    total: items.length,
    page,
    pageSize,
  }
}

export function mockDelay<T>(value: T, delay = 120): Promise<T> {
  return new Promise((resolve) => {
    setTimeout(() => resolve(cloneData(value)), delay)
  })
}

export function createMockError(message: string, code: string) {
  const error = new Error(message)
  ;(error as Error & { code: string }).code = code
  return error
}
