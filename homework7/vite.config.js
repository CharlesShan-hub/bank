import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 开发服务器使用 5174 端口，避免与本机其他 Vite 项目冲突
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5174,
  },
})
