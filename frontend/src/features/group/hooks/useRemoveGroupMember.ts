import { useMutation, useQueryClient } from '@tanstack/react-query'
import { removeGroupMember } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import { getErrorMessage } from '@/lib/error'

interface RemoveMemberParams {
  groupId: string
  memberId: string
}

export function useRemoveGroupMember(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: ({ groupId, memberId }: RemoveMemberParams) =>
      removeGroupMember(courseId, assignmentId, groupId, memberId),
    onSuccess: (_, variables) => {
      toast.success('Membro removido com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
      queryClient.invalidateQueries({
        queryKey: ['group-members', courseId, assignmentId, variables.groupId],
      })
    },
    onError: (error: unknown) => {
      const message = getErrorMessage(error)
      toast.error(message)
    },
  })

  return { removeMember: mutateAsync, isLoading: isPending }
}
