import { useQuery } from '@tanstack/react-query'
import { getAssignments } from '@/features/assignments/api/assignmentsApi'

export function useGetAssignments(courseId: string) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['assignments', courseId],
    queryFn: () => getAssignments(courseId),
  })

  return { assignments: data ?? [], isLoading, isError }
}
