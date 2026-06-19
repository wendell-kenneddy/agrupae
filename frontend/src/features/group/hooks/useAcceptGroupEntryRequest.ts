import { useMutation, useQueryClient } from '@tanstack/react-query'
import { acceptGroupEntryRequest } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'

export function useAcceptGroupEntryRequest(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: ({ groupId, requestId }: { groupId: string; requestId: string }) =>
      acceptGroupEntryRequest(courseId, assignmentId, groupId, requestId),
    onSuccess: (_, { groupId }) => {
      toast.success('Solicitação aceita com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
      queryClient.invalidateQueries({ queryKey: ['groupEntryRequests', courseId, assignmentId, groupId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      if (status === 409) {
        toast.error('Grupo já está cheio ou o estudante já está em outro grupo.')
      } else if (status === 422) {
        toast.error('Não foi possível aceitar. Trabalho arquivado.')
      } else {
        toast.error('Erro ao aceitar solicitação. Tente novamente.')
      }
    },
  })

  return { accept: mutateAsync, isLoading: isPending }
}
