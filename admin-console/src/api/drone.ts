import { get, post } from '@/utils/request'
import type { ICreateDroneParams, IDrone, IPageParams, IPageResult } from '@/types'

export function getDrones(params: IPageParams) {
  return get<IPageResult<IDrone>>('/admin/drones', params as unknown as Record<string, unknown>)
}

export function createDrone(data: ICreateDroneParams) {
  return post<IDrone>('/admin/drones', data as unknown as Record<string, unknown>)
}
