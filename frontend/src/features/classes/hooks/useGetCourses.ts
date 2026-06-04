import { useQuery } from '@tanstack/react-query'
import { getCourses } from '@/features/classes/api/classesApi'
import { useAuth } from '@/app/providers/AuthContext'
import type { ClassRole } from '@/features/classes/types/classes.types'

export function useGetCourses() {
  const { user } = useAuth()

  const { data, isLoading, isError } = useQuery({
    queryKey: ['courses'],
    queryFn: getCourses,
    enabled: !!user,
  })

  const courses = (data ?? []).map((c) => ({
    ...c,
    role: (c.leaderId === user?.id ? 'OWNER' : 'STUDENT') as ClassRole,
  }))

  return { courses, isLoading, isError }
}
