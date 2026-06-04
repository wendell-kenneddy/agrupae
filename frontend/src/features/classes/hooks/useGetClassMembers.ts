import { useQuery } from '@tanstack/react-query'
import { getClassMembers } from '@/features/classes/api/classesApi'

export function useGetClassMembers(courseId: string) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['course-members', courseId],
    queryFn: () => getClassMembers(courseId),
  })

  return { members: data ?? [], isLoading, isError }
}
