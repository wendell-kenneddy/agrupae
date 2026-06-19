import { useMutation, useQueryClient } from '@tanstack/react-query'
import { dissolveGroup } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'

export function useDissolveGroup(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (groupId: string) => dissolveGroup(courseId, assignmentId, groupId),
    onSuccess: () => {
      toast.success('Grupo dissolvido com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
      queryClient.invalidateQueries({ queryKey: ['my-entry-requests', courseId, assignmentId] })
    },
    onError: (error: AxiosError) => {
      const status = error.response?.status
      switch (status) {
        case 403:
          toast.error('Você não tem permissão para dissolver este grupo.')
          break
        case 404:
          toast.error('Grupo ou trabalho não encontrado.')
          break
        case 422:
          toast.error('Não foi possível dissolver o grupo. Trabalho arquivado ou prazo encerrado.')
          break
        default:
          toast.error('Erro ao dissolver o grupo. Tente novamente.')
      }
    },
  })

  return { dissolve: mutateAsync, isLoading: isPending }
}
