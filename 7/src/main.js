// 应用入口：创建 Vue 应用并全局完整引入 Element Plus
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn' // 中文语言包
import App from './App.vue'

const app = createApp(App)
app.use(ElementPlus, { locale: zhCn }) // 全局注册，所有页面可直接使用 el-* 组件
app.mount('#app') // 挂载到 index.html 的 #app 节点
