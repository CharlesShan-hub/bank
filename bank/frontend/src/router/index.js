// 路由表 + 全局守卫
// 页面结构：/login、/register 公开；/admin 管理端（用户管理子页）；/user 普通用户主页
// 本项目选做 2.1（用户管理）+ 2.3（普通用户主页），不含 2.2 账户管理
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/login' }, // 根路径重定向到登录页
  // 懒加载写法：页面组件用到时才下载，首屏更快
  { path: '/login', component: () => import('../views/LoginView.vue') },
  { path: '/register', component: () => import('../views/RegisterView.vue') },
  {
    path: '/admin',
    component: () => import('../views/AdminHome.vue'), // 管理端外壳（侧栏 + 顶栏）
    redirect: '/admin/users', // 进入 /admin 默认落到用户管理
    children: [
      // 选做 2.1：用户管理
      { path: 'users', component: () => import('../views/UserManageView.vue') },
    ],
  },
  {
    path: '/user',
    component: () => import('../views/UserHomeView.vue'), // 普通用户主页（选做 2.3）
  },
]

const router = createRouter({ history: createWebHistory(), routes })

// 路由守卫：未登录跳登录页；/admin 仅管理员可进；普通用户误入 /admin 则回用户主页
router.beforeEach((to) => {
  const raw = localStorage.getItem('user')
  const user = raw ? JSON.parse(raw) : null
  const publicPaths = ['/login', '/register']
  if (!user && !publicPaths.includes(to.path)) {
    return '/login'
  }
  if (to.path.startsWith('/admin') && user && user.role !== 'admin') {
    return '/user'
  }
})

export default router
