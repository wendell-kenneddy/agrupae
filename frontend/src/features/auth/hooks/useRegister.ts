import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { register } from '@/features/auth/api/authApi'
import { setAccessToken } from '@/lib/axios'
import type { RegisterRequest } from '@/features/auth/types/auth.types'

export function useRegister() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  async function handleRegister(data: RegisterRequest) {
    setIsLoading(true)
    setError(null)

    try {
      const token = await register(data)
      setAccessToken(token)
      navigate('/home')
    } catch {
      setError('Erro ao criar conta. Tente novamente.')
    } finally {
      setIsLoading(false)
    }
  }

  return { handleRegister, isLoading, error }
}
