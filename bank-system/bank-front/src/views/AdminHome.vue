<template>
  <el-container class="admin-layout">
    <el-aside width="210px" class="aside">
      <div class="logo">
        <BankLogo :size="26" />
        <span>银行管理系统</span>
      </div>
      <!-- 左侧菜单：点击切换右侧内容 -->
      <el-menu
        router
        :default-active="route.path"
        background-color="transparent"
        text-color="#a8c4dd"
        active-text-color="#ffffff"
        class="menu"
      >
        <el-menu-item index="/admin/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/accounts">
          <el-icon><CreditCard /></el-icon>
          <span>账户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <span class="page-title">{{ pageTitle }}</span>
        <el-dropdown @command="onCommand">
          <span class="user-info">
            <el-avatar :size="30" class="avatar">{{ user?.username?.charAt(0).toUpperCase() }}</el-avatar>
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
      <el-main class="main">
        <router-view />
      </el-main>
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
const user = JSON.parse(localStorage.getItem('user') || 'null')

const pageTitle = computed(() =>
  route.path.startsWith('/admin/users') ? '用户管理' : '账户管理'
)

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
  height: 100vh;
}
.aside {
  background: linear-gradient(180deg, #0c2d52 0%, #14589c 100%);
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-weight: bold;
  font-size: 15px;
  letter-spacing: 1px;
  background: rgba(255, 255, 255, 0.06);
}
.menu {
  border-right: none;
}
.menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.18);
}
.menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06);
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.page-title {
  font-size: 16px;
  font-weight: 600;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.avatar {
  background: #14589c;
}
.username {
  font-size: 14px;
  color: #303133;
}
.main {
  background: #f2f5f9;
}
</style>
