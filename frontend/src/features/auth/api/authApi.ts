import api from '@/lib/axios'
import type { LoginRequest, RegisterRequest, AuthResponse } from '@/features/auth/types/auth.types'

export async function login(data: LoginRequest): Promise<AuthResponse> {
  const response = await api.post<AuthResponse>('/login', data)
  return response.data
}

export async function register(data: RegisterRequest): Promise<void> {
  await api.post('/signup', data)
}
