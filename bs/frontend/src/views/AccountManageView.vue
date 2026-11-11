<template>
  <!-- 选做 2.2(1) 要求：账户列表展示（本页只读，不含注销/编辑按钮） -->
  <el-card class="table-card" shadow="never">
    <div class="card-header">
      <div class="card-title">
        <span>账户列表</span>
        <el-tag type="success" effect="plain">共 {{ accounts.length }} 个账户</el-tag>
      </div>
      <el-button :icon="Refresh" circle size="small" @click="load" />
    </div>
    <el-table
      v-loading="loading"
      :data="accounts"
      :header-cell-style="{ background: '#0f6b4f', color: '#ffffff', fontWeight: 'bold' }"
      class="green-table"
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
    </el-table>
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { Refresh, User } from '@element-plus/icons-vue'
import request from '../api/request'

// 账户列表数据（用户名列由后端联查返回）
const accounts = ref([])
const loading = ref(false)

// 拉取账户列表：GET /api/accounts（进入页面时自动调用）
async function load() {
  loading.value = true
  try {
    accounts.value = await request.get('/accounts')
  } finally {
    loading.value = false
  }
}

// 余额列的自定义排序：balance 以字符串返回，需转数字再比较
function byBalance(a, b) {
  return Number(a.balance) - Number(b.balance)
}

// 组件挂载即拉取数据
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
.mono {
  font-family: Consolas, Monaco, monospace;
  letter-spacing: 1px;
}
.money {
  color: #0f6b4f;
  font-weight: bold;
  font-family: Consolas, Monaco, monospace;
}
</style>
