import { useQuery } from '@tanstack/react-query'
import { getAssignment } from '@/features/assignments/api/assignmentsApi'

export function useGetAssignment(courseId: string, assignmentId: string) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['assignment', assignmentId],
    queryFn: () => getAssignment(courseId, assignmentId),
  })

  return { assignment: data, isLoading, isError }
}
