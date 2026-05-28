import { get } from '../utils/request'

export function getProfile() {
  return get('/users/me')
}
