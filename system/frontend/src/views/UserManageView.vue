<template>
  <!-- 选做 2.1 要求：基于 el-table 展示所有用户信息，每行提供编辑、删除按钮 -->
  <el-card class="table-card" shadow="never">
    <div class="card-header">
      <div class="card-title">
        <span>用户列表</span>
        <el-tag type="warning" effect="plain">共 {{ users.length }} 人</el-tag>
      </div>
      <el-button :icon="Refresh" circle size="small" @click="load" />
    </div>
    <el-table
      v-loading="loading"
      :data="users"
      :header-cell-style="{ background: '#b8860b', color: '#ffffff', fontWeight: 'bold' }"
      class="gold-table"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="用户名" min-width="140">
        <template #default="{ row }">
          <span class="cell-user"><el-icon><User /></el-icon>{{ row.username }}</span>
        </template>
      </el-table-column>
      <el-table-column label="角色" width="110">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'warning' : 'primary'" effect="light">
            {{ row.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="200" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" plain size="small" :icon="Edit" @click="openEdit(row)">编辑</el-button>
          <el-button type="danger" plain size="small" :icon="Delete" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>

  <el-dialog v-model="dialogVisible" title="编辑用户" width="420px" destroy-on-close>
    <el-form :model="editForm" label-width="80px">
      <el-form-item label="用户名"><el-input v-model="editForm.username" /></el-form-item>
      <el-form-item label="密码"><el-input v-model="editForm.password" placeholder="留空表示不修改" /></el-form-item>
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
import { Refresh, User, Edit, Delete } from '@element-plus/icons-vue'
import { listUsers, updateUser, deleteUser } from '../api/bank'

// 表格数据与编辑弹窗状态
const users = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
// 编辑表单：密码留空表示「不修改密码」
const editForm = reactive({ id: null, username: '', password: '' })

// 拉取用户列表：模拟接口 listUsers()（进入页面时自动调用）
async function load() {
  loading.value = true
  try {
    users.value = await listUsers()
  } finally {
    loading.value = false
  }
}

// 打开编辑弹窗：回填当前行数据
function openEdit(row) {
  Object.assign(editForm, { id: row.id, username: row.username, password: '' })
  dialogVisible.value = true
}

// 保存修改：调用 updateUser(id, form)，内部查重用户名，成功后刷新列表
async function onSave() {
  if (!editForm.username.trim()) {
    ElMessage.warning('用户名不能为空')
    return
  }
  saving.value = true
  try {
    await updateUser(editForm.id, { username: editForm.username, password: editForm.password })
    ElMessage.success('修改成功')
    dialogVisible.value = false
    load()
  } catch {
    // 失败提示（如用户名已存在）已由模拟接口层弹窗显示
  } finally {
    saving.value = false
  }
}

// 删除用户：先弹确认框防误操作，确认后调用 deleteUser(id)
// 接口内部会级联删除该用户名下的账户，且不允许删除 admin
async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
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
</style>
