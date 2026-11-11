<template>
  <!-- 登录页：整页金黄渐变 + 居中白色卡片（上下布局，回车键也可提交） -->
  <div class="login-wrap">
    <div class="login-card">
      <!-- 顶部品牌区 -->
      <div class="brand">
        <div class="brand-icon"><BankLogo :size="46" /></div>
        <h1 class="brand-title">银行管理系统</h1>
        <p class="brand-en">BANK MANAGEMENT SYSTEM</p>
        <div class="brand-line"></div>
      </div>
      <!-- 登录表单：本项目无后端，login() 从 localStorage "数据库"里校验 -->
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
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import BankLogo from '../components/BankLogo.vue'
import { login } from '../api/bank'

const router = useRouter()
// v-model 双向绑定登录表单
const form = reactive({ username: '', password: '' })
const loading = ref(false)

// 登录：调用模拟接口 login()，成功返回 { id, username, role }
async function onLogin() {
  // 提交前先做非空校验
  if (!form.username || !form.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const user = await login(form)
    // 登录态存 localStorage：路由守卫、顶栏头像都从这里读取
    localStorage.setItem('user', JSON.stringify(user))
    ElMessage.success('登录成功')
    // 按角色分流：管理员进管理主页，普通用户进用户主页
    router.push(user.role === 'admin' ? '/admin' : '/user')
  } catch {
    // 失败提示（用户名或密码错误）已由模拟接口层统一弹窗
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
  background: linear-gradient(150deg, #8a6408 0%, #b8860b 100%);
}
.login-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 42px 40px 24px;
  box-shadow: 0 20px 50px rgba(74, 52, 4, 0.35);
}
.brand {
  text-align: center;
  margin-bottom: 26px;
}
.brand-icon {
  color: #b8860b;
  margin-bottom: 12px;
}
.brand-title {
  font-family: Georgia, 'SimSun', serif;
  font-size: 26px;
  letter-spacing: 6px;
  color: #b8860b;
  margin-bottom: 8px;
}
.brand-en {
  font-size: 11px;
  letter-spacing: 3px;
  color: #bdb49a;
}
.brand-line {
  width: 46px;
  height: 3px;
  background: #d4a017;
  margin: 18px auto 0;
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
  color: #b8860b;
  text-decoration: none;
  font-weight: bold;
}
</style>
