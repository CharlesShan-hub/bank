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
async function onRegister() {
  await formRef.value.validate()
  loading.value = true
  try {
    await request.post('/register', { username: form.username, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // 注册失败：错误提示已由 axios 拦截器弹窗显示
  } finally {
    loading.value = false
  }
}