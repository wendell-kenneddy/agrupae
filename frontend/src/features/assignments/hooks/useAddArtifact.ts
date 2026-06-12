import { useMutation, useQueryClient } from '@tanstack/react-query'
import { addArtifact } from '@/features/assignments/api/assignmentsApi'
import type { AddArtifactRequest } from '@/features/assignments/types/assignments.types'

export function useAddArtifact(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: AddArtifactRequest) => addArtifact(courseId, assignmentId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['artifacts', assignmentId] })
    },
  })

  return { add: mutateAsync, isLoading: isPending }
}
