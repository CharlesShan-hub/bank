<template>
  <!-- 选做 2.1 要求：用户管理——横幅页头 + 搜索 + 抽屉式编辑，区别于常规的卡片+弹窗布局 -->
  <div class="user-page">
    <!-- 页头横幅：标题、统计与用户名搜索 -->
    <div class="page-banner">
      <div class="banner-text">
        <h2>用户管理</h2>
        <p>共 {{ filtered.length }} 位用户 · 平台全部注册账号一览</p>
      </div>
      <el-input v-model="keyword" placeholder="搜索用户名" :prefix-icon="Search" clearable class="banner-search" />
    </div>

    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="filtered" stripe class="user-table">
        <el-table-column label="用户" min-width="200">
          <template #default="{ row }">
            <div class="user-cell">
              <el-avatar :size="34" class="user-avatar">{{ row.username.charAt(0).toUpperCase() }}</el-avatar>
              <div class="user-meta">
                <span class="user-name">{{ row.username }}</span>
                <span class="user-id">ID {{ row.id }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="130">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'info'" effect="dark" round>
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="190">
          <template #default="{ row }">
            <span class="time">{{ fmt(row.createdAt) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑抽屉：从右侧滑出，回填当前行数据 -->
    <el-drawer v-model="drawerVisible" title="编辑用户信息" size="400px" destroy-on-close>
      <div class="drawer-body">
        <div class="drawer-head">
          <el-avatar :size="60" class="user-avatar">{{ editForm.username?.charAt(0).toUpperCase() }}</el-avatar>
          <span class="drawer-id">ID {{ editForm.id }}</span>
        </div>
        <el-form :model="editForm" label-position="top" size="large">
          <el-form-item label="用户名">
            <el-input v-model="editForm.username" :prefix-icon="User" />
          </el-form-item>
          <el-form-item label="密码">
            <el-input v-model="editForm.password" type="password" show-password :prefix-icon="Lock" placeholder="留空表示不修改" />
          </el-form-item>
          <el-button class="save-btn" type="primary" :loading="saving" @click="onSave">保存修改</el-button>
        </el-form>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, User, Lock, Edit, Delete } from '@element-plus/icons-vue'
import request from '../api/request'

// 表格数据、搜索关键词与编辑抽屉状态
const users = ref([])
const keyword = ref('')
const loading = ref(false)
const drawerVisible = ref(false)
const saving = ref(false)
// 编辑表单：密码留空表示「不修改密码」
const editForm = reactive({ id: null, username: '', password: '' })

// 按用户名过滤（前端本地过滤，不额外请求）
const filtered = computed(() =>
  keyword.value.trim()
    ? users.value.filter((u) => u.username.includes(keyword.value.trim()))
    : users.value
)

// 时间显示格式：把后端的 ISO 串转成「yyyy-MM-dd HH:mm:ss」
function fmt(iso) {
  return String(iso || '').replace('T', ' ').slice(0, 19)
}

// 拉取用户列表：GET /api/users（进入页面时自动调用）
async function load() {
  loading.value = true
  try {
    users.value = await request.get('/users')
  } finally {
    loading.value = false
  }
}

// 打开编辑抽屉：回填当前行数据
function openEdit(row) {
  Object.assign(editForm, { id: row.id, username: row.username, password: '' })
  drawerVisible.value = true
}

// 保存修改：PUT /api/users/{id}，成功后刷新列表并关闭抽屉
async function onSave() {
  if (!editForm.username.trim()) {
    ElMessage.warning('用户名不能为空')
    return
  }
  saving.value = true
  try {
    await request.put(`/users/${editForm.id}`, { username: editForm.username, password: editForm.password })
    ElMessage.success('修改成功')
    drawerVisible.value = false
    load()
  } finally {
    saving.value = false
  }
}

// 删除用户：先弹确认框防误操作，确认后 DELETE /api/users/{id}
// 后端会保护 admin 并级联删除该用户名下的账户
async function onDelete(row) {
  await ElMessageBox.confirm(`确定删除用户「${row.username}」吗？`, '提示', { type: 'warning' })
  await request.delete(`/users/${row.id}`)
  ElMessage.success('删除成功')
  load()
}

// 组件挂载即拉取数据
onMounted(load)
</script>

<style scoped>
.user-page {
  max-width: 980px;
  margin: 0 auto;
}
/* 页头横幅：深红渐变 + 装饰圆环 */
.page-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding: 22px 28px;
  border-radius: 14px;
  background: linear-gradient(135deg, #6f1019 0%, #b01e28 100%);
  color: #fff;
  position: relative;
  overflow: hidden;
  margin-bottom: 18px;
}
.page-banner::after {
  content: '';
  position: absolute;
  width: 180px;
  height: 180px;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.15);
  top: -70px;
  right: 180px;
}
.banner-text {
  position: relative;
  z-index: 1;
}
.banner-text h2 {
  margin: 0 0 6px;
  font-family: Georgia, 'SimSun', serif;
  font-size: 22px;
  letter-spacing: 4px;
}
.banner-text p {
  margin: 0;
  font-size: 13px;
  opacity: 0.85;
}
.banner-search {
  width: 260px;
  position: relative;
  z-index: 1;
}
/* 表格：浅红表头 + 斑马纹 */
.table-card {
  border: 1px solid #efe4e4;
  border-radius: 14px;
  box-shadow: none !important;
}
.user-table {
  --el-table-row-hover-bg-color: #faf0f0;
  --el-table-border-color: #f0e6e6;
}
.user-table :deep(thead th) {
  background: #f7e7e8 !important;
  color: #8a1420 !important;
  font-weight: bold;
}
.user-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-avatar {
  background: #b01e28;
  color: #fff;
  font-weight: bold;
  flex-shrink: 0;
}
.user-meta {
  display: flex;
  flex-direction: column;
  line-height: 1.4;
}
.user-name {
  font-weight: 600;
  color: #303133;
}
.user-id {
  font-size: 12px;
  color: #b9a8a8;
}
.time {
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
  color: #6b6060;
}
/* 编辑抽屉 */
.drawer-body {
  padding: 0 6px;
}
.drawer-head {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 6px 0 18px;
  border-bottom: 1px dashed #eee0e0;
  margin-bottom: 20px;
}
.drawer-id {
  font-size: 12px;
  color: #b9a8a8;
  letter-spacing: 1px;
}
.save-btn {
  width: 100%;
  letter-spacing: 4px;
  margin-top: 6px;
}
</style>
