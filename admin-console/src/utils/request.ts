import axios from 'axios'
import type { IApiResponse } from '@/types'
import { ElMessage } from 'element-plus'
import router from '@/router'

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
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res.data as unknown as typeof response
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
      ElMessage.warning('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.response?.data?.message || '网络请求失败')
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

export function del<T = unknown>(url: string): Promise<T> {
  return request.delete(url) as Promise<T>
}

export default request
