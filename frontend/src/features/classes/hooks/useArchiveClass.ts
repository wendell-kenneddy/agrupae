import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { archiveClass } from '@/features/classes/api/classesApi'
import { toast } from '@/components/ui/useToast'

export function useArchiveClass(courseId: string) {
  const [isLoading, setIsLoading] = useState(false)
  const navigate = useNavigate()

  async function handleArchive() {
    setIsLoading(true)
    try {
      await archiveClass(courseId)
      toast.success('Turma arquivada com sucesso!')
      navigate('/home')
    } catch {
      toast.error('Erro ao arquivar turma. Tente novamente.')
    } finally {
      setIsLoading(false)
    }
  }

  return { handleArchive, isLoading }
}
