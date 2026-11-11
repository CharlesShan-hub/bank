import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// Vite 构建配置：入口插件 + 开发服务器端口
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173,
  },
})
