import { useMutation, useQueryClient } from '@tanstack/react-query'
import { rejectGroupEntryRequest } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'

export function useRejectGroupEntryRequest(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: ({ groupId, requestId }: { groupId: string; requestId: string }) =>
      rejectGroupEntryRequest(courseId, assignmentId, groupId, requestId),
    onSuccess: (_, { groupId }) => {
      toast.success('Solicitação rejeitada com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
      queryClient.invalidateQueries({ queryKey: ['groupEntryRequests', courseId, assignmentId, groupId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      if (status === 422) {
        toast.error('Não foi possível rejeitar. Trabalho arquivado.')
      } else {
        toast.error('Erro ao rejeitar solicitação. Tente novamente.')
      }
    },
  })

  return { reject: mutateAsync, isLoading: isPending }
}
