import { useMutation } from '@tanstack/react-query'
import { editAssignment } from '@/features/assignments/api/assignmentsApi'
import { toast } from '@/components/ui/useToast'
import type { CreateAssignmentRequest } from '@/features/assignments/types/assignments.types'
import type { AxiosError } from 'axios'
import { getErrorMessage } from '@/lib/error'

export function useEditAssignment(courseId: string, assignmentId: string) {
  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: CreateAssignmentRequest) => editAssignment(courseId, assignmentId, data),
    onSuccess: () => {
      toast.success('Trabalho editado com sucesso!')
    },
    onError: (error: AxiosError) => {
      toast.error(getErrorMessage(error))
    },
  })

  return { edit: mutateAsync, isLoading: isPending }
}
