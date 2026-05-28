import { createI18n } from 'vue-i18n'
import zhCN from './zh-CN'
import enUS from './en-US'

function getStoredLocale(): string {
  try {
    const stored = localStorage.getItem('locale')
    if (stored && (stored === 'zh-CN' || stored === 'en-US')) {
      return stored
    }
  } catch {
    // localStorage may not be available in SSR
  }
  return 'zh-CN'
}

const i18n = createI18n({
  legacy: false,
  locale: getStoredLocale(),
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS,
  },
})

export default i18n
