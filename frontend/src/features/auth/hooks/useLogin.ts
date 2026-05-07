import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login } from '@/features/auth/api/authApi'
import { useAuth } from '@/app/providers/AuthContext'
import type { LoginRequest } from '@/features/auth/types/auth.types'

export function useLogin() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const { setAccessToken } = useAuth()
  const navigate = useNavigate()

  async function handleLogin(data: LoginRequest) {
    setIsLoading(true)
    setError(null)

    try {
      const response = await login(data)
      sessionStorage.setItem('accessToken', response.accessToken)
      setAccessToken(response.accessToken)
      navigate('/home')
    } catch {
      setError('Invalid email or password.')
    } finally {
      setIsLoading(false)
    }
  }

  return { handleLogin, isLoading, error }
}
