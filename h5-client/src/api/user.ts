import { get, put } from '../utils/request'
import type { IShip } from '../types'

export function getProfile() {
  return get('/users/me')
}

export function updateShip(data: Omit<IShip, 'id' | 'isDefault'>) {
  return put<IShip>('/users/me/ship', data)
}
