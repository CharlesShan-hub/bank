<template>
  <!-- 注册页：左右分栏布局——左侧红色品牌面板 + 右侧白色表单区 -->
  <div class="register-split">
    <!-- 左侧品牌面板 -->
    <div class="brand-pane">
      <div class="brand-badge"><BankLogo :size="72" /></div>
      <h1 class="brand-name">银行管理系统</h1>
      <p class="brand-en">BANK MANAGEMENT SYSTEM</p>
      <div class="brand-divider"></div>
      <p class="brand-slogan">开通账户 · 即刻启程</p>
    </div>
    <!-- 右侧表单区：必做要求，注册表单含用户名、密码、确认密码及注册按钮 -->
    <div class="form-pane">
      <div class="form-box">
        <h2 class="form-title">注册新用户</h2>
        <p class="form-sub">填写以下信息完成注册，注册成功将自动开立银行账户</p>
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
        </el-form>
        <div class="footer-link">已有账号？<router-link to="/login">返回登录</router-link></div>
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

// 前端校验规则：用户名 2-20 字符；密码至少 6 位；确认密码用自定义校验器比对两次输入
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

// 点击注册按钮：先过一遍表单校验，再 POST /api/register
// 后端查重用户名，重名返回 400「用户名已存在」，由拦截器统一弹窗
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
.register-split {
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
  margin-bottom: 26px;
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
