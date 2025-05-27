import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'LoginPage',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/',
    name: 'LayoutPage',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'DashboardPage',
        component: () => import('@/views/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'UsersPage',
        component: () => import('@/views/Users.vue')
      },
      {
        path: 'goods',
        name: 'GoodsPage',
        component: () => import('@/views/Goods.vue')
      },
      {
        path: 'orders',
        name: 'OrdersPage',
        component: () => import('@/views/Orders.vue')
      },
      {
        path: 'categories',
        name: 'CategoriesPage',
        component: () => import('@/views/Categories.vue')
      },
      {
        path: 'auth',
        name: 'AuthPage',
        component: () => import('@/views/Auth.vue')
      },
      {
        path: 'profile',
        name: 'ProfilePage',
        component: () => import('@/views/Profile.vue')
      }
    ]
  },
  {
    path: '/401',
    name: '401',
    component: () => import('@/views/401.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('adminToken')
  
  // 白名单路由，不需要登录就可以访问
  const whiteList = ['/login', '/401']
  
  if (whiteList.includes(to.path)) {
    // 如果在白名单中，直接放行
    next()
  } else {
    // 不在白名单中，需要验证token
    if (!token) {
      // 没有token，重定向到登录页
      next('/401')
    } else {
      // 有token，放行
      next()
    }
  }
})

export default router 