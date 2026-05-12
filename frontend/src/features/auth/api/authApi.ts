import api from '@/lib/axios'
import type { LoginRequest, RegisterRequest } from '@/features/auth/types/auth.types'

export async function login(data: LoginRequest): Promise<string> {
  const response = await api.post<string>('/auth/login', data)
  return response.data
}

export async function register(data: RegisterRequest): Promise<string> {
  const response = await api.post<string>('/auth/signup', data)
  return response.data
}

export async function refresh(): Promise<string> {
  const response = await api.post<string>('/auth/refresh')
  return response.data
}

export async function logout(): Promise<void> {
  await api.post('/auth/logout')
}
