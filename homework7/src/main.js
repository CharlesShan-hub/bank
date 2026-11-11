// 应用入口：全局完整引入 Element Plus，并把图标注册为全局组件
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as Icons from '@element-plus/icons-vue'
import App from './App.vue'

const app = createApp(App)
app.use(ElementPlus, { locale: zhCn }) // 全局注册组件库
// 遍历注册所有图标组件，模板里可直接写 <Search /> 等
for (const [name, comp] of Object.entries(Icons)) {
  app.component(name, comp)
}
app.mount('#app')
