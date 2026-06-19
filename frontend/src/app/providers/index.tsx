/* eslint-disable react-refresh/only-export-components */
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { AuthProvider } from '@/app/providers/AuthContext'
import { Router } from '@/app/router'
import { ToastContainer } from '@/components/ui/Toast'

export const queryClient = new QueryClient()

export function Providers() {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <Router />
        <ToastContainer />
      </AuthProvider>
    </QueryClientProvider>
  )
}
