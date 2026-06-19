import { useMutation, useQueryClient } from '@tanstack/react-query'
import { createGroup } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { CreateGroupRequest } from '@/features/group/types/groups.types'
import type { AxiosError } from 'axios'

export function useCreateGroup(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: CreateGroupRequest) => createGroup(courseId, assignmentId, data),
    onSuccess: () => {
      toast.success('Grupo criado com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      switch (status) {
        case 403:
          toast.error('Criação de grupos não permitida neste trabalho.')
          break
        case 409:
          toast.error('Você já pertence a um grupo neste trabalho.')
          break
        case 422:
          toast.error('Não foi possível criar o grupo. Limite atingido ou trabalho arquivado.')
          break
        default:
          toast.error('Erro ao criar grupo. Tente novamente.')
      }
    },
  })

  return { create: mutateAsync, isLoading: isPending }
}
