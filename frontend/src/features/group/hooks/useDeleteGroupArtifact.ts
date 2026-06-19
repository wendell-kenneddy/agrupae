import { useMutation, useQueryClient } from '@tanstack/react-query'
import { deleteGroupArtifact } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import { getErrorMessage } from '@/lib/error'

export function useDeleteGroupArtifact(courseId: string, assignmentId: string, groupId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (artifactId: string) =>
      deleteGroupArtifact(courseId, assignmentId, groupId, artifactId),
    onSuccess: () => {
      toast.success('Link removido com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['group-artifacts', courseId, assignmentId, groupId] })
    },
    onError: (error: unknown) => {
      const message = getErrorMessage(error)
      toast.error(message)
    },
  })

  return { deleteArtifact: mutateAsync, isLoading: isPending }
}
