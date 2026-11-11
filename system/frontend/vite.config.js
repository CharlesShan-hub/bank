// Vite 配置：前端跑在 7993 端口
// 纯前端项目：没有后端，数据全部缓存在浏览器 localStorage，无需配置代理
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 7993,
  },
})
