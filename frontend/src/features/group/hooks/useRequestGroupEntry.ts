import { useMutation, useQueryClient } from '@tanstack/react-query'
import { requestGroupEntry } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'

export function useRequestGroupEntry(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (groupId: string) => requestGroupEntry(courseId, assignmentId, groupId),
    onSuccess: () => {
      toast.success('Solicitação enviada!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
      queryClient.invalidateQueries({ queryKey: ['myEntryRequests', courseId, assignmentId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      switch (status) {
        case 409:
          toast.error('Você já tem uma solicitação pendente ou já pertence a um grupo.')
          break
        case 422:
          toast.error('Não foi possível solicitar entrada. Trabalho arquivado.')
          break
        default:
          toast.error('Erro ao solicitar entrada. Tente novamente.')
      }
    },
  })

  return { requestEntry: mutateAsync, isLoading: isPending }
}
