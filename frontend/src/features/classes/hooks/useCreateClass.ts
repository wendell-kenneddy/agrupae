import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { createClass } from '@/features/classes/api/classesApi'
import type { CreateClassRequest } from '@/features/classes/types/classes.types'

export function useCreateClass() {
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const navigate = useNavigate()

  async function handleCreateClass(data: CreateClassRequest) {
    setIsLoading(true)
    setError(null)

    try {
      await createClass(data)
      navigate('/home')
    } catch {
      setError('Erro ao criar turma. Tente novamente.')
    } finally {
      setIsLoading(false)
    }
  }

  return { handleCreateClass, isLoading, error }
}
