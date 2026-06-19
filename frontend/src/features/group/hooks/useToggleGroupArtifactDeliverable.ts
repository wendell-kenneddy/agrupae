import { useMutation, useQueryClient } from '@tanstack/react-query'
import { changeGroupArtifactDeliverableStatus, changeGroupArtifactPrivacy } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import { getErrorMessage } from '@/lib/error'

export function useToggleGroupArtifactDeliverable(courseId: string, assignmentId: string, groupId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: async ({ artifactId, deliverable }: { artifactId: string; deliverable: boolean }) => {
      await changeGroupArtifactDeliverableStatus(courseId, assignmentId, groupId, artifactId, deliverable)
      if (deliverable) {
        await changeGroupArtifactPrivacy(courseId, assignmentId, groupId, artifactId, false)
      }
    },
    onSuccess: (_, variables) => {
      toast.success(
        variables.deliverable
          ? 'Artefato marcado como entregável!'
          : 'Artefato desmarcado como entregável!'
      )
      queryClient.invalidateQueries({ queryKey: ['group-artifacts', courseId, assignmentId, groupId] })
    },
    onError: (error: unknown) => {
      const message = getErrorMessage(error)
      toast.error(message)
    },
  })

  return { toggleDeliverable: mutateAsync, isLoading: isPending }
}
