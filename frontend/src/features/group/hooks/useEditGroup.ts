import { useMutation, useQueryClient } from '@tanstack/react-query'
import { editGroup } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'

export function useEditGroup(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: ({ groupId, name }: { groupId: string; name: string }) =>
      editGroup(courseId, assignmentId, groupId, name),
    onSuccess: () => {
      toast.success('Nome do grupo atualizado com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      switch (status) {
        case 403:
          toast.error('Você não tem permissão para editar este grupo.')
          break
        case 404:
          toast.error('Grupo ou trabalho não encontrado.')
          break
        case 409:
          toast.error('Já existe um grupo com este nome neste trabalho.')
          break
        case 422:
          toast.error('Não foi possível editar. Trabalho arquivado ou prazo encerrado.')
          break
        default:
          toast.error('Erro ao editar o grupo. Tente novamente.')
      }
    },
  })

  return { edit: mutateAsync, isLoading: isPending }
}
