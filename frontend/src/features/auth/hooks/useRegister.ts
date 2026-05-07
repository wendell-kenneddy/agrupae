import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { register } from '@/features/auth/api/authApi'
import type { RegisterRequest } from '@/features/auth/types/auth.types'

export function useRegister() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  async function handleRegister(data: RegisterRequest) {
    setIsLoading(true)
    setError(null)

    try {
      await register(data)
      navigate('/login')
    } catch {
      setError('Error creating account. Try again.')
    } finally {
      setIsLoading(false)
    }
  }

  return { handleRegister, isLoading, error }
}
