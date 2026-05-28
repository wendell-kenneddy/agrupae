import { useMutation } from '@tanstack/react-query'
import { transferOwnership } from '@/features/classes/api/classesApi'

export function useTransferOwnership(courseId: string) {
  const { mutateAsync, isPending } = useMutation({
    mutationFn: (newLeaderId: string) => transferOwnership(courseId, newLeaderId),
  })

  return { transfer: mutateAsync, isLoading: isPending }
}
