import { useMutation, useQueryClient } from '@tanstack/react-query'
import { deleteArtifact } from '@/features/assignments/api/assignmentsApi'
import { toast } from '@/components/ui/useToast'
import type { AxiosError } from 'axios'
import { getErrorMessage } from '@/lib/error'

export function useDeleteArtifact(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (artifactId: string) => deleteArtifact(courseId, assignmentId, artifactId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['artifacts', assignmentId] })
      toast.success('Artefato excluído com sucesso!')
    },
    onError: (error: AxiosError) => {
      toast.error(getErrorMessage(error))
    },
  })

  return { remove: mutateAsync, isLoading: isPending }
}
