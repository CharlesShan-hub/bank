<template>
  <el-container class="user-layout">
    <el-header class="header">
      <div class="brand">
        <BankLogo :size="24" />
        <span>银行管理系统</span>
      </div>
      <el-dropdown @command="onCommand">
        <span class="user-info">
          <el-avatar :size="30">{{ user?.username?.charAt(0).toUpperCase() }}</el-avatar>
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
      <!-- 余额卡片：展示当前登录用户的账号与实时余额 -->
      <el-card class="balance-card" shadow="never">
        <div class="balance-label">当前账户余额</div>
        <div class="balance-value">¥ {{ balance.toFixed(2) }}</div>
        <div class="account-no">账号：<span class="mono">{{ accountNo }}</span></div>
      </el-card>
      <!-- 选做 2.3：存款、取款、转账三个操作入口 -->
      <div class="actions">
        <el-button type="primary" size="large" round :icon="Money" @click="openDialog('deposit')">存 款</el-button>
        <el-button type="primary" plain size="large" round :icon="Coin" @click="openDialog('withdraw')">取 款</el-button>
        <el-button type="success" size="large" round :icon="Position" @click="openDialog('transfer')">转 账</el-button>
      </div>
    </el-main>

    <!-- 点击不同按钮显示不同操作表单 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px" destroy-on-close>
      <el-form label-width="90px">
        <el-form-item v-if="dialogType === 'transfer'" label="对方账号">
          <el-input v-model="form.toAccountNo" placeholder="请输入对方账号" class="mono-input" />
        </el-form-item>
        <el-form-item label="操作金额">
          <el-input-number v-model="form.amount" :min="0.01" :max="1000000" :precision="2" :step="100" style="width: 220px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">确定</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowDown, SwitchButton, Money, Coin, Position } from '@element-plus/icons-vue'
import BankLogo from '../components/BankLogo.vue'
import request from '../api/request'

const router = useRouter()
// 登录信息由登录页写入 localStorage，这里取当前用户 id 调接口
const user = JSON.parse(localStorage.getItem('user') || 'null')

const accountNo = ref('')
const balance = ref(0)
const dialogVisible = ref(false)
const dialogType = ref('deposit')
const submitting = ref(false)
const form = reactive({ toAccountNo: '', amount: 100 })

const dialogTitle = computed(() => ({ deposit: '存款', withdraw: '取款', transfer: '转账' }[dialogType.value]))

// 查询当前用户账户：GET /api/account/{userId}
// 进入页面时自动调用，每次存取转成功后也再调一次刷新余额
async function load() {
  const acc = await request.get(`/account/${user.id}`)
  accountNo.value = acc.accountNo
  balance.value = Number(acc.balance)
}

// 打开操作弹窗：deposit / withdraw / transfer 三种类型共用一个弹窗
function openDialog(type) {
  dialogType.value = type
  form.amount = 100
  if (type === 'transfer') form.toAccountNo = ''
  dialogVisible.value = true
}

// 提交操作：按弹窗类型分别调用
// POST /api/account/{id}/deposit | /withdraw | /transfer
async function onSubmit() {
  if (!form.amount || form.amount <= 0) {
    ElMessage.warning('请输入正确金额')
    return
  }
  if (dialogType.value === 'transfer' && !form.toAccountNo.trim()) {
    ElMessage.warning('请输入对方账号')
    return
  }
  submitting.value = true
  try {
    if (dialogType.value === 'deposit') {
      await request.post(`/account/${user.id}/deposit`, { amount: form.amount })
      ElMessage.success('存款成功')
    } else if (dialogType.value === 'withdraw') {
      await request.post(`/account/${user.id}/withdraw`, { amount: form.amount })
      ElMessage.success('取款成功')
    } else {
      await request.post(`/account/${user.id}/transfer`, { toAccountNo: form.toAccountNo.trim(), amount: form.amount })
      ElMessage.success('转账成功')
    }
    dialogVisible.value = false
    load()
  } catch {
    // 失败提示（如余额不足）已由 axios 拦截器弹窗显示
  } finally {
    submitting.value = false
  }
}

function onCommand(command) {
  if (command === 'logout') {
    localStorage.removeItem('user')
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}

load()
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
  border-bottom: 1px solid #e4e9e6;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 16px;
  letter-spacing: 2px;
  font-family: Georgia, 'SimSun', serif;
  color: #0f6b4f;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.user-info :deep(.el-avatar) {
  background: #0f6b4f;
}
.username {
  font-size: 14px;
  color: #303133;
}
.main {
  background: #f4f7f5;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 60px;
}
.balance-card {
  width: 420px;
  border-radius: 16px;
  text-align: center;
  background: linear-gradient(140deg, #0a3f2f 0%, #0f6b4f 100%);
  color: #fff;
  border-top: 3px solid #0d9b6f;
}
.balance-label {
  font-size: 14px;
  opacity: 0.8;
  letter-spacing: 2px;
}
.balance-value {
  font-size: 40px;
  font-weight: bold;
  font-family: Consolas, Monaco, monospace;
  margin: 16px 0 12px;
  letter-spacing: 2px;
  color: #ffffff;
}
.account-no {
  font-size: 13px;
  opacity: 0.8;
}
.mono {
  font-family: Consolas, Monaco, monospace;
  letter-spacing: 1px;
}
.actions {
  margin-top: 40px;
  display: flex;
  gap: 20px;
}
.actions :deep(.el-button) {
  padding: 22px 34px;
  font-size: 16px;
  letter-spacing: 4px;
}
</style>
