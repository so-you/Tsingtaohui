export function isWarehouseMockEnabled(value: unknown) {
  return value === 'true'
}

export const USE_WAREHOUSE_MOCK = isWarehouseMockEnabled(import.meta.env.VITE_USE_MOCK)

export function setupMock() {
  return USE_WAREHOUSE_MOCK
}
