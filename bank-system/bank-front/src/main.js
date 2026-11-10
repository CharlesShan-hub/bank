// 应用入口：
// 创建 Vue 应用，注册路由与 Element Plus
// 组件库，最后挂载到页面
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn' // Element Plus 中文语言包
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(router) // 注册 vue-router，页面由 <router-view> 渲染
app.use(ElementPlus, { locale: zhCn }) // 注册 Element Plus（中文文案）
app.mount('#app') // 挂载到 index.html 的 #app 节点
