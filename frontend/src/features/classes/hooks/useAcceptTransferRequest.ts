import { useMutation, useQueryClient } from '@tanstack/react-query'
import { acceptTransferRequest } from '@/features/classes/api/classesApi'
import { toast } from '@/components/ui/useToast'

export function useAcceptTransferRequest(courseId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (requestId: string) => acceptTransferRequest(courseId, requestId),
    onSuccess: () => {
      toast.success('Transferência de responsabilidade aceita com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['pending-transfer-request', courseId] })
      queryClient.invalidateQueries({ queryKey: ['course', courseId] })
    },
    onError: () => {
      toast.error('Erro ao aceitar a transferência. Tente novamente.')
    },
  })

  return { accept: mutateAsync, isLoading: isPending }
}
