<template>
  <!-- 管理端外壳：左侧菜单 + 顶栏 + 内容区，子页面由 <router-view> 渲染 -->
  <el-container class="admin-layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <BankLogo :size="26" />
        <span>银行管理系统</span>
      </div>
      <!-- router 模式：菜单项的 index 即跳转路径，当前路由自动高亮 -->
      <!-- 本项目选做 2.1 + 2.2，管理端有用户管理、账户管理两个菜单项 -->
      <el-menu
        :default-active="route.path"
        router
        background-color="transparent"
        text-color="#ecd9a0"
        active-text-color="#ffffff"
        class="menu"
      >
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon><span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/accounts">
          <el-icon><CreditCard /></el-icon><span>账户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="page-title">{{ pageTitle }}</span>
        <el-dropdown @command="onCommand">
          <span class="user-info">
            <el-avatar :size="30">{{ user?.username?.charAt(0).toUpperCase() }}</el-avatar>
            <span class="username">{{ user?.username }}（管理员）</span>
            <el-icon><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, CreditCard, ArrowDown, SwitchButton } from '@element-plus/icons-vue'
import BankLogo from '../components/BankLogo.vue'

const route = useRoute()
const router = useRouter()
// 登录信息存在 localStorage（登录页写入），这里读取用于顶栏展示
const user = JSON.parse(localStorage.getItem('user') || 'null')

// 顶栏标题跟随当前子页面变化
const pageTitle = computed(() =>
  ({ '/admin/users': '用户管理', '/admin/accounts': '账户管理' }[route.path] || '管理主页')
)

// 下拉菜单命令：目前仅「退出登录」——清掉登录态并回登录页
function onCommand(command) {
  if (command === 'logout') {
    localStorage.removeItem('user')
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}
.aside {
  background: linear-gradient(180deg, #8a6408 0%, #b8860b 100%);
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 60px;
  padding: 0 20px;
  color: #fff;
  font-weight: bold;
  font-size: 15px;
  letter-spacing: 2px;
  font-family: Georgia, 'SimSun', serif;
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
}
.menu {
  border-right: none;
}
.menu :deep(.el-menu-item) {
  margin: 6px 12px;
  border-radius: 6px;
}
.menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.18);
}
.menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #efe8d8;
}
.page-title {
  font-family: Georgia, 'SimSun', serif;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 3px;
  color: #b8860b;
  padding-bottom: 6px;
  border-bottom: 3px solid #b8860b;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.user-info :deep(.el-avatar) {
  background: #b8860b;
}
.username {
  font-size: 14px;
  color: #303133;
}
.main {
  background: #faf7ef;
}
</style>
