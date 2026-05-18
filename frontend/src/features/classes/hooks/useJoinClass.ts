import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { joinClass } from '@/features/classes/api/classesApi'
import type { JoinClassRequest } from '@/features/classes/types/classes.types'

export function useJoinClass() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  async function handleJoinClass(data: JoinClassRequest) {
    setIsLoading(true)
    setError(null)

    try {
      await joinClass(data)
      navigate('/home')
    } catch {
      setError('Código inválido ou expirado. Verifique e tente novamente.')
    } finally {
      setIsLoading(false)
    }
  }

  return { handleJoinClass, isLoading, error }
}
