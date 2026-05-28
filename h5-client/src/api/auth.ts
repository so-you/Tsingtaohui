import { post } from '../utils/request'

export function login(data: { username: string; password: string }) {
  return post('/auth/login', data)
}

export function register(data: { username: string; password: string; preferredLanguage: string }) {
  return post('/auth/register', data)
}
