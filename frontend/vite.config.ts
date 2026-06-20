import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

const apiTarget = process.env.VITE_API_URL || 'http://localhost:8081'

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: { '@': path.resolve(__dirname, './src') },
  },
  server: {
    proxy: {
      '/auth': {
        target: apiTarget,
        changeOrigin: true,
        secure: false,
      },
      '/users': {
        target: apiTarget,
        changeOrigin: true,
        secure: false,
      },
      '/courses/join': {
        target: apiTarget,
        changeOrigin: true,
        secure: false,
      },
      '/courses': {
        target: apiTarget,
        changeOrigin: true,
        secure: false,
      },
    },
  },
})
