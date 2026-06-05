import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { archiveClass } from '@/features/classes/api/classesApi'

export function useArchiveClass(courseId: string) {
  const [isLoading, setIsLoading] = useState(false)
  const navigate = useNavigate()

  async function handleArchive() {
    setIsLoading(true)
    try {
      await archiveClass(courseId)
      navigate('/home')
    } catch {
      // tratar erro depois
    } finally {
      setIsLoading(false)
    }
  }

  return { handleArchive, isLoading }
}
