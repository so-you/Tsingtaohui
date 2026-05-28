import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { IUserInfo } from '@/types'
import { login as apiLogin } from '@/api/auth'
import { getProfile } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<IUserInfo | null>(null)
  const isLoggedIn = ref(!!token.value)

  async function login(username: string, password: string) {
    const data = await apiLogin({ username, password })
    token.value = data.token
    localStorage.setItem('token', data.token)
    if (data.user) {
      userInfo.value = {
        id: data.user.id,
        username: data.user.username,
        role: data.user.userType || data.user.role,
        userType: data.user.userType,
        preferredLanguage: data.user.preferredLanguage
      }
    }
    isLoggedIn.value = true
  }

  async function fetchProfile() {
    const data = await getProfile()
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
