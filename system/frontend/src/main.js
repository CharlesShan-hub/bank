// 应用入口：创建 Vue 实例，装配 Element Plus 与路由后挂载到 #app
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css' // Element Plus 全量样式
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(ElementPlus) // 注册组件库：el-* 组件全局可用
app.use(router) // 注册 vue-router
app.mount('#app') // 挂载到 index.html 的 #app 节点
