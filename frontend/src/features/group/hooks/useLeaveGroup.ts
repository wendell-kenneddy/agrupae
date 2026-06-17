import { useMutation, useQueryClient } from '@tanstack/react-query'
import { leaveGroup } from '@/features/group/api/groupsApi'
import { toast } from '@/components/ui/useToast'
import { getErrorMessage } from '@/lib/error'

export function useLeaveGroup(courseId: string, assignmentId: string) {
  const queryClient = useQueryClient()

  const { mutateAsync, isPending } = useMutation({
    mutationFn: (groupId: string) => leaveGroup(courseId, assignmentId, groupId),
    onSuccess: () => {
      toast.success('Você saiu do grupo com sucesso!')
      queryClient.invalidateQueries({ queryKey: ['groups', courseId, assignmentId] })
      queryClient.invalidateQueries({ queryKey: ['my-entry-requests', courseId, assignmentId] })
    },
    onError: (error: unknown) => {
      const message = getErrorMessage(error)
      toast.error(message)
    },
  })

  return { leave: mutateAsync, isLoading: isPending }
}
