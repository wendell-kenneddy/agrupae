import { useMutation, useQueryClient } from '@tanstack/react-query'
import { addArtifact } from '@/features/assignments/api/assignmentsApi'
import type { AddArtifactRequest } from '@/features/assignments/types/assignments.types'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'
import { getErrorMessage } from '@/lib/error'

export function useAddArtifact(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: AddArtifactRequest) => addArtifact(courseId, assignmentId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['artifacts', assignmentId] })
    },
    onError: (error: AxiosError) => {
      toast.error(getErrorMessage(error))
    },
  })

  return { add: mutateAsync, isLoading: isPending }
}
