import { useMutation, useQueryClient } from '@tanstack/react-query'
import { transferOwnership } from '@/features/classes/api/classesApi'
import { toast } from '@/components/ui/useToast'

export function useTransferOwnership(courseId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (newLeaderId: string) => transferOwnership(courseId, newLeaderId),
    onSuccess: () => {
      toast.success('Solicitação de transferência enviada com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['pending-transfer-request', courseId] })
    },
  })

  return { transfer: mutateAsync, isLoading: isPending }
}
