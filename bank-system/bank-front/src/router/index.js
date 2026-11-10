import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/LoginView.vue') },
  { path: '/register', component: () => import('../views/RegisterView.vue') },
  {
    path: '/admin',
    component: () => import('../views/AdminHome.vue'),
    redirect: '/admin/users',
    children: [
      { path: 'users', component: () => import('../views/UserManageView.vue') },
      { path: 'accounts', component: () => import('../views/AccountManageView.vue') },
    ],
  },
  {
    path: '/user',
    component: () => import('../views/UserHomeView.vue'),
  },
]

const router = createRouter({ history: createWebHistory(), routes })

// 登录守卫：未登录只能访问登录/注册页，管理页仅限 admin
router.beforeEach((to) => {
  const raw = localStorage.getItem('user')
  const user = raw ? JSON.parse(raw) : null
  const publicPaths = ['/login', '/register']
  if (!user && !publicPaths.includes(to.path)) return '/login'
  if (to.path.startsWith('/admin') && user?.role !== 'admin') return '/login'
})

export default router
