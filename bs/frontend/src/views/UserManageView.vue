<template>
  <el-card class="table-card" shadow="never">
    <div class="card-header">
      <div class="card-title">
        <span>用户列表</span>
        <el-tag type="success" effect="plain">共 {{ users.length }} 人</el-tag>
      </div>
      <el-button :icon="Refresh" circle size="small" @click="load" />
    </div>
    <el-table
      v-loading="loading"
      :data="users"
      :header-cell-style="{ background: '#0f6b4f', color: '#ffffff', fontWeight: 'bold' }"
      class="green-table"
    >
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column label="用户名" min-width="140">
        <template #default="{ row }">
          <span class="cell-user"><el-icon><User /></el-icon>{{ row.username }}</span>
        </template>
      </el-table-column>
      <el-table-column label="角色" width="110">
        <template #default="{ row }">
          <el-tag :type="row.role === 'admin' ? 'danger' : 'primary'" effect="light">
            {{ row.role === 'admin' ? '管理员' : '普通用户' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="注册时间" width="180" />
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
import request from '../api/request'

const users = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const saving = ref(false)
const editForm = reactive({ id: null, username: '', password: '' })

async function load() {
  loading.value = true
  try {
    users.value = await request.get('/users')
  } finally {
    loading.value = false
  }
}

function openEdit(row) {
  Object.assign(editForm, { id: row.id, username: row.username, password: '' })
  dialogVisible.value = true
}

async function onSave() {
  if (!editForm.username.trim()) {
    ElMessage.warning('用户名不能为空')
    return
  }
  saving.value = true
  try {
    await request.put(`/users/${editForm.id}`, { username: editForm.username, password: editForm.password })
    ElMessage.success('修改成功')
    dialogVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', { type: 'warning' })
  await request.delete(`/users/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.table-card {
  border: 1px solid #e4e9e6;
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
  color: #0f6b4f;
  border-left: 4px solid #0f6b4f;
  padding-left: 12px;
}
.green-table {
  --el-table-row-hover-bg-color: #eef6f2;
  --el-table-border-color: #eef0ea;
}
.cell-user {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
}
</style>
