import { useQuery } from '@tanstack/react-query'
import { getArtifacts } from '@/features/assignments/api/assignmentsApi'

export function useGetArtifacts(courseId: string, assignmentId: string) {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['artifacts', assignmentId],
    queryFn: () => {
      return getArtifacts(courseId, assignmentId)
    },
  })

  return { artifacts: data ?? [], isLoading, isError }
}
