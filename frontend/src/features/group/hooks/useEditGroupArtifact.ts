import { useMutation, useQueryClient } from '@tanstack/react-query'
import { editGroupArtifact } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import { getErrorMessage } from '@/lib/error'
import type { EditGroupArtifactRequest } from '@/features/group/types/groups.types'

export function useEditGroupArtifact(courseId: string, assignmentId: string, groupId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: ({ artifactId, data }: { artifactId: string; data: EditGroupArtifactRequest }) =>
      editGroupArtifact(courseId, assignmentId, groupId, artifactId, data),
    onSuccess: () => {
      toast.success('Link editado com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['group-artifacts', courseId, assignmentId, groupId] })
    },
    onError: (error: unknown) => {
      const message = getErrorMessage(error)
      toast.error(message)
    },
  })

  return { editArtifact: mutateAsync, isLoading: isPending }
}
