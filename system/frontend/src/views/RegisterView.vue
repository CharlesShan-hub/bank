<template>
  <!-- 注册页：整页金黄渐变 + 居中白色卡片（上下布局） -->
  <div class="register-wrap">
    <div class="register-card">
      <!-- 顶部品牌区 -->
      <div class="brand">
        <div class="brand-icon"><BankLogo :size="46" /></div>
        <h1 class="brand-title">银行管理系统</h1>
        <p class="brand-en">BANK MANAGEMENT SYSTEM</p>
        <div class="brand-line"></div>
      </div>
      <!-- 必做要求：注册表单含用户名、密码、确认密码及注册按钮；
           el-form 的 rules 做前端校验，:model + v-model 双向绑定数据 -->
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
      <div class="demo-tip">注册成功后将自动开立银行账户</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import BankLogo from '../components/BankLogo.vue'
import { register } from '../api/bank'

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

// 点击注册按钮：先过一遍表单校验，再调用模拟接口 register()
// 内部会查重用户名，重名抛「用户名已存在」由接口层统一弹窗
async function onRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await register({ username: form.username, password: form.password })
    // 注册成功：自动开户完成，跳转登录页面
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // 注册失败：错误提示已由模拟接口层弹窗显示
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
  background: linear-gradient(150deg, #8a6408 0%, #b8860b 100%);
}
.register-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 38px 40px 22px;
  box-shadow: 0 20px 50px rgba(74, 52, 4, 0.35);
}
.brand {
  text-align: center;
  margin-bottom: 22px;
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
.demo-tip {
  margin-top: 18px;
  padding-top: 14px;
  border-top: 1px dashed #eee3c8;
  text-align: center;
  font-size: 12px;
  color: #b8a97e;
}
</style>
