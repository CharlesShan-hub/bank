<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="never">
      <div class="brand">
        <BankLogo :size="46" />
        <h2>银行管理系统</h2>
        <p>Bank Management System</p>
      </div>
      <el-divider />
      <el-form :model="form" label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
            size="large"
            @keyup.enter="onLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="submit-btn" @click="onLogin">登 录</el-button>
        </el-form-item>
        <div class="switch-tip">
          还没有账号？<el-link type="primary" :underline="false" @click="router.push('/register')">立即注册</el-link>
        </div>
      </el-form>
    </el-card>
    <div class="page-tip">演示账号：admin / 123456（管理员） · zhangsan / 123456（普通用户）</div>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BankLogo from '../components/BankLogo.vue'
import request from '../api/request'

const router = useRouter()
const form = reactive({ username: '', password: '' })

async function onLogin() {
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  try {
    const user = await request.post('/login', form)
    localStorage.setItem('user', JSON.stringify(user))
    ElMessage.success('登录成功')
    // 按角色分流：管理员进管理主页，普通用户进用户主页
    router.push(user.role === 'admin' ? '/admin' : '/user')
  } catch {
    // 错误已由 axios 拦截器统一弹窗
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 16px;
  background: linear-gradient(150deg, #0c2d52 0%, #14589c 100%);
}
.auth-card {
  width: 400px;
  border-radius: 14px;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.2);
}
.brand {
  text-align: center;
  color: #14447c;
}
.brand h2 {
  margin: 10px 0 4px;
  letter-spacing: 3px;
}
.brand p {
  color: #909399;
  font-size: 12px;
  letter-spacing: 1px;
}
.submit-btn {
  width: 100%;
  letter-spacing: 4px;
}
.switch-tip {
  text-align: center;
  font-size: 13px;
  color: #909399;
}
.page-tip {
  color: rgba(255, 255, 255, 0.72);
  font-size: 13px;
  letter-spacing: 1px;
}
</style>
