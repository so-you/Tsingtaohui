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

// Response interceptor: handle 401
http.interceptors.response.use(
  (response: AxiosResponse) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('user')
      uni.reLaunch({ url: '/pages/auth/login' })
    }
    return Promise.reject(error)
  },
)

export function get<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return http.get<T, T>(url, config)
}

export function post<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  return http.post<T, T>(url, data, config)
}

export function put<T = unknown>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
  return http.put<T, T>(url, data, config)
}

export function del<T = unknown>(url: string, config?: AxiosRequestConfig): Promise<T> {
  return http.delete<T, T>(url, config)
}

export default http
