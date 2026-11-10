import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 7997,
    proxy: {
      '/api': {
        target: 'http://localhost:6997',
        changeOrigin: true,
      },
    },
  },
})
