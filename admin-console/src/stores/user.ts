import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { IUserInfo } from '@/types'
import { post, get } from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<IUserInfo | null>(null)
  const isLoggedIn = ref(!!token.value)

  async function login(username: string, password: string) {
    const data = await post<{ token: string }>('/auth/login', { username, password })
    token.value = data.token
    localStorage.setItem('token', data.token)
    isLoggedIn.value = true
  }

  async function fetchProfile() {
    const data = await get<IUserInfo>('/admin/profile')
    userInfo.value = data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    isLoggedIn.value = false
    localStorage.removeItem('token')
  }

  return { token, userInfo, isLoggedIn, login, logout, fetchProfile }
})
