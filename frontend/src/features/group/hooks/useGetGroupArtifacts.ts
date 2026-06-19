import { useQuery } from '@tanstack/react-query'
import { getGroupArtifacts, getPublicGroupArtifacts } from '@/features/group/api/groupsApi'

export function useGetGroupArtifacts(
  courseId: string,
  assignmentId: string,
  groupId: string,
  isMember: boolean
) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['group-artifacts', courseId, assignmentId, groupId, isMember],
    queryFn: () =>
      isMember
        ? getGroupArtifacts(courseId, assignmentId, groupId)
        : getPublicGroupArtifacts(courseId, assignmentId, groupId),
    enabled: !!groupId && !!courseId && !!assignmentId,
  })

  return {
    artifacts: data ?? [],
    isLoading,
    isError,
  }
}
