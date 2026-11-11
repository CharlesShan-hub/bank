<template>
  <!-- 实验(2)：el-container 完成整体布局（左侧边栏 / 顶部栏 / 主区域） -->
  <el-container class="layout">
    <!-- 左侧边栏：品牌区 + 导航菜单 -->
    <el-aside width="220px" class="aside">
      <div class="brand">
        <el-icon :size="22"><Platform /></el-icon>
        <span>组件实训平台</span>
      </div>
      <!-- 实验(5)：el-menu 竖直导航菜单，点击切换选中项 -->
      <el-menu
        default-active="goods"
        background-color="transparent"
        text-color="#a3b8ce"
        active-text-color="#ffffff"
        class="menu"
      >
        <el-menu-item index="goods">
          <el-icon><Goods /></el-icon>
          <span>商品管理</span>
        </el-menu-item>
        <el-menu-item index="stock">
          <el-icon><Box /></el-icon>
          <span>库存管理</span>
        </el-menu-item>
        <el-menu-item index="category">
          <el-icon><Files /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏：标题 + 实验标记 -->
      <el-header class="header">
        <span class="title">Element Plus 常用组件实训</span>
        <el-tag effect="dark" round>实验二</el-tag>
      </el-header>

      <!-- 主区域：三个案例卡片从上到下排列 -->
      <el-main class="main">
        <!-- 实验(1)：el-button 按钮组件 -->
        <el-card shadow="never" class="card">
          <template #header>案例一：el-button 按钮组件</template>
          <div class="btn-row">
            <!-- type="primary" 蓝色 + round 圆角 -->
            <el-button type="primary" round :icon="Search">查 询</el-button>
            <!-- circle 渲染为圆形图标按钮 -->
            <el-button type="primary" :icon="Search" circle aria-label="搜索" />
            <span class="tip">蓝色圆角「查询」按钮与搜索图标圆形按钮</span>
          </div>
        </el-card>

        <!-- 实验(3)：el-table 表格组件 -->
        <el-card shadow="never" class="card">
          <template #header>案例二：el-table 图书信息表</template>
          <el-table :data="books" border stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="书名" min-width="190" />
            <el-table-column prop="author" label="作者" width="150" />
            <el-table-column prop="description" label="简介" min-width="320" />
          </el-table>
        </el-card>

        <!-- 实验(4)：el-form 表单组件 -->
        <el-card shadow="never" class="card">
          <template #header>案例三：el-form 学生信息登记表</template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="stu-form">
            <el-form-item label="学生姓名" prop="name">
              <el-input v-model="form.name" placeholder="请输入学生姓名" />
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入 11 位手机号" maxlength="11" />
            </el-form-item>
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio value="男">男</el-radio>
                <el-radio value="女">女</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="兴趣爱好" prop="hobbies">
              <el-checkbox-group v-model="form.hobbies">
                <el-checkbox v-for="h in hobbyOptions" :key="h" :value="h">{{ h }}</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="所在城市" prop="city">
              <el-select v-model="form.city" placeholder="请选择所在城市" style="width: 220px">
                <el-option v-for="c in cityOptions" :key="c" :label="c" :value="c" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="onSubmit">提交</el-button>
              <el-button @click="onReset">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
// 组合式 API 写法：ref 定义引用，reactive 定义对象
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Platform, Goods, Box, Files } from '@element-plus/icons-vue'

// 实验(3)：表格数据
const books = [
  { id: 1, name: 'Vue.js 从入门到实战', author: '张小明', description: '全面讲解 Vue 3 核心概念、组件开发、路由状态管理，通过电商项目实战掌握完整开发流程。' },
  { id: 2, name: 'JavaScript 高级程序设计', author: '尼古拉斯·泽卡斯', description: '前端开发者必读经典，深入讲解 JS 核心语法、面向对象、异步编程、DOM 操作等高级特性。' },
  { id: 3, name: 'CSS 权威指南', author: '埃里克·迈耶', description: '全面覆盖 CSS 选择器、盒模型、布局、动画、响应式设计，从基础到前沿技术一网打尽。' },
  { id: 4, name: 'React 进阶之路', author: '李明', description: '深入 React Hooks、Context、性能优化、服务端渲染，适合有一定基础的前端开发者。' },
  { id: 5, name: 'Node.js 后端开发实战', author: '王芳', description: '从零搭建 Node.js 服务，掌握 Express、数据库操作、身份认证、项目部署全流程。' },
  { id: 6, name: '前端工程化精讲', author: '陈磊', description: 'Webpack、Vite、Git、CI/CD、代码规范、自动化测试，打造高效前端开发工作流。' },
]

// 实验(4)：表单数据、选项与校验规则
const formRef = ref()
const hobbyOptions = ['篮球', '阅读', '音乐', '编程', '旅行']
const cityOptions = ['北京', '上海', '广州', '深圳', '杭州']
const form = reactive({ name: '', phone: '', gender: '', hobbies: [], city: '' })
const rules = {
  name: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号须为 1 开头的 11 位数字', trigger: 'blur' },
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  hobbies: [{ type: 'array', required: true, message: '请选择兴趣爱好', trigger: 'change' }],
  city: [{ required: true, message: '请选择所在城市', trigger: 'change' }],
}

// 提交：整表校验通过后提示成功并清空表单
function onSubmit() {
  formRef.value.validate((valid) => {
    if (!valid) return
    ElMessage.success(`登记成功：${form.name}（${form.city}）`)
    formRef.value.resetFields()
  })
}

// 重置：清空数据并移除校验状态
function onReset() {
  formRef.value.resetFields()
}
</script>

<style scoped>
.layout {
  height: 100vh;
}
.aside {
  background: linear-gradient(180deg, #16324f 0%, #1d4e89 100%);
}
.brand {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-weight: bold;
  letter-spacing: 1px;
  background: rgba(255, 255, 255, 0.06);
}
.menu {
  border-right: none;
}
.menu :deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.18);
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.title {
  font-size: 16px;
  font-weight: 600;
  color: #1d4e89;
}
.main {
  background: #f4f6fb;
}
.card {
  margin-bottom: 16px;
  border-radius: 10px;
}
.btn-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.tip {
  font-size: 13px;
  color: #909399;
}
.stu-form {
  max-width: 560px;
}
</style>
