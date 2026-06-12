import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { register, getMe } from '@/features/auth/api/authApi'
import { setAccessToken } from '@/lib/axios'
import { useAuth } from '@/app/providers/AuthContext'
import { toast } from '@/components/ui/useToast'
import type { RegisterRequest } from '@/features/auth/types/auth.types'

export function useRegister() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()
  const { setUser } = useAuth()

  async function handleRegister(data: RegisterRequest) {
    setIsLoading(true)
    setError(null)

    try {
      const token = await register(data)
      setAccessToken(token)
      const user = await getMe()
      setUser(user)
      toast.success('Conta criada com sucesso!')
      navigate('/home')
    } catch (err: unknown) {
      const status = (err as { response?: { status?: number } })?.response?.status
      if (status === 409) {
        setError('Este e-mail já está em uso. Tente outro.')
      } else {
        setError('Erro ao criar conta. Tente novamente.')
      }
    } finally {
      setIsLoading(false)
    }
  }

  return { handleRegister, isLoading, error }
}
