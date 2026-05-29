<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { useResponsive } from '@/composables/useResponsive'
import {
  Odometer,
  User,
  Goods,
  List,
  SwitchButton,
  Fold,
  Expand,
  ArrowDown,
  Position,
  DocumentChecked,
  Memo,
  Download,
  Setting,
  Van
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const { t, locale } = useI18n()
const userStore = useUserStore()
const { isMobile } = useResponsive()

const isCollapse = ref(false)
const mobileMenuOpen = ref(false)

const activeMenu = computed(() => {
  return route.path
})

const breadcrumb = computed(() => {
  const matched = route.matched
  return matched.map(m => ({
    title: m.meta.titleKey ? t(m.meta.titleKey as string) : (m.name as string),
    path: m.path
  })).filter(b => b.title)
})

function handleSelect(path: string) {
  router.push(path)
  if (isMobile.value) {
    mobileMenuOpen.value = false
  }
}

function handleLanguage(lang: string) {
  locale.value = lang
  localStorage.setItem('locale', lang)
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}

function toggleSidebar() {
  if (isMobile.value) {
    mobileMenuOpen.value = !mobileMenuOpen.value
  } else {
    isCollapse.value = !isCollapse.value
  }
}

const sidebarWidth = computed(() => {
  if (isMobile.value) return '240px'
  return isCollapse.value ? '64px' : '240px'
})

// Close mobile menu on route change
watch(() => route.path, () => {
  if (isMobile.value) {
    mobileMenuOpen.value = false
  }
})
</script>

<template>
  <el-container class="admin-layout">
    <!-- Mobile Backdrop -->
    <div
      v-if="isMobile && mobileMenuOpen"
      class="sidebar-backdrop"
      @click="mobileMenuOpen = false"
    />

    <!-- Sidebar -->
    <el-aside
      :width="sidebarWidth"
      class="sidebar"
      :class="{ 'mobile-open': isMobile && mobileMenuOpen }"
    >
      <div class="logo">
        <div class="logo-icon">
          <el-icon :size="28"><Odometer /></el-icon>
        </div>
        <h1 v-show="!isCollapse || isMobile" class="logo-text">{{ t('layout.brandName') }}</h1>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse && !isMobile"
        :collapse-transition="false"
        router
        class="sidebar-menu"
        @select="handleSelect"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>{{ t('menu.dashboard') }}</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>{{ t('menu.users') }}</template>
        </el-menu-item>
        <el-menu-item index="/products">
          <el-icon><Goods /></el-icon>
          <template #title>{{ t('menu.products') }}</template>
        </el-menu-item>
        <el-menu-item index="/orders">
          <el-icon><List /></el-icon>
          <template #title>{{ t('menu.orders') }}</template>
        </el-menu-item>
        <el-menu-item index="/drones">
          <el-icon><Position /></el-icon>
          <template #title>{{ t('menu.drones') }}</template>
        </el-menu-item>
        <el-menu-item index="/customs-sync">
          <el-icon><DocumentChecked /></el-icon>
          <template #title>{{ t('menu.customsSync') }}</template>
        </el-menu-item>
        <el-menu-item index="/ships">
          <el-icon><Van /></el-icon>
          <template #title>{{ t('menu.ships') }}</template>
        </el-menu-item>
        <el-menu-item index="/rules">
          <el-icon><Setting /></el-icon>
          <template #title>{{ t('menu.rules') }}</template>
        </el-menu-item>
        <el-menu-item index="/audit-logs">
          <el-icon><Memo /></el-icon>
          <template #title>{{ t('menu.auditLogs') }}</template>
        </el-menu-item>
        <el-menu-item index="/reconciliation">
          <el-icon><Download /></el-icon>
          <template #title>{{ t('menu.reconciliation') }}</template>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer" v-show="!isCollapse || isMobile">
        <p class="version">v1.0.0</p>
      </div>
    </el-aside>

    <el-container class="main-wrapper">
      <!-- Header -->
      <el-header class="header">
        <div class="header-left">
          <div class="collapse-btn" @click="toggleSidebar">
            <el-icon :size="18">
              <Fold v-if="isMobile ? mobileMenuOpen : !isCollapse" />
              <Expand v-else />
            </el-icon>
          </div>

          <el-breadcrumb separator="/" class="header-breadcrumb">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">
              {{ t('menu.dashboard') }}
            </el-breadcrumb-item>
            <el-breadcrumb-item v-for="(item, index) in breadcrumb" :key="index">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleLanguage" class="lang-dropdown">
            <span class="header-trigger">
              <el-icon :size="16"><Globe /></el-icon>
              <span class="trigger-text">
                {{ locale === 'zh-CN' ? t('common.languageZh') : t('common.languageEn') }}
              </span>
              <el-icon :size="12"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="zh-CN">{{ t('common.languageZh') }}</el-dropdown-item>
                <el-dropdown-item command="en-US">{{ t('common.languageEn') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <el-divider direction="vertical" class="header-divider" />

          <el-dropdown @command="handleLogout">
            <span class="header-trigger">
              <el-avatar :size="28" class="user-avatar">
                {{ userStore.userInfo?.username?.charAt(0).toUpperCase() || 'A' }}
              </el-avatar>
              <span class="trigger-text user-name">
                {{ userStore.userInfo?.username || 'Admin' }}
              </span>
              <el-icon :size="12"><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  {{ t('common.logout') }}
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- Main Content -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
  background: #f0f2f5;
}

/* Sidebar */
.sidebar {
  background: #001529;
  transition: width 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.logo-icon {
  width: 36px;
  height: 36px;
  background: #1677ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.logo-text {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  letter-spacing: 1px;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: #001529;
  padding: 8px 0;
}

.sidebar-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.65);
  height: 48px;
  line-height: 48px;
  margin: 4px 8px;
  border-radius: 6px;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.05);
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  color: #fff;
  background: #1677ff;
}

.sidebar-menu :deep(.el-icon) {
  color: inherit;
}

.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.06);
}

.version {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.35);
}

/* Header */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 24px;
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.02);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.collapse-btn {
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  cursor: pointer;
  color: #666;
  transition: all 0.2s;
}

.collapse-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.header-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.2s;
}

.header-trigger:hover {
  background: #f5f5f5;
}

.trigger-text {
  font-size: 14px;
}

.user-name {
  font-weight: 500;
  color: #333;
}

.user-avatar {
  background: #1677ff;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
}

.header-divider {
  height: 20px;
  margin: 0 8px;
}

/* Main Content */
.main-content {
  padding: 20px;
  overflow-y: auto;
}

.main-wrapper {
  background: #f0f2f5;
}

/* Mobile Responsive */
.sidebar-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1999;
  transition: opacity 0.3s;
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    top: 0;
    left: 0;
    height: 100vh;
    z-index: 2000;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }

  .sidebar.mobile-open {
    transform: translateX(0);
  }

  .main-wrapper {
    width: 100%;
  }

  .header-breadcrumb {
    display: none;
  }

  .main-content {
    padding: 12px;
  }

  .trigger-text {
    display: none;
  }

  .header-divider {
    display: none;
  }

  .header {
    padding: 0 12px;
  }
}
</style>
