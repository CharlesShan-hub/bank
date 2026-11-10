<template>
  <div>
    <div class="page-card">
      <div class="card-header">
        <div>
          <h3>用户列表</h3>
          <p class="sub">共 {{ users.length }} 位注册用户</p>
        </div>
        <el-button :icon="Refresh" @click="load">刷新</el-button>
      </div>
      <!-- el-table 显示所有用户信息，每行提供删除、编辑按钮 -->
      <el-table
        v-loading="loading"
        :data="users"
        border
        stripe
        :header-cell-style="headerStyle"
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" min-width="120">
          <template #default="{ row }">
            <span class="username">
              <el-icon :size="14"><User /></el-icon>{{ row.username }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="110">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'info'" effect="light">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="created_at" label="注册时间" width="180" />
        <el-table-column label="操作" width="170" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" plain :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" plain :icon="Delete" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="编辑用户" width="440px" destroy-on-close>
      <el-form :model="editForm" label-width="70px">
        <el-form-item label="用户名">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="editForm.password" type="password" show-password placeholder="留空表示不修改" />
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

const users = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const editForm = reactive({ id: null, username: '', password: '' })

const headerStyle = { background: '#eef3f8', color: '#14447c', fontWeight: 600 }

async function load() {
  loading.value = true
  try {
    users.value = await request.get('/users')
  } finally {
    loading.value = false
  }
}
onMounted(load)

function openEdit(row) {
  Object.assign(editForm, { id: row.id, username: row.username, password: '' })
  dialogVisible.value = true
}

async function onSave() {
  if (!editForm.username) {
    ElMessage.warning('用户名不能为空')
    return
  }
  await request.put(`/users/${editForm.id}`, {
    username: editForm.username,
    ...(editForm.password ? { password: editForm.password } : {}),
  })
  ElMessage.success('修改成功')
  dialogVisible.value = false
  load()
}

function onDelete(row) {
  ElMessageBox.confirm(`确定删除用户「${row.username}」吗？其名下账户将一并删除。`, '提示', {
    type: 'warning',
  })
    .then(async () => {
      await request.delete(`/users/${row.id}`)
      ElMessage.success('删除成功')
      load()
    })
    .catch(() => {
      // 用户取消删除
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
</style>
