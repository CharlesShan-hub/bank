<template>
  <div>
    <div class="page-card">
      <div class="card-header">
        <div>
          <h3>账户列表</h3>
          <p class="sub">共 {{ accounts.length }} 个账户</p>
        </div>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <!-- 选做 2.2 要求(2)：基于 el-table 显示所有账户信息，每行提供注销、编辑按钮 -->
      <el-table
        v-loading="loading"
        :data="accounts"
        border
        stripe
        :header-cell-style="headerStyle"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="所属用户" min-width="110">
          <template #default="{ row }">
            <span class="username">
              <el-icon :size="14"><User /></el-icon>{{ row.username }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="account_no" label="账号" width="170">
          <template #default="{ row }">
            <span class="mono">{{ row.account_no }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="140" align="right" sortable>
          <template #default="{ row }">
            <span class="money">¥ {{ Number(row.balance).toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '正常' ? 'success' : 'danger'" effect="light">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="开户时间" width="180" />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain :icon="Delete" @click="onClose(row)">注销</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑账户" width="440px" destroy-on-close>
      <el-form :model="editForm" label-width="70px">
        <el-form-item label="余额">
          <el-input-number v-model="editForm.balance" :min="0" :precision="2" style="width: 220px" />
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
        <el-button type="primary" @click="onSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Edit, Delete, User } from '@element-plus/icons-vue'
import request from '../api/request'

// 选做 2.2 要求(1)：本页对应左侧「账户管理」菜单项
// 路由为 /admin/accounts，仅 admin 可见（路由守卫保证）
const accounts = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editForm = reactive({ id: null, balance: 0, status: '正常' })

const headerStyle = { background: '#eef3f8', color: '#14447c', fontWeight: 600 }

// 拉取账户列表：GET /api/accounts
// 后端 JOIN users 带出所属用户名
// onMounted 时自动加载一次
async function load() {
  loading.value = true
  try {
    accounts.value = await request.get('/accounts')
  } finally {
    loading.value = false
  }
}
onMounted(load)

// 打开编辑弹窗：回填当前行的余额与状态
function openEdit(row) {
  Object.assign(editForm, { id: row.id, balance: row.balance, status: row.status })
  dialogVisible.value = true
}

// PUT /api/accounts/{id}
// 余额限制非负、保留两位小数（el-input-number）
// 状态下拉仅「正常 / 注销」两项
async function onSave() {
  await request.put(`/accounts/${editForm.id}`, {
    balance: editForm.balance,
    status: editForm.status,
  })
  ElMessage.success('修改成功')
  dialogVisible.value = false
  load()
}

// 注销前弹确认框，防止误操作
// 确认后 DELETE /api/accounts/{id}
// 后端软删除：仅把状态置为「注销」
function onClose(row) {
  ElMessageBox.confirm(`确定注销账户「${row.account_no}」吗？`, '提示', { type: 'warning' })
    .then(async () => {
      // 注销为软删除：仅把状态置为「注销」
      await request.delete(`/accounts/${row.id}`)
      ElMessage.success('注销成功')
      load()
    })
    .catch(() => {
      // 用户取消注销
    })
}
</script>

<style scoped>
.page-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 16px;
}
.card-header h3 {
  font-size: 17px;
  margin-bottom: 4px;
  color: #14447c;
  border-left: 4px solid #14589c;
  padding-left: 10px;
}
.sub {
  font-size: 12px;
  color: #909399;
}
.username {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}
.mono {
  font-family: Consolas, Menlo, monospace;
  letter-spacing: 0.5px;
}
.money {
  font-weight: 600;
  color: #14589c;
}
</style>
