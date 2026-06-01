import axios from 'axios'
import type { IApiResponse } from '@/types'
import { ElMessage } from 'element-plus'
import router from '@/router'
import i18n from '@/i18n'

const t = i18n.global.t

const request = axios.create({
  baseURL: '/api/v1',
  timeout: 15000
})

request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    const res = response.data as IApiResponse
    if (res.code !== '0' && res.code !== 0) {
      ElMessage.error(res.message || t('common.error'))
      return Promise.reject(new Error(res.message || t('common.error')))
    }
    return res.data as unknown as typeof response
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/admin/login')
      ElMessage.warning(t('login.tokenExpired'))
    } else {
      ElMessage.error(error.response?.data?.message || t('common.networkError'))
    }
    return Promise.reject(error)
  }
)

export function get<T = unknown>(url: string, params?: Record<string, unknown>): Promise<T> {
  return request.get(url, { params }) as Promise<T>
}

export function post<T = unknown>(url: string, data?: Record<string, unknown>): Promise<T> {
  return request.post(url, data) as Promise<T>
}

export function put<T = unknown>(url: string, data?: Record<string, unknown>): Promise<T> {
  return request.put(url, data) as Promise<T>
}

export function patch<T = unknown>(url: string, data?: Record<string, unknown>): Promise<T> {
  return request.patch(url, data) as Promise<T>
}

export function del<T = unknown>(url: string): Promise<T> {
  return request.delete(url) as Promise<T>
}

export default request
