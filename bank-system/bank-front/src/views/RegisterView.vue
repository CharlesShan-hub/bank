<template>
  <div class="auth-page">
    <el-card class="auth-card" shadow="never">
      <div class="brand">
        <BankLogo :size="46" />
        <h2>用户注册</h2>
        <p>创建您的系统账户</p>
      </div>
      <el-divider />
      <!-- v-model 双向绑定表单数据 -->
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码（至少 6 位）" size="large" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirm">
          <el-input v-model="form.confirm" type="password" show-password placeholder="请再次输入密码" size="large" @keyup.enter="onRegister" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="submit-btn" @click="onRegister">注 册</el-button>
        </el-form-item>
        <div class="switch-tip">
          已有账号？<el-link type="primary" :underline="false" @click="router.push('/login')">返回登录</el-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BankLogo from '../components/BankLogo.vue'
import request from '../api/request'

const router = useRouter()
const formRef = ref()
const form = reactive({ username: '', password: '', confirm: '' })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少 6 位', trigger: 'blur' },
  ],
  confirm: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) =>
        value === form.password ? callback() : callback(new Error('两次输入的密码不一致')),
      trigger: 'blur',
    },
  ],
}

function onRegister() {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await request.post('/register', {
        username: form.username,
        password: form.password,
      })
      ElMessage.success('注册成功，请登录')
      router.push('/login') // 注册成功 → 跳转登录页
    } catch {
      // 注册失败 → axios 拦截器已弹窗显示提示信息
    }
  })
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(150deg, #0c2d52 0%, #14589c 100%);
}
.auth-card {
  width: 420px;
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
</style>
