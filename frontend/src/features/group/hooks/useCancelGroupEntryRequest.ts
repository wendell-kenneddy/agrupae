import { useMutation, useQueryClient } from '@tanstack/react-query'
import { cancelGroupEntryRequest } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'

export function useCancelGroupEntryRequest(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: ({ groupId, requestId }: { groupId: string; requestId: string }) =>
      cancelGroupEntryRequest(courseId, assignmentId, groupId, requestId),
    onSuccess: () => {
      toast.success('Solicitação cancelada.')
      queryClient.invalidateQueries({ queryKey: ['myEntryRequests', courseId, assignmentId] })
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      switch (status) {
        case 404:
          toast.error('Solicitação não encontrada.')
          break
        default:
          toast.error('Erro ao cancelar solicitação. Tente novamente.')
      }
    },
  })

  return { cancel: mutateAsync, isLoading: isPending }
}
