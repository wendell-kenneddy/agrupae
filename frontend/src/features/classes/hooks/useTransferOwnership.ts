import { useMutation } from '@tanstack/react-query'
import { transferOwnership } from '@/features/classes/api/classesApi'
import { toast } from '@/components/ui/useToast'

export function useTransferOwnership(courseId: string) {
  const { mutateAsync, isPending } = useMutation({
    mutationFn: (newLeaderId: string) => transferOwnership(courseId, newLeaderId),
    onSuccess: () => {
      toast.success('Responsabilidade transferida com sucesso!')
    },
  })

  return { transfer: mutateAsync, isLoading: isPending }
}
