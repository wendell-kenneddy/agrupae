import { useMutation, useQueryClient } from '@tanstack/react-query'
import { rejectTransferRequest } from '@/features/classes/api/classesApi'
import { toast } from '@/components/ui/useToast'

export function useRejectTransferRequest(courseId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (requestId: string) => rejectTransferRequest(courseId, requestId),
    onSuccess: () => {
      toast.success('Transferência de responsabilidade recusada.')
      queryClient.invalidateQueries({ queryKey: ['pending-transfer-request', courseId] })
    },
    onError: () => {
      toast.error('Erro ao recusar a transferência. Tente novamente.')
    },
  })

  return { reject: mutateAsync, isLoading: isPending }
}
