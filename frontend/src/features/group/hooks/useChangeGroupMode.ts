import { useMutation, useQueryClient } from '@tanstack/react-query'
import { changeGroupMode } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'

export function useChangeGroupMode(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: ({ groupId, open }: { groupId: string; open: boolean }) =>
      changeGroupMode(courseId, assignmentId, groupId, open),
    onSuccess: (_, variables) => {
      toast.success(
        variables.open ? 'Grupo alterado para Aberto!' : 'Grupo alterado para Fechado!'
      )
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      switch (status) {
        case 403:
          toast.error('Você não tem permissão para alterar o modo deste grupo.')
          break
        case 404:
          toast.error('Grupo ou trabalho não encontrado.')
          break
        case 422:
          toast.error('Não foi possível alterar o modo. Trabalho arquivado ou prazo encerrado.')
          break
        default:
          toast.error('Erro ao alterar o modo do grupo. Tente novamente.')
      }
    },
  })

  return { changeMode: mutateAsync, isLoading: isPending }
}
