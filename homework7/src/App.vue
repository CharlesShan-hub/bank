<template>
  <!-- 实验(2)：el-container 布局容器（上下结构：顶部导航 + 下方左右分栏） -->
  <el-container class="page">
    <!-- 顶部栏：品牌 + 横向导航菜单 -->
    <el-header class="topbar" height="60px">
      <div class="brand">
        <el-icon :size="20"><Collection /></el-icon>
        <span>Element Plus 组件练习</span>
      </div>
      <!-- 实验(5)：el-menu 横向导航菜单 -->
      <el-menu mode="horizontal" default-active="商品管理" class="top-menu">
        <el-menu-item index="商品管理">
          <el-icon><ShoppingCart /></el-icon>商品管理
        </el-menu-item>
        <el-menu-item index="库存管理">
          <el-icon><Box /></el-icon>库存管理
        </el-menu-item>
        <el-menu-item index="分类管理">
          <el-icon><FolderOpened /></el-icon>分类管理
        </el-menu-item>
      </el-menu>
    </el-header>

    <el-container class="body">
      <!-- 左侧边栏：实验内容清单与当前菜单状态 -->
      <el-aside width="260px" class="aside">
        <el-card shadow="never" class="aside-card">
          <template #header>本次实验内容</template>
          <ul class="todo-list">
            <li>el-button 按钮（查询 / 圆形图标）</li>
            <li>el-container 页面整体布局</li>
            <li>el-table 图书数据表格</li>
            <li>el-form 学生信息登记表</li>
            <li>el-menu 导航菜单栏</li>
          </ul>
        </el-card>
      </el-aside>

      <!-- 主区域：用 el-tabs 分页承载两个案例 -->
      <el-main class="main">
        <el-tabs v-model="activeTab" type="border-card">
          <!-- 实验(1) + 实验(3)：按钮工具栏 + 图书表格 -->
          <el-tab-pane label="图书列表" name="books">
            <div class="toolbar">
              <!-- type="primary" 蓝色 + round 圆角 -->
              <el-button type="primary" round>
                <el-icon><Search /></el-icon>&nbsp;查 询
              </el-button>
              <!-- circle 圆形图标按钮 -->
              <el-button circle aria-label="搜索">
                <el-icon><Search /></el-icon>
              </el-button>
              <span class="tip">蓝色圆角「查询」按钮与搜索图标圆形按钮</span>
            </div>
            <el-table :data="books" stripe>
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column prop="name" label="书名" min-width="200" />
              <el-table-column prop="author" label="作者" width="170">
                <template #default="{ row }">
                  <el-tag effect="plain" type="success">{{ row.author }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="description" label="简介" min-width="300" />
            </el-table>
          </el-tab-pane>

          <!-- 实验(4)：学生信息登记表单 -->
          <el-tab-pane label="学生登记" name="student">
            <el-form ref="form" :model="form" :rules="rules" label-position="top" class="stu-form">
              <el-form-item label="学生姓名" prop="name">
                <el-input v-model="form.name" placeholder="请输入学生姓名" clearable />
              </el-form-item>
              <el-form-item label="手机号码" prop="phone">
                <el-input v-model="form.phone" placeholder="请输入 11 位手机号" maxlength="11" clearable />
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="form.gender">
                  <el-radio-button value="男">男</el-radio-button>
                  <el-radio-button value="女">女</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="兴趣爱好" prop="hobbies">
                <el-checkbox-group v-model="form.hobbies">
                  <el-checkbox-button v-for="h in hobbyList" :key="h" :value="h">{{ h }}</el-checkbox-button>
                </el-checkbox-group>
              </el-form-item>
              <el-form-item label="所在城市" prop="city">
                <el-select v-model="form.city" placeholder="请选择所在城市" style="width: 240px">
                  <el-option v-for="c in cityList" :key="c" :label="c" :value="c" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" round @click="submitForm">提交登记</el-button>
                <el-button round plain @click="resetForm">重置表单</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
// 选项式 API 写法：数据放 data()，逻辑放 methods，与组合式写法形成对比
export default {
  name: 'App',
  data() {
    return {
      activeTab: 'books',
      // 实验(3)：表格数据
      books: [
        { id: 1, name: 'Vue.js 从入门到实战', author: '张小明', description: '全面讲解 Vue 3 核心概念、组件开发、路由状态管理，通过电商项目实战掌握完整开发流程。' },
        { id: 2, name: 'JavaScript 高级程序设计', author: '尼古拉斯·泽卡斯', description: '前端开发者必读经典，深入讲解 JS 核心语法、面向对象、异步编程、DOM 操作等高级特性。' },
        { id: 3, name: 'CSS 权威指南', author: '埃里克·迈耶', description: '全面覆盖 CSS 选择器、盒模型、布局、动画、响应式设计，从基础到前沿技术一网打尽。' },
        { id: 4, name: 'React 进阶之路', author: '李明', description: '深入 React Hooks、Context、性能优化、服务端渲染，适合有一定基础的前端开发者。' },
        { id: 5, name: 'Node.js 后端开发实战', author: '王芳', description: '从零搭建 Node.js 服务，掌握 Express、数据库操作、身份认证、项目部署全流程。' },
        { id: 6, name: '前端工程化精讲', author: '陈磊', description: 'Webpack、Vite、Git、CI/CD、代码规范、自动化测试，打造高效前端开发工作流。' },
      ],
      // 实验(4)：选项数据、表单绑定与校验规则
      hobbyList: ['运动', '阅读', '音乐', '编程', '摄影'],
      cityList: ['北京', '上海', '广州', '深圳', '成都'],
      form: { name: '', phone: '', gender: '', hobbies: [], city: '' },
      rules: {
        name: [{ required: true, message: '学生姓名不能为空', trigger: 'blur' }],
        phone: [
          { required: true, message: '手机号码不能为空', trigger: 'blur' },
          {
            // 自定义校验器：1 开头的 11 位数字
            validator: (rule, value, callback) => {
              if (/^1\d{10}$/.test(value)) callback()
              else callback(new Error('手机号格式不正确'))
            },
            trigger: 'blur',
          },
        ],
        gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
        hobbies: [{ type: 'array', required: true, message: '请至少选择一个兴趣爱好', trigger: 'change' }],
        city: [{ required: true, message: '请选择所在城市', trigger: 'change' }],
      },
    }
  },
  methods: {
    // 提交：整表校验通过后提示成功并清空表单
    submitForm() {
      this.$refs.form.validate((valid) => {
        if (!valid) return
        this.$message.success(`登记成功：${this.form.name}（${this.form.city}）`)
        this.$refs.form.resetFields()
      })
    },
    // 重置：清空数据并移除校验状态
    resetForm() {
      this.$refs.form.resetFields()
    },
  },
}
</script>

<style>
html,
body,
#app {
  height: 100%;
  margin: 0;
}
body {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Microsoft YaHei', Arial, sans-serif;
  background: #f0f4f8;
}
.page {
  min-height: 100vh;
}
.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  box-shadow: 0 1px 4px rgba(6, 78, 89, 0.12);
}
.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: bold;
  color: #0f766e;
  letter-spacing: 1px;
}
.top-menu {
  border-bottom: none;
}
.body {
  padding: 16px;
  gap: 16px;
}
.aside-card .todo-list {
  margin: 0;
  padding-left: 18px;
  color: #475569;
  font-size: 13px;
  line-height: 2;
}
.main {
  padding: 0;
  background: transparent;
}
.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}
.tip {
  font-size: 13px;
  color: #94a3b8;
}
.stu-form {
  max-width: 520px;
  padding-top: 6px;
}
</style>
