import axios from 'axios'
import type { AxiosRequestConfig, AxiosResponse } from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

const http = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor: attach Bearer token
http.interceptors.request.use(
  (config) => {
    try {
      const raw = localStorage.getItem('user')
      if (raw) {
        const store = JSON.parse(raw)
        const token = store?.token
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
      }
    } catch {
      // ignore parse errors
    }
    return config
  },
  (error) => Promise.reject(error),
)

// Response interceptor: unwrap ApiResponse, handle 401
http.interceptors.response.use(
  (response: AxiosResponse) => {
    const body = response.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === '0') {
        return body.data
      } else {
        const err = new Error(body.message || 'Request failed')
        ;(err as any).code = body.code
        return Promise.reject(err)
      }
    }
    return response.data
  },
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      uni.reLaunch({ url: '/pages/auth/login' })
    }
    return Promise.reject(error)
  },
)

export function get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return http.get(url, config) as Promise<T>
}

export function post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  return http.post(url, data, config) as Promise<T>
}

export function put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  return http.put(url, data, config) as Promise<T>
}

export function del<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return http.delete(url, config) as Promise<T>
}

export default http
