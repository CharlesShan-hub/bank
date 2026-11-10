import { createRouter, createWebHistory } from 'vue-router'

// 路由表说明：
// /login、/register 公开页；
// /admin 管理端，嵌套用户管理、账户管理两个子页；
// /user 普通用户主页
const routes = [
  // 根路径重定向到登录页
  { path: '/', redirect: '/login' },
  { path: '/login', component: () => import('../views/LoginView.vue') },
  { path: '/register', component: () => import('../views/RegisterView.vue') },
  // 管理端主页 + 两个子页（选做 2.1 / 2.2）
  {
    path: '/admin',
    component: () => import('../views/AdminHome.vue'),
    redirect: '/admin/users',
    children: [
      { path: 'users', component: () => import('../views/UserManageView.vue') },
      { path: 'accounts', component: () => import('../views/AccountManageView.vue') },
    ],
  },
  // 普通用户主页
  {
    path: '/user',
    component: () => import('../views/UserHomeView.vue'),
  },
]

const router = createRouter({ history: createWebHistory(), routes })

// 登录守卫：未登录只能访问登录/注册页，管理页仅限 admin
router.beforeEach((to) => {
  // 登录态保存在 localStorage，刷新页面后依然生效
  const raw = localStorage.getItem('user')
  const user = raw ? JSON.parse(raw) : null
  const publicPaths = ['/login', '/register']
  // 未登录访问受保护页面 → 打回登录页
  if (!user && !publicPaths.includes(to.path)) return '/login'
  // 非管理员访问管理端页面 → 打回登录页
  if (to.path.startsWith('/admin') && user?.role !== 'admin') return '/login'
})

export default router
