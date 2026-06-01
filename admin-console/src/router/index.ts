import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { titleKey: 'route.dashboard' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/users/index.vue'),
        meta: { titleKey: 'route.users' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/products/index.vue'),
        meta: { titleKey: 'route.products' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/orders/index.vue'),
        meta: { titleKey: 'route.orders' }
      },
      {
        path: 'drones',
        name: 'Drones',
        component: () => import('@/views/drones/index.vue'),
        meta: { titleKey: 'route.drones' }
      },
      {
        path: 'customs-sync',
        name: 'CustomsSync',
        component: () => import('@/views/customs-sync/index.vue'),
        meta: { titleKey: 'route.customsSync' }
      },
      {
        path: 'ships',
        name: 'Ships',
        component: () => import('@/views/ships/index.vue'),
        meta: { titleKey: 'route.ships' }
      },
      {
        path: 'rules',
        name: 'Rules',
        component: () => import('@/views/rules/index.vue'),
        meta: { titleKey: 'route.rules' }
      },
      {
        path: 'audit-logs',
        name: 'AuditLogs',
        component: () => import('@/views/audit-logs/index.vue'),
        meta: { titleKey: 'route.auditLogs' }
      },
      {
        path: 'reconciliation',
        name: 'Reconciliation',
        component: () => import('@/views/reconciliation/index.vue'),
        meta: { titleKey: 'route.reconciliation' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory('/admin/'),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth !== false && !token) {
    next('/admin/login')
  } else {
    next()
  }
})

export default router
