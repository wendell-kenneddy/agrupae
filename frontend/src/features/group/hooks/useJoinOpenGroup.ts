import { useMutation, useQueryClient } from '@tanstack/react-query'
import { joinOpenGroup } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'

export function useJoinOpenGroup(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (groupId: string) => joinOpenGroup(courseId, assignmentId, groupId),
    onSuccess: () => {
      toast.success('Você entrou no grupo!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      switch (status) {
        case 409:
          toast.error('Você já pertence a um grupo neste trabalho ou o grupo está cheio.')
          break
        case 422:
          toast.error('Não foi possível entrar no grupo. Trabalho arquivado.')
          break
        default:
          toast.error('Erro ao entrar no grupo. Tente novamente.')
      }
    },
  })

  return { join: mutateAsync, isLoading: isPending }
}
