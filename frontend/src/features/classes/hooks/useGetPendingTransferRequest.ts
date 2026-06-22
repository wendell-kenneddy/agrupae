import { useQuery } from '@tanstack/react-query'
import { getPendingTransferRequest } from '@/features/classes/api/classesApi'

export function useGetPendingTransferRequest(courseId: string) {
  const { data, isLoading, isError, refetch } = useQuery({
    queryKey: ['pending-transfer-request', courseId],
    queryFn: () => getPendingTransferRequest(courseId),
    retry: false,
  })

  return { pendingRequest: data, isLoading, isError, refetch }
}
