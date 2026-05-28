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
        meta: { title: '运营仪表盘' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('@/views/users/index.vue'),
        meta: { title: '用户管理' }
      },
      {
        path: 'products',
        name: 'Products',
        component: () => import('@/views/products/index.vue'),
        meta: { title: '商品管理' }
      },
      {
        path: 'orders',
        name: 'Orders',
        component: () => import('@/views/orders/index.vue'),
        meta: { title: '订单管理' }
      },
      {
        path: 'drones',
        name: 'Drones',
        component: () => import('@/views/drones/index.vue'),
        meta: { title: '无人机管理' }
      },
      {
        path: 'customs-sync',
        name: 'CustomsSync',
        component: () => import('@/views/customs-sync/index.vue'),
        meta: { title: '海关同步' }
      },
      {
        path: 'ships',
        name: 'Ships',
        component: () => import('@/views/ships/index.vue'),
        meta: { title: '船舶管理' }
      },
      {
        path: 'rules',
        name: 'Rules',
        component: () => import('@/views/rules/index.vue'),
        meta: { title: '规则配置' }
      },
      {
        path: 'audit-logs',
        name: 'AuditLogs',
        component: () => import('@/views/audit-logs/index.vue'),
        meta: { title: '审计日志' }
      },
      {
        path: 'reconciliation',
        name: 'Reconciliation',
        component: () => import('@/views/reconciliation/index.vue'),
        meta: { title: '对账导出' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth !== false && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
