import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { login, getMe } from '@/features/auth/api/authApi'
import { setAccessToken } from '@/lib/axios'
import { useAuth } from '@/app/providers/AuthContext'
import { toast } from '@/components/ui/useToast'
import type { LoginRequest } from '@/features/auth/types/auth.types'

export function useLogin() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()
  const { setUser } = useAuth()

  async function handleLogin(data: LoginRequest) {
    setIsLoading(true)
    setError(null)

    try {
      const token = await login(data)
      setAccessToken(token)
      const user = await getMe()
      setUser(user)
      toast.success('Login realizado com sucesso!')
      navigate('/home')
    } catch {
      setError('Email ou senha inválidos.')
    } finally {
      setIsLoading(false)
    }
  }

  return { handleLogin, isLoading, error }
}
