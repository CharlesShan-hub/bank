<template>
  <el-container class="user-layout">
    <el-header class="header">
      <div class="brand">
        <BankLogo :size="24" />
        <span>银行管理系统</span>
      </div>
      <el-dropdown @command="onCommand">
        <span class="user-info">
          <el-avatar :size="30" class="avatar">{{ user?.username?.charAt(0).toUpperCase() }}</el-avatar>
          <span class="username">{{ user?.username }}（普通用户）</span>
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
      <el-card class="welcome" shadow="never">
        <h2>欢迎回来，{{ user?.username }}</h2>
        <p class="sub">普通用户功能（存款、取款、转账）开发中，敬请期待</p>
      </el-card>
    </el-main>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, SwitchButton } from '@element-plus/icons-vue'
import BankLogo from '../components/BankLogo.vue'

const router = useRouter()
const user = JSON.parse(localStorage.getItem('user') || 'null')

function onCommand(command) {
  if (command === 'logout') {
    localStorage.removeItem('user')
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.user-layout {
  min-height: 100vh;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 16px;
  letter-spacing: 1px;
  color: #14447c;
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
.welcome {
  text-align: center;
  padding: 60px 0;
}
.welcome h2 {
  letter-spacing: 2px;
  margin-bottom: 10px;
  color: #14447c;
}
.sub {
  color: #909399;
  font-size: 13px;
}
</style>
