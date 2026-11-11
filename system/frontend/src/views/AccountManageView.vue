<template>
  <!-- 选做 2.2 要求：账户列表展示，每行提供编辑、注销按钮
       （注销是软删除：仅把状态置为「注销」，不物理删除数据，防止信息丢失） -->
  <el-card class="table-card" shadow="never">
    <div class="card-header">
      <div class="card-title">
        <span>账户列表</span>
        <el-tag type="warning" effect="plain">共 {{ accounts.length }} 个账户</el-tag>
      </div>
      <el-button :icon="Refresh" circle size="small" @click="load" />
    </div>
    <el-table
      v-loading="loading"
      :data="accounts"
      :header-cell-style="{ background: '#b8860b', color: '#ffffff', fontWeight: 'bold' }"
      class="gold-table"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="用户名" min-width="120">
        <template #default="{ row }">
          <span class="cell-user"><el-icon><User /></el-icon>{{ row.username }}</span>
        </template>
      </el-table-column>
      <el-table-column label="账号" min-width="150">
        <template #default="{ row }">
          <span class="mono">{{ row.accountNo }}</span>
        </template>
      </el-table-column>
      <el-table-column label="余额" width="140" align="right" sortable :sort-method="byBalance">
        <template #default="{ row }">
          <span class="money">¥ {{ Number(row.balance).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === '正常' ? 'success' : 'danger'" effect="light">{{ row.status }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="开户时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" plain size="small" :icon="Edit" @click="openEdit(row)">编辑</el-button>
          <el-button
            type="danger"
            plain
            size="small"
            :icon="CircleClose"
            :disabled="row.status === '注销'"
            @click="onClose(row)"
          >注销</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <!-- 编辑弹窗：可调整余额与状态 -->
  <el-dialog v-model="dialogVisible" title="编辑账户" width="420px" destroy-on-close>
    <el-form :model="editForm" label-width="80px">
      <el-form-item label="余额">
        <el-input-number v-model="editForm.balance" :min="0" :max="100000000" :precision="2" :step="100" style="width: 220px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="editForm.status" style="width: 220px">
          <el-option label="正常" value="正常" />
          <el-option label="注销" value="注销" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="saving" @click="onSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, User, Edit, Delete, CircleClose } from '@element-plus/icons-vue'
import { listAccounts, updateAccount, closeAccount } from '../api/bank'

// 账户列表数据（用户名列由接口层联查返回）
const accounts = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
// 编辑表单：余额非负、状态二选一
const editForm = reactive({ id: null, balance: 0, status: '正常' })

// 拉取账户列表：模拟接口 listAccounts()（进入页面时自动调用）
async function load() {
  loading.value = true
  try {
    accounts.value = await listAccounts()
  } finally {
    loading.value = false
  }
}

// 余额列的自定义排序
function byBalance(a, b) {
  return Number(a.balance) - Number(b.balance)
}

// 打开编辑弹窗：回填当前行数据
function openEdit(row) {
  Object.assign(editForm, { id: row.id, balance: Number(row.balance), status: row.status })
  dialogVisible.value = true
}

// 保存修改：调用 updateAccount(id, form)，成功后刷新列表
async function onSave() {
  saving.value = true
  try {
    await updateAccount(editForm.id, { balance: editForm.balance, status: editForm.status })
    ElMessage.success('修改成功')
    dialogVisible.value = false
    load()
  } catch {
    // 失败提示（如余额不能为负）已由模拟接口层弹窗显示
  } finally {
    saving.value = false
  }
}

// 注销账户：先弹确认框说明「注销不是删除」，确认后调用 closeAccount(id)
// 接口内部仅把状态置为「注销」——用户仍然存在，账户信息保留防止丢失
async function onClose(row) {
  await ElMessageBox.confirm(
    `确定注销账户「${row.accountNo}」吗？注销后账户不可存取转，但信息会保留。`,
    '提示',
    { type: 'warning' }
  )
  await closeAccount(row.id)
  ElMessage.success('注销成功')
  load()
}

// 组件挂载即拉取数据
onMounted(load)
</script>

<style scoped>
.table-card {
  border: 1px solid #efe8d8;
  border-radius: 12px;
  box-shadow: none !important;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.card-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-family: Georgia, 'SimSun', serif;
  font-size: 17px;
  font-weight: bold;
  letter-spacing: 2px;
  color: #b8860b;
  border-left: 4px solid #b8860b;
  padding-left: 12px;
}
.gold-table {
  --el-table-row-hover-bg-color: #faf5e6;
  --el-table-border-color: #f0e9d8;
}
.cell-user {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}
.mono {
  font-family: Consolas, Monaco, monospace;
  letter-spacing: 1px;
}
.money {
  color: #b8860b;
  font-weight: bold;
  font-family: Consolas, Monaco, monospace;
}
</style>
