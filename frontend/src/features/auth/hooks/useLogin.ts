import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '@/features/auth/api/authApi'
import { setAccessToken } from '@/lib/axios'
import type { LoginRequest } from '@/features/auth/types/auth.types'

export function useLogin() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  async function handleLogin(data: LoginRequest) {
    setIsLoading(true)
    setError(null)

    try {
      const token = await login(data)
      setAccessToken(token)
      navigate('/home')
    } catch {
      setError('Email ou senha inválidos.')
    } finally {
      setIsLoading(false)
    }
  }

  return { handleLogin, isLoading, error }
}
