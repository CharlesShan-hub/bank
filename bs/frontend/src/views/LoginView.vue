<template>
  <!-- 登录页：左侧品牌区 + 右侧表单区（回车键也可提交） -->
  <div class="login-wrap">
    <div class="login-panel">
      <!-- 左侧品牌区：墨绿底 + 白色点缀 -->
      <div class="brand-side">
        <div class="brand-icon"><BankLogo :size="46" /></div>
        <h1 class="brand-title">银行管理系统</h1>
        <p class="brand-en">BANK MANAGEMENT SYSTEM</p>
        <div class="brand-line"></div>
        <p class="brand-slogan">安全 · 稳健 · 值得信赖</p>
        <div class="deco deco-1"></div>
        <div class="deco deco-2"></div>
      </div>
      <!-- 右侧表单区 -->
      <div class="form-side">
        <h2 class="form-title">欢迎登录</h2>
        <el-form :model="form" size="large" @keyup.enter="onLogin">
          <el-form-item>
            <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.password" type="password" placeholder="密码" show-password :prefix-icon="Lock" />
          </el-form-item>
          <el-button class="submit-btn" type="primary" size="large" :loading="loading" @click="onLogin">登 录</el-button>
          <div class="footer-link">还没有账号？<router-link to="/register">立即注册</router-link></div>
        </el-form>
        <div class="demo-tip">演示账号：admin / 123456（管理员） · zhangsan / 123456（普通用户）</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import BankLogo from '../components/BankLogo.vue'
import request from '../api/request'

const router = useRouter()
// v-model 双向绑定登录表单
const form = reactive({ username: '', password: '' })
const loading = ref(false)

// 登录：POST /api/login，成功后端返回 { id, username, role }
async function onLogin() {
  // 提交前先做非空校验，避免无谓请求
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const user = await request.post('/login', form)
    // 登录态存 localStorage：路由守卫、顶栏头像都从这里读取
    localStorage.setItem('user', JSON.stringify(user))
    ElMessage.success('登录成功')
    // 按角色分流：管理员进管理主页，普通用户进用户主页
    router.push(user.role === 'admin' ? '/admin' : '/user')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f7f5;
}
.login-panel {
  width: 780px;
  min-height: 460px;
  display: flex;
  border-radius: 16px;
  overflow: hidden;
  border: 1px solid #e4e9e6;
  box-shadow: 0 20px 50px rgba(10, 63, 47, 0.18);
}
.brand-side {
  position: relative;
  width: 44%;
  background: linear-gradient(160deg, #0a3f2f 0%, #0f6b4f 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 40px;
  overflow: hidden;
}
.brand-icon {
  color: #ffffff;
  opacity: 0.95;
  margin-bottom: 18px;
}
.brand-title {
  font-family: Georgia, 'SimSun', serif;
  font-size: 30px;
  letter-spacing: 6px;
  margin-bottom: 10px;
}
.brand-en {
  font-size: 11px;
  letter-spacing: 3px;
  color: rgba(255, 255, 255, 0.6);
}
.brand-line {
  width: 46px;
  height: 3px;
  background: #0d9b6f;
  margin: 22px 0;
}
.brand-slogan {
  font-size: 15px;
  letter-spacing: 4px;
  color: rgba(255, 255, 255, 0.85);
}
.deco {
  position: absolute;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 50%;
}
.deco-1 {
  width: 220px;
  height: 220px;
  right: -80px;
  top: -80px;
}
.deco-2 {
  width: 140px;
  height: 140px;
  left: -50px;
  bottom: -60px;
}
.form-side {
  flex: 1;
  background: #fff;
  padding: 52px 46px 30px;
  display: flex;
  flex-direction: column;
}
.form-title {
  font-family: Georgia, 'SimSun', serif;
  font-size: 22px;
  letter-spacing: 3px;
  color: #0f6b4f;
  margin-bottom: 26px;
}
.submit-btn {
  width: 100%;
  letter-spacing: 6px;
}
.footer-link {
  text-align: center;
  font-size: 13px;
  color: #909399;
  margin-top: 15px;
}
.footer-link a {
  color: #0f6b4f;
  text-decoration: none;
  font-weight: bold;
}
.demo-tip {
  margin-top: auto;
  padding-top: 24px;
  text-align: center;
  font-size: 12px;
  color: #9aa8a0;
}
</style>
