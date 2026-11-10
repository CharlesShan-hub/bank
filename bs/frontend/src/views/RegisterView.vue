<template>
  <div class="register-wrap">
    <div class="register-panel">
      <!-- 左侧品牌区：墨绿底 + 白色点缀 -->
      <div class="brand-side">
        <div class="brand-icon"><BankLogo :size="46" /></div>
        <h1 class="brand-title">银行管理系统</h1>
        <p class="brand-en">BANK MANAGEMENT SYSTEM</p>
        <div class="brand-line"></div>
        <p class="brand-slogan">开启您的专属账户</p>
        <div class="deco deco-1"></div>
        <div class="deco deco-2"></div>
      </div>
      <!-- 右侧表单区 -->
      <div class="form-side">
        <h2 class="form-title">注册新用户</h2>
        <el-form ref="formRef" :model="form" :rules="rules" size="large" @keyup.enter="onRegister">
          <el-form-item prop="username">
            <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="form.password" type="password" placeholder="密码" show-password :prefix-icon="Lock" />
          </el-form-item>
          <el-form-item prop="confirmPassword">
            <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" show-password :prefix-icon="Lock" />
          </el-form-item>
          <el-button class="submit-btn" type="primary" size="large" :loading="loading" @click="onRegister">注 册</el-button>
          <div class="footer-link">已有账号？<router-link to="/login">返回登录</router-link></div>
        </el-form>
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
const formRef = ref(null)
const loading = ref(false)
// v-model 双向绑定表单数据
const form = reactive({ username: '', password: '', confirmPassword: '' })

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度为 2 - 20 个字符', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) =>
        value === form.password ? callback() : callback(new Error('两次输入的密码不一致')),
      trigger: 'blur',
    },
  ],
}

// 点击注册按钮，axios 发起注册请求
async function onRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await request.post('/register', { username: form.username, password: form.password })
    // 注册成功：跳转登录页面
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // 注册失败：错误提示已由 axios 拦截器弹窗显示
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-wrap {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f7f5;
}
.register-panel {
  width: 780px;
  min-height: 500px;
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
  padding: 44px 46px 30px;
}
.form-title {
  font-family: Georgia, 'SimSun', serif;
  font-size: 22px;
  letter-spacing: 3px;
  color: #0f6b4f;
  margin-bottom: 22px;
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
</style>
