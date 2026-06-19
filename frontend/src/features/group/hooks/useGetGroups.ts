import { useQuery } from '@tanstack/react-query'
import { getGroups } from '@/features/group/api/groupsApi'

export function useGetGroups(courseId: string, assignmentId: string) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['groups', courseId, assignmentId],
    queryFn: () => getGroups(courseId, assignmentId),
  })

  return {
    groupsData: data,
    isLoading,
    isError,
  }
}
