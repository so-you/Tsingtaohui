import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as apiLogin, register as apiRegister } from '../api/auth'
import { getProfile } from '../api/user'
import type { IUserInfo } from '../types'

interface IAuthResponse {
  token: string
  user: IUserInfo
}

export const useUserStore = defineStore(
  'user',
  () => {
    const token = ref<string>('')
    const userInfo = ref<IUserInfo | null>(null)

    const isLoggedIn = computed(() => !!token.value)

    async function login(username: string, password: string) {
      const res = (await apiLogin({ username, password })) as unknown as IAuthResponse
      token.value = res.token
      userInfo.value = res.user
      return res
    }

    async function register(username: string, password: string) {
      const lang = localStorage.getItem('locale') || 'zh-CN'
      const res = (await apiRegister({
        username,
        password,
        preferredLanguage: lang,
      })) as unknown as IAuthResponse
      token.value = res.token
      userInfo.value = res.user
      return res
    }

    async function fetchProfile() {
      const res = (await getProfile()) as unknown as IUserInfo
      userInfo.value = res
      return res
    }

    function logout() {
      token.value = ''
      userInfo.value = null
    }

    return {
      token,
      userInfo,
      isLoggedIn,
      login,
      register,
      fetchProfile,
      logout,
    }
  },
  {
    persist: {
      key: 'user',
      storage: {
        getItem: (key: string) => {
          try {
            return localStorage.getItem(key)
          } catch {
            return null
          }
        },
        setItem: (key: string, value: string) => {
          try {
            localStorage.setItem(key, value)
          } catch {
            // ignore storage errors
          }
        },
      },
      paths: ['token', 'userInfo'],
    },
  },
)
