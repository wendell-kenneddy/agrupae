import { useMutation, useQueryClient } from '@tanstack/react-query'
import { editArtifact } from '@/features/assignments/api/assignmentsApi'
import type { AddArtifactRequest } from '@/features/assignments/types/assignments.types'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'
import { getErrorMessage } from '@/lib/error'

export function useEditArtifact(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: ({ artifactId, data }: { artifactId: string; data: AddArtifactRequest }) =>
      editArtifact(courseId, assignmentId, artifactId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['artifacts', assignmentId] })
      toast.success('Artefato atualizado com sucesso!')
    },
    onError: (error: AxiosError) => {
      toast.error(getErrorMessage(error))
    },
  })

  return { edit: mutateAsync, isLoading: isPending }
}
