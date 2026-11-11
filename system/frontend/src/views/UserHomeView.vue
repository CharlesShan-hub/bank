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
      <div class="page-body">
        <!-- 顶部资产横幅：欢迎语 + 余额大数字 + 账号与状态 -->
        <div class="banner">
          <div class="banner-left">
            <div class="hello">你好，{{ user?.username }}</div>
            <div class="sub">欢迎回到您的个人账户中心</div>
          </div>
          <div class="banner-right">
            <div class="balance-label">当前账户余额（元）</div>
            <div class="balance-value">{{ balance.toFixed(2) }}</div>
            <div class="meta">
              <span class="chip">账号 {{ accountNo || '—' }}</span>
              <el-tag :type="status === '正常' ? 'success' : 'danger'" effect="dark" size="small">{{ status }}</el-tag>
            </div>
          </div>
        </div>

        <!-- 选做 2.3：三张操作卡片，点击进入对应操作弹窗 -->
        <div class="ops">
          <div class="op-card" @click="openDialog('deposit')">
            <div class="op-icon"><el-icon :size="26"><Money /></el-icon></div>
            <h3 class="op-title">存款</h3>
            <p class="op-desc">将现金存入本账户，资金实时到账</p>
            <span class="op-go">立即存款 →</span>
          </div>
          <div class="op-card" @click="openDialog('withdraw')">
            <div class="op-icon"><el-icon :size="26"><Coin /></el-icon></div>
            <h3 class="op-title">取款</h3>
            <p class="op-desc">从本账户支取资金，余额充足即可办理</p>
            <span class="op-go">立即取款 →</span>
          </div>
          <div class="op-card" @click="openDialog('transfer')">
            <div class="op-icon"><el-icon :size="26"><Position /></el-icon></div>
            <h3 class="op-title">转账</h3>
            <p class="op-desc">输入对方账号向他行账户划转资金</p>
            <span class="op-go">立即转账 →</span>
          </div>
        </div>
      </div>
    </el-main>

    <!-- 点击不同卡片显示不同操作表单 -->
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
import { getMyAccount, deposit, withdraw, transfer } from '../api/bank'

const router = useRouter()
// 登录信息由登录页写入 localStorage，这里取当前用户 id 调模拟接口
const user = JSON.parse(localStorage.getItem('user') || 'null')

const accountNo = ref('')
const balance = ref(0)
const status = ref('正常')
const dialogVisible = ref(false)
const dialogType = ref('deposit')
const submitting = ref(false)
const form = reactive({ toAccountNo: '', amount: 100 })

const dialogTitle = computed(() => ({ deposit: '存款', withdraw: '取款', transfer: '转账' }[dialogType.value]))

// 查询当前用户账户：模拟接口 getMyAccount(userId)
// 进入页面时自动调用，每次存取转成功后也再调一次刷新余额
async function load() {
  try {
    const acc = await getMyAccount(user.id)
    accountNo.value = acc.accountNo
    balance.value = Number(acc.balance)
    status.value = acc.status
  } catch {
    // 账户不存在等错误已由模拟接口层弹窗显示
  }
}

// 打开操作弹窗：deposit / withdraw / transfer 三种类型共用一个弹窗
function openDialog(type) {
  dialogType.value = type
  form.amount = 100
  if (type === 'transfer') form.toAccountNo = ''
  dialogVisible.value = true
}

// 提交操作：按弹窗类型分别调用对应模拟接口
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
      await deposit(user.id, form.amount)
      ElMessage.success('存款成功')
    } else if (dialogType.value === 'withdraw') {
      await withdraw(user.id, form.amount)
      ElMessage.success('取款成功')
    } else {
      await transfer(user.id, { toAccountNo: form.toAccountNo.trim(), amount: form.amount })
      ElMessage.success('转账成功')
    }
    dialogVisible.value = false
    load()
  } catch {
    // 失败提示（如余额不足）已由模拟接口层弹窗显示
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
  border-bottom: 1px solid #efe8d8;
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  font-size: 16px;
  letter-spacing: 2px;
  font-family: Georgia, 'SimSun', serif;
  color: #b8860b;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.user-info :deep(.el-avatar) {
  background: #b8860b;
}
.username {
  font-size: 14px;
  color: #303133;
}
.main {
  background: #faf7ef;
  display: flex;
  justify-content: center;
  padding: 32px 20px;
}
.page-body {
  width: 100%;
  max-width: 920px;
}
/* 资产横幅：金色渐变 + 装饰圆环 */
.banner {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 24px;
  padding: 30px 36px;
  border-radius: 18px;
  background: linear-gradient(135deg, #8a6408 0%, #b8860b 100%);
  color: #fff;
  position: relative;
  overflow: hidden;
  box-shadow: 0 12px 30px rgba(138, 100, 8, 0.28);
}
.banner::before,
.banner::after {
  content: '';
  position: absolute;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.18);
}
.banner::before {
  width: 180px;
  height: 180px;
  top: -70px;
  left: 24%;
}
.banner::after {
  width: 120px;
  height: 120px;
  bottom: -50px;
  right: 26%;
}
.banner-left,
.banner-right {
  position: relative;
  z-index: 1;
}
.hello {
  font-size: 22px;
  font-weight: bold;
  letter-spacing: 2px;
}
.sub {
  margin-top: 8px;
  font-size: 13px;
  opacity: 0.85;
}
.balance-label {
  font-size: 13px;
  opacity: 0.85;
  letter-spacing: 1px;
  text-align: right;
}
.balance-value {
  font-size: 42px;
  font-weight: bold;
  font-family: Consolas, Monaco, monospace;
  letter-spacing: 1px;
  text-align: right;
  margin: 6px 0 10px;
}
.meta {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 10px;
}
.chip {
  padding: 3px 12px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.2);
  font-family: Consolas, Monaco, monospace;
  font-size: 12px;
  letter-spacing: 1px;
}
/* 三张操作卡片：仪表盘网格 */
.ops {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-top: 24px;
}
.op-card {
  background: #fff;
  border: 1px solid #efe8d8;
  border-radius: 14px;
  padding: 26px 24px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.op-card:hover {
  transform: translateY(-4px);
  border-color: #b8860b;
  box-shadow: 0 12px 26px rgba(138, 100, 8, 0.15);
}
.op-icon {
  width: 52px;
  height: 52px;
  border-radius: 14px;
  background: #f5ecd4;
  color: #b8860b;
  display: flex;
  align-items: center;
  justify-content: center;
}
.op-title {
  margin: 16px 0 8px;
  font-size: 17px;
  font-weight: bold;
  letter-spacing: 2px;
  color: #5c4406;
}
.op-desc {
  margin: 0 0 14px;
  font-size: 13px;
  color: #90885f;
  line-height: 1.6;
}
.op-go {
  font-size: 13px;
  font-weight: bold;
  color: #b8860b;
}
</style>
