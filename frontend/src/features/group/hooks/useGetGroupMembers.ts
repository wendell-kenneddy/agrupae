import { useQuery } from '@tanstack/react-query'
import { getGroupMembers } from '@/features/group/api/groupsApi'

export function useGetGroupMembers(courseId: string, assignmentId: string, groupId: string) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['group-members', courseId, assignmentId, groupId],
    queryFn: () => getGroupMembers(courseId, assignmentId, groupId),
    enabled: !!groupId && !!courseId && !!assignmentId,
  })

  return {
    members: data?.content ?? [],
    isLoading,
    isError,
  }
}
