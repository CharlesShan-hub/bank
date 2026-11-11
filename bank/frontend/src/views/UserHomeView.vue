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
      <div class="home-body">
        <!-- 左侧：银行卡样式的余额卡片 -->
        <el-card class="balance-card" shadow="never">
          <div class="card-top">
            <BankLogo :size="26" />
            <el-tag class="card-tag" size="small" round>储蓄账户</el-tag>
          </div>
          <div class="balance-label">当前账户余额（元）</div>
          <div class="balance-value">¥ {{ balance.toFixed(2) }}</div>
          <div class="card-bottom">
            <span class="account-no">账号 <span class="mono">{{ accountNo || '—' }}</span></span>
            <el-tag :type="status === '正常' ? 'success' : 'danger'" effect="plain" size="small">{{ status }}</el-tag>
          </div>
        </el-card>

        <!-- 右侧：竖排账户操作面板（选做 2.3：存款 / 取款 / 转账） -->
        <div class="actions-panel">
          <h3 class="panel-title">账户操作</h3>
          <el-button type="primary" size="large" :icon="Money" class="op-btn" @click="openDialog('deposit')">存 款</el-button>
          <el-button type="primary" plain size="large" :icon="Coin" class="op-btn" @click="openDialog('withdraw')">取 款</el-button>
          <el-button type="warning" size="large" :icon="Position" class="op-btn" @click="openDialog('transfer')">转 账</el-button>
          <p class="panel-tip">点击按钮发起相应交易</p>
        </div>
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
const status = ref('正常')
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
  status.value = acc.status
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
  border-bottom: 1px solid #efe4e4;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 16px;
  letter-spacing: 2px;
  font-family: Georgia, 'SimSun', serif;
  color: #b01e28;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.user-info :deep(.el-avatar) {
  background: #b01e28;
}
.username {
  font-size: 14px;
  color: #303133;
}
.main {
  background: #f8f4f3;
  display: flex;
  justify-content: center;
  align-items: center;
  height: calc(100vh - 60px);
  padding: 20px;
}
/* 左右布局：左边银行卡，右边操作面板 */
.home-body {
  display: flex;
  align-items: stretch;
  gap: 26px;
}
/* 银行卡样式余额卡片 */
.balance-card {
  width: 400px;
  border-radius: 18px;
  background: linear-gradient(150deg, #6f1019 0%, #b01e28 100%);
  border-top: 3px solid #d24a52;
  color: #fff;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 232px;
  box-shadow: 0 14px 34px rgba(60, 8, 13, 0.24);
}
.card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.card-tag {
  background: rgba(255, 255, 255, 0.22);
  border: none;
  color: #fff;
}
.balance-label {
  font-size: 13px;
  opacity: 0.85;
  letter-spacing: 2px;
}
.balance-value {
  font-size: 42px;
  font-weight: bold;
  font-family: Consolas, Monaco, monospace;
  letter-spacing: 2px;
  margin-top: 8px;
}
.card-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.account-no {
  font-size: 13px;
  opacity: 0.85;
}
.mono {
  font-family: Consolas, Monaco, monospace;
  letter-spacing: 1px;
}
/* 右侧操作面板：竖排按钮 */
.actions-panel {
  width: 300px;
  background: #fff;
  border: 1px solid #efe4e4;
  border-radius: 18px;
  padding: 28px 26px 20px;
  display: flex;
  flex-direction: column;
}
.panel-title {
  margin: 0 0 20px;
  font-family: Georgia, 'SimSun', serif;
  font-size: 18px;
  font-weight: bold;
  letter-spacing: 3px;
  color: #b01e28;
  padding-bottom: 10px;
  border-bottom: 2px solid #b01e28;
}
.actions-panel .op-btn {
  width: 100%;
  height: 52px;
  margin: 0 0 15px;
  border-radius: 10px;
  font-size: 15px;
  letter-spacing: 6px;
}
.panel-tip {
  margin: 2px 0 0;
  text-align: center;
  font-size: 12px;
  color: #b9a8a8;
}
</style>
