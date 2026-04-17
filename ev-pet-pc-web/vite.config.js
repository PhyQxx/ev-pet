import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 9081,
    proxy: {
      '/api': {
        target: 'http://localhost:19081',
        changeOrigin: true
      }
    }
  }
})
