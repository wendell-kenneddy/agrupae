import { useQuery } from '@tanstack/react-query'
import { getMyEntryRequests } from '@/features/group/api/groupsApi'

export function useGetMyEntryRequests(courseId: string, assignmentId: string) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['myEntryRequests', courseId, assignmentId],
    queryFn: () => getMyEntryRequests(courseId, assignmentId),
  })

  return {
    myRequests: data ?? [],
    isLoading,
    isError,
  }
}
