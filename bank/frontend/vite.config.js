// Vite 配置：前端跑在 7995 端口
// /api 开头的请求代理到 6995 的 Spring Boot 后端——前端只写相对路径，天然规避跨域
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 7995,
    proxy: {
      '/api': {
        target: 'http://localhost:6995',
        changeOrigin: true,
      },
    },
  },
})
