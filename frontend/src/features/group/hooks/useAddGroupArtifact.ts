import { useMutation, useQueryClient } from '@tanstack/react-query'
import { addGroupArtifact } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import { getErrorMessage } from '@/lib/error'
import type { AddGroupArtifactRequest } from '@/features/group/types/groups.types'

export function useAddGroupArtifact(courseId: string, assignmentId: string, groupId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (data: AddGroupArtifactRequest) =>
      addGroupArtifact(courseId, assignmentId, groupId, data),
    onSuccess: () => {
      toast.success('Link adicionado com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['group-artifacts', courseId, assignmentId, groupId] })
    },
    onError: (error: unknown) => {
      const message = getErrorMessage(error)
      toast.error(message)
    },
  })

  return { addArtifact: mutateAsync, isLoading: isPending }
}
