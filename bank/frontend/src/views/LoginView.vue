<template>
  <!-- 登录页：左右分栏布局——左侧红色品牌面板 + 右侧白色表单区（回车键也可提交） -->
  <div class="login-split">
    <!-- 左侧品牌面板 -->
    <div class="brand-pane">
      <div class="brand-badge"><BankLogo :size="72" /></div>
      <h1 class="brand-name">银行管理系统</h1>
      <p class="brand-en">BANK MANAGEMENT SYSTEM</p>
      <div class="brand-divider"></div>
      <p class="brand-slogan">安全 · 稳健 · 值得信赖</p>
    </div>
    <!-- 右侧表单区 -->
    <div class="form-pane">
      <div class="form-box">
        <h2 class="form-title">欢迎登录</h2>
        <p class="form-sub">请输入您的账号信息进入系统</p>
        <el-form :model="form" size="large" @keyup.enter="onLogin">
          <el-form-item>
            <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item>
            <el-input v-model="form.password" type="password" placeholder="密码" show-password :prefix-icon="Lock" />
          </el-form-item>
          <el-button class="submit-btn" type="primary" size="large" :loading="loading" @click="onLogin">登 录</el-button>
        </el-form>
        <div class="footer-link">还没有账号？<router-link to="/register">立即注册</router-link></div>
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
.login-split {
  min-height: 100vh;
  display: flex;
}
/* 左侧品牌面板：整块深红渐变 + 装饰圆环 */
.brand-pane {
  width: 40%;
  min-width: 380px;
  background: linear-gradient(160deg, #6f1019 0%, #b01e28 100%);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}
.brand-pane::before,
.brand-pane::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.15);
}
.brand-pane::before {
  width: 320px;
  height: 320px;
  top: -110px;
  left: -90px;
}
.brand-pane::after {
  width: 240px;
  height: 240px;
  bottom: -90px;
  right: -70px;
}
.brand-badge {
  color: #fff;
  margin-bottom: 22px;
  position: relative;
  z-index: 1;
}
.brand-name {
  font-family: Georgia, 'SimSun', serif;
  font-size: 30px;
  letter-spacing: 8px;
  margin-bottom: 10px;
  position: relative;
  z-index: 1;
}
.brand-en {
  font-size: 12px;
  letter-spacing: 4px;
  opacity: 0.75;
  position: relative;
  z-index: 1;
}
.brand-divider {
  width: 52px;
  height: 3px;
  background: rgba(255, 255, 255, 0.55);
  margin: 24px auto;
  position: relative;
  z-index: 1;
}
.brand-slogan {
  font-size: 14px;
  letter-spacing: 6px;
  opacity: 0.9;
  position: relative;
  z-index: 1;
}
/* 右侧表单区：米白底 + 居中窄表单 */
.form-pane {
  flex: 1;
  background: #faf6f6;
  display: flex;
  align-items: center;
  justify-content: center;
}
.form-box {
  width: 380px;
}
.form-title {
  font-family: Georgia, 'SimSun', serif;
  font-size: 26px;
  letter-spacing: 4px;
  color: #b01e28;
  margin-bottom: 8px;
}
.form-sub {
  font-size: 13px;
  color: #9b8f8f;
  margin-bottom: 30px;
}
.submit-btn {
  width: 100%;
  letter-spacing: 6px;
  margin-top: 4px;
}
.footer-link {
  text-align: center;
  font-size: 13px;
  color: #909399;
  margin-top: 18px;
}
.footer-link a {
  color: #b01e28;
  text-decoration: none;
  font-weight: bold;
}
</style>
