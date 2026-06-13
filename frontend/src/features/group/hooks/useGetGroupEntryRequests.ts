import { useQuery } from '@tanstack/react-query'
import { getGroupEntryRequests } from '@/features/group/api/groupsApi'
import type { GroupEntryRequestStatus } from '@/features/group/types/groups.types'

export function useGetGroupEntryRequests(
  courseId: string,
  assignmentId: string,
  groupId: string | undefined,
  status?: GroupEntryRequestStatus
) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['groupEntryRequests', courseId, assignmentId, groupId, status],
    queryFn: () => getGroupEntryRequests(courseId, assignmentId, groupId!, status),
    enabled: !!groupId,
  })

  return {
    requests: data ?? [],
    isLoading,
    isError,
  }
}
