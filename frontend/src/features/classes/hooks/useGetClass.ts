import { useQuery } from '@tanstack/react-query'
import { getClass } from '@/features/classes/api/classesApi'
import { useAuth } from '@/app/providers/AuthContext'
import type { ClassRole } from '@/features/classes/types/classes.types'

export function useGetClass(id: string) {
  const { user } = useAuth()

  const { data, isLoading, isError } = useQuery({
    queryKey: ['course', id],
    queryFn: () => getClass(id),
    enabled: !!user,
  })

  const course = data
    ? {
        ...data,
        role: (data.leaderId === user?.id ? 'OWNER' : 'STUDENT') as ClassRole,
      }
    : undefined

  return { course, isLoading, isError }
}
